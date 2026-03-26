from __future__ import annotations

from dataclasses import dataclass
import hashlib
import html
import re
from typing import Iterable, List, Literal, Sequence

import numpy as np


PluginType = Literal["action", "drc", "constraint"]


@dataclass(frozen=True)
class QueryProfile:
    raw_query: str
    retrieval_query: str
    query_tokens: List[str]
    expansions: List[str]
    subgoals: List[str]
    english_terms: List[str]
    api_terms: List[str]
    relation_terms: List[str]
    example_terms: List[str]
    target_symbols: List[str]
    access_path_terms: List[str]
    attribute_names: List[str]
    wants_examples: bool
    wants_api_docs: bool
    prefers_methods: bool
    read_only: bool
    output_window: bool


def strip_html(raw: str) -> str:
    no_script = re.sub(r"<script[\s\S]*?</script>", " ", raw, flags=re.IGNORECASE)
    no_style = re.sub(r"<style[\s\S]*?</style>", " ", no_script, flags=re.IGNORECASE)
    no_tag = re.sub(r"<[^>]+>", " ", no_style)
    unescaped = html.unescape(no_tag)
    return re.sub(r"\s+", " ", unescaped).strip()


def tokenize(text: str) -> List[str]:
    return re.findall(r"[A-Za-z_][A-Za-z0-9_\.]{0,80}", text)


def extract_symbols(text: str) -> List[str]:
    tokens = set()
    for m in re.findall(r"\bIX[A-Za-z0-9_]+\b", text):
        tokens.add(m)
    for m in re.findall(r"\bcom\.mentor\.[A-Za-z0-9_\.]+\b", text):
        tokens.add(m)
    return sorted(tokens)


def detect_plugin_type(query: str) -> tuple[PluginType, float]:
    q = query.lower()
    if re.search(r"\bdrc\b|design rule|违规|检查规则|检查", q):
        return "drc", 0.9
    if re.search(r"constraint|约束|routing|布线|放置", q):
        return "constraint", 0.85
    if re.search(r"action|菜单|按钮|输出窗口|context menu", q):
        return "action", 0.8
    return "action", 0.45


