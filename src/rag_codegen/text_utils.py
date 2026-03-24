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

    plugin_expansions = {
        "action": [
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
            "IXDRCheck",
            "IXLogicDRCheck",
            "IXDRCViolationReporter",
            "begin",
            "check",
            "end",
            "Severity",
        ],
        "constraint": [
            "constraint",
            "IXConstraint",
            "match",
            "result",
            "validate",
        ],
    }
    expansions.extend(plugin_expansions.get(plugin_type, []))

    hint_patterns = [
        (r"只读|read[\s\-]?only|readonly", ["read only", "isReadOnly"]),
        (r"输出窗口|output window|println|print|打印|日志", ["IXOutputWindow", "println", "output window"]),
        (r"菜单|context menu|main menu|按钮|action", ["Trigger.MainMenu", "Trigger.ContextMenu", "menu"]),
        (r"选中对象|selected object|selection", ["IXObject", "selected object", "selection"]),
        (r"属性|attribute|property", ["attribute", "property", "getAttribute"]),
        (r"检查|违规|rule|check", ["check", "rule"]),
        (r"线长|wire length", ["IXWire", "wire length", "IXWireLengthResult"]),
        (r"示例|模板|实现|example|template|implement", ["example", "template", "implementation"]),
        (r"接口|签名|方法|api|signature|method", ["API", "method", "signature"]),
    ]

    for pattern, terms in hint_patterns:
        if re.search(pattern, low):
            expansions.extend(terms)

    deduped: List[str] = []
    seen = set()
    for term in [query] + expansions:
        norm = term.strip()
        if not norm:
            continue
        key = norm.lower()
        if key in seen:
            continue
        seen.add(key)
        deduped.append(norm)

    retrieval_query = " ".join(deduped)
    return QueryProfile(
        raw_query=query,
        retrieval_query=retrieval_query,
        query_tokens=tokenize(retrieval_query),
        expansions=deduped[1:],
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