def build_query_profile(query: str, plugin_type: PluginType) -> QueryProfile:
    low = query.lower()
    expansions: List[str] = []

    wants_examples = bool(re.search(r"示例|样例|模板|生成|实现|代码|example|template|implement|generate", low))
    wants_api_docs = bool(re.search(r"接口|方法|签名|api|method|signature|javadoc|文档", low))
    prefers_methods = bool(re.search(r"方法|签名|枚举|method|signature|trigger|severity", low))
    read_only = bool(re.search(r"只读|read[\s\-]?only|readonly", low))
    output_window = bool(re.search(r"输出窗口|output window|println|print|打印|日志", low))
    explicit_api_terms = [tok for tok in tokenize(query) if tok.startswith("IX") or "." in tok]
    raw_tokens = tokenize(query)

    subgoals = [
        f"Confirm the primary {plugin_type} plugin interface and required lifecycle methods.",
        "Locate candidate APIs by interface name, method name, and description keywords.",
        "Inspect related classes, superinterfaces, and inherited methods one to two hops deep.",
        "Find implementation examples or templates that can be combined into the final answer.",
    ]

    plugin_expansions = {
        "action": [
            "custom action",
            "logic action",
            "IXAction",
            "IXLogicAction",
            "execute",
            "isAvailable",
            "isReadOnly",
            "getTriggers",
            "Trigger.MainMenu",
            "Trigger.ContextMenu",
        ],
        "drc": [
            "custom drc",
            "design rule check",
            "IXDRCheck",
            "IXLogicDRCheck",
            "IXDRCViolationReporter",
            "begin",
            "check",
            "end",
            "Severity",
        ],
        "constraint": [
            "custom constraint",
            "set attribute constraint",
            "constraint",
            "IXConstraint",
            "match",
            "result",
            "validate",
        ],
    }
    api_terms: List[str] = []
    english_terms = [plugin_type, "plugin api", "javadoc"]
    relation_terms = ["superinterface", "inherited methods", "extends", "implements", "related class"]
    example_terms = [plugin_type, "example", "template", "implementation"]
    target_symbols: List[str] = []
    access_path_terms: List[str] = []
    attribute_names: List[str] = []

    hint_patterns = [
        (
            r"只读|read[\s\-]?only|readonly",
            ["read only", "read-only plugin"],
            ["isReadOnly"],
            ["isReadOnly", "IXAvailability"],
            ["read only example"],
            "Confirm read-only semantics and availability checks.",
        ),
        (
            r"输出窗口|output window|println|print|打印|日志",
            ["output window", "print message", "logging"],
            ["IXOutputWindow", "println", "IXApplicationContext"],
            ["IXOutputWindow", "IXOutputPrinter"],
            ["output window example"],
            "Find APIs for writing messages to the output window.",
        ),
        (
            r"菜单|context menu|main menu|按钮|action",
            ["menu action", "context menu", "main menu"],
            ["Trigger.MainMenu", "Trigger.ContextMenu", "getTriggers"],
            ["IXAction", "IXBaseAction"],
            ["action template"],
            "Check how the plugin is exposed in the menu system.",
        ),
        (
            r"选中对象|selected object|selection",
            ["selected object", "selection context"],
            ["IXObject", "selection", "context"],
            ["IXComponentSelectionContext", "IXObject"],
            ["selection example"],
            "Locate APIs for reading the current selection or target object.",
        ),
        (
            r"属性|attribute|property",
            ["attribute", "property"],
            ["attribute", "property", "getAttribute"],
            ["IXAttribute", "IXObject"],
            ["attribute example"],
            "Locate APIs for reading or writing object attributes.",
        ),
        (
            r"检查|违规|rule|check",
            ["rule check", "validation"],
            ["check", "rule", "violation"],
            ["IXDRCheck", "Severity"],
            ["drc example"],
            "Inspect rule-checking interfaces, severities, and callbacks.",
        ),
        (
            r"线长|wire length",
            ["wire length"],
            ["IXWire", "wire length", "IXWireLengthResult"],
            ["IXWire", "IXRoute"],
            ["wire example"],
            "Find geometry or wire-length APIs related to the requirement.",
        ),
        (
            r"functiondesign|function design|功能设计|功能图",
            ["function design", "capture design"],
            ["IXFunctionDesign", "IXFunctionDesignAction"],
            ["IXDesign", "IXObject", "IXBaseCurrentContext"],
            ["function design action example"],
            "Locate the function-design specific action interface and the API path to the active or selected function design.",
        ),
        (
            r"示例|模板|实现|example|template|implement",
            ["example", "template", "implementation"],
            ["implementation"],
            ["related methods"],
            ["example", "template", "implementation"],
            "Look for example code that demonstrates the API combination.",
        ),
        (
            r"接口|签名|方法|api|signature|method",
            ["api", "method signature", "interface description"],
            ["API", "method", "signature", "description"],
            ["superinterface", "inherited methods"],
            ["api example"],
            "Prioritize interface and method documentation over broad guides.",
        ),
    ]

    for pattern, english, api, relation, example, subgoal in hint_patterns:
        if re.search(pattern, low):
            english_terms.extend(english)
            api_terms.extend(api)
            relation_terms.extend(relation)
            example_terms.extend(example)
            subgoals.append(subgoal)

    api_terms.extend(plugin_expansions.get(plugin_type, []))

    attr_match = re.search(r"(?:属性|attribute)(?:值| value)?\s*[\"'“”]?\s*([A-Z][A-Za-z0-9_]+)", query)
    if attr_match:
        attribute_names.append(attr_match.group(1))

    for token in raw_tokens:
        if token.startswith("IX"):
            target_symbols.append(token)
            continue
        if re.match(r"[A-Z][A-Za-z0-9]+Design$", token):
            inferred = "IX" + token
            target_symbols.append(inferred)
            api_terms.append(inferred)
            relation_terms.append(inferred)

    if re.search(r"属性值|attribute value|attribute", low):
        target_symbols.extend(["IXObject", "IXAttributes"])
        api_terms.extend(["getAttribute", "IXObject", "IXAttributes"])
        relation_terms.extend(["IXObject", "IXWriteableObject"])
        access_path_terms.extend(["getAttribute", "getAttributes"])
        subgoals.append("Find how the target object exposes attributes and whether the Name attribute is available directly.")

    if re.search(r"\bname\b|名称|名字", low):
        attribute_names.append("Name")
        api_terms.extend(["Name", "IXAttributes.Name"])

    if re.search(r"寻找|获取|拿到|obtain|get|selected|selection|当前|current", low):
        access_path_terms.extend(["getSelectedObjects", "getCurrentDesign", "getCurrentUser", "selection", "current"])

    if "IXFunctionDesign" in target_symbols or re.search(r"functiondesign|function design|功能设计|功能图", low):
        target_symbols.extend(["IXFunctionDesign", "IXFunctionDesignAction"])
        access_path_terms.extend(["getSelectedObjects", "selection", "filterClass", "IXBaseCurrentContext"])
        api_terms.extend(["IXBaseCurrentContext", "getSelectedObjects"])
        relation_terms.extend(["IXDesign", "IXObject", "IXBaseCurrentContext"])

    english_terms.extend(explicit_api_terms)
    api_terms.extend(explicit_api_terms)
    relation_terms.extend(explicit_api_terms)
    example_terms.extend(explicit_api_terms)

    def _dedupe(items: List[str]) -> List[str]:
        out: List[str] = []
        seen = set()
        for item in items:
            norm = item.strip()
            if not norm:
                continue
            key = norm.lower()
            if key in seen:
                continue
            seen.add(key)
            out.append(norm)
        return out

    english_terms = _dedupe(english_terms)
    api_terms = _dedupe(api_terms)
    relation_terms = _dedupe(relation_terms)
    example_terms = _dedupe(example_terms)
    subgoals = _dedupe(subgoals)
    target_symbols = _dedupe(target_symbols)
    access_path_terms = _dedupe(access_path_terms)
    attribute_names = _dedupe(attribute_names)

    expansions = _dedupe(
        english_terms + api_terms + relation_terms + example_terms + target_symbols + access_path_terms + attribute_names
    )

    retrieval_query = " ".join([query] + expansions)
    return QueryProfile(
        raw_query=query,
        retrieval_query=retrieval_query,
        query_tokens=tokenize(retrieval_query),
        expansions=expansions,
        subgoals=subgoals,
        english_terms=english_terms,
        api_terms=api_terms,
        relation_terms=relation_terms,
        example_terms=example_terms,
        target_symbols=target_symbols,
        access_path_terms=access_path_terms,
        attribute_names=attribute_names,
        wants_examples=wants_examples,
        wants_api_docs=wants_api_docs,
        prefers_methods=prefers_methods,
        read_only=read_only,
        output_window=output_window,
    )


def truncate_text(text: str, max_chars: int = 1200) -> str:
    if len(text) <= max_chars:
        return text
    return text[: max_chars - 3] + "..."


def keyword_overlap_score(query_tokens: Sequence[str], symbols: Iterable[str]) -> float:
    q = {t.lower() for t in query_tokens}
    s = {x.lower() for x in symbols}
    if not q or not s:
        return 0.0
    return len(q.intersection(s)) / max(1, len(q))


def hashed_dense_embed(text: str, dim: int = 256) -> np.ndarray:
    vec = np.zeros(dim, dtype=np.float32)
    tokens = tokenize(text)
    if not tokens:
        return vec

    for tok in tokens:
        digest = hashlib.sha256(tok.encode("utf-8")).digest()
        for i in range(dim):
            byte = digest[i % len(digest)]
            sign = 1.0 if ((byte >> (i % 8)) & 1) else -1.0
            vec[i] += sign

    norm = float(np.linalg.norm(vec))
    if norm > 0:
        vec /= norm
    return vec
