from __future__ import annotations

import re
import time
from typing import Dict, List, Optional, Tuple

from .config import Settings
from .llm import LLMClient, LLMConfig
from .prompts import DEVELOPER_PROMPT, SELF_CHECK_PROMPT, SYSTEM_PROMPT
from .retrieve import RetrievalMode, Retriever
from .storage import Storage
from .text_utils import detect_plugin_type, extract_symbols


class Generator:
    def __init__(self, settings: Settings, storage: Storage, retriever: Retriever):
        self.settings = settings
        self.storage = storage
        self.retriever = retriever
        self.llm = LLMClient(
            LLMConfig(
                base_url=settings.llm_base_url,
                api_key=settings.llm_api_key,
                model=settings.llm_model,
                timeout_seconds=settings.llm_timeout_seconds,
            )
        )

    def generate(
        self,
        requirement: str,
        plugin_type: Optional[str],
        context_budget: int = 12,
        retrieval_mode: RetrievalMode = "hybrid",
        self_check_enabled: bool = True,
    ) -> Dict:
        t0 = time.time()

        resolved_type = plugin_type
        if not resolved_type:
            resolved_type, _ = detect_plugin_type(requirement)

        evidence_cards = []
        trace = {}
        if retrieval_mode != "none":
            result = self.retriever.retrieve(
                query=requirement,
                plugin_type_hint=resolved_type,
                top_k=context_budget,
                mode=retrieval_mode,
            )
            resolved_type = result.plugin_type
            evidence_cards = result.evidence_cards
            trace = result.trace

        analysis = self._analyze_requirement(requirement, resolved_type)
        answer_text = self._generate_text(requirement, resolved_type, evidence_cards, analysis)

        report = self._self_check(answer_text, resolved_type)

        if self_check_enabled and self.llm.enabled and (report["invalid_symbols"] or report["missing_required_methods"]):
            revised = self._revise(answer_text, report, requirement, resolved_type, evidence_cards)
            if revised:
                answer_text = revised
                report = self._self_check(answer_text, resolved_type)
                report["fixed"] = not (report["invalid_symbols"] or report["missing_required_methods"])

        code_blocks = self._extract_code_blocks(answer_text)
        if not code_blocks:
            code_blocks = [answer_text]

        used_symbols = sorted(set(extract_symbols("\n".join(code_blocks))))

        return {
            "plugin_type": resolved_type,
            "analysis": analysis,
            "code_blocks": code_blocks,
            "used_symbols": used_symbols,
            "self_check_report": report,
            "evidence_cards": evidence_cards,
            "trace": trace,
            "latency_seconds": round(time.time() - t0, 3),
        }

    def _generate_text(
        self,
        requirement: str,
        plugin_type: str,
        evidence_cards: List[Dict],
        analysis: str,
    ) -> str:
        if not self.llm.enabled:
            return self._mock_generation(requirement, plugin_type, evidence_cards)

        ev = []
        for i, c in enumerate(evidence_cards, start=1):
            ev.append(
                f"[{i}] title={c['title']} source={c['source_path']} symbols={','.join(c['symbols'][:12])}\n{c['text']}"
            )

        user_prompt = (
            f"Requirement:\n{requirement}\n\n"
            f"Plugin type: {plugin_type}\n"
            f"Structured analysis:\n{analysis}\n\n"
            "Evidence:\n"
            + "\n\n".join(ev)
            + "\n\nOutput style: core class + template"
        )

        messages = [
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "system", "content": DEVELOPER_PROMPT},
            {"role": "user", "content": user_prompt},
        ]

        try:
            content, _usage = self.llm.chat(messages, temperature=0.1, max_tokens=1800)
            return content
        except Exception:
            return self._mock_generation(requirement, plugin_type, evidence_cards)

    def _revise(
        self,
        draft: str,
        report: Dict,
        requirement: str,
        plugin_type: str,
        evidence_cards: List[Dict],
    ) -> Optional[str]:
        ev = []
        for i, c in enumerate(evidence_cards, start=1):
            ev.append(f"[{i}] {c['title']} symbols={','.join(c['symbols'][:8])}")

        user_prompt = (
            f"Requirement:\n{requirement}\n\n"
            f"Plugin type: {plugin_type}\n"
            f"Validation report: {report}\n\n"
            "Evidence symbols:\n"
            + "\n".join(ev)
            + "\n\nDraft:\n"
            + draft
        )

        messages = [
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "system", "content": SELF_CHECK_PROMPT},
            {"role": "user", "content": user_prompt},
        ]

        try:
            content, _usage = self.llm.chat(messages, temperature=0.0, max_tokens=1800)
            return content
        except Exception:
            return None

    def _analyze_requirement(self, requirement: str, plugin_type: str) -> str:
        low = requirement.lower()
        read_only = "read-only" in low or "只读" in requirement
        trigger = "MainMenu/ContextMenu" if plugin_type == "action" else "N/A"
        risk = "API mismatch" if "ix" not in low else "Low"
        return (
            f"plugin_type={plugin_type}; "
            f"read_only={str(read_only).lower()}; "
            f"trigger={trigger}; "
            f"risk={risk}"
        )

    def _extract_code_blocks(self, text: str) -> List[str]:
        blocks = re.findall(r"```(?:java)?\n([\s\S]*?)```", text)
        return [b.strip() for b in blocks if b.strip()]

    def _self_check(self, answer_text: str, plugin_type: str) -> Dict:
        code = "\n".join(self._extract_code_blocks(answer_text)) or answer_text
        used_symbols = sorted(set(extract_symbols(code)))

        valid_symbols = set(self.storage.list_symbols([plugin_type, "core"]))
        invalid = sorted([s for s in used_symbols if s not in valid_symbols])

        required = self._required_methods(plugin_type)
        missing = []
        for m in required:
            if not re.search(rf"\b{re.escape(m)}\s*\(", code):
                missing.append(m)

        if plugin_type == "drc" and not re.search(r"implements\s+[^\{;]*DRCheck", code):
            missing.append("implements_IX*DRCheck")
        if plugin_type == "action" and not re.search(r"implements\s+[^\{;]*Action", code):
            missing.append("implements_IX*Action")
        if plugin_type == "constraint" and not re.search(r"implements\s+[^\{;]*Constraint", code):
            missing.append("implements_IX*Constraint")

        return {
            "invalid_symbols": invalid,
            "missing_required_methods": sorted(set(missing)),
            "fixed": False,
        }

    def _required_methods(self, plugin_type: str) -> List[str]:
        base = ["getName", "getDescription", "getVersion"]
        if plugin_type == "action":
            return base + ["execute", "isAvailable", "isReadOnly", "getTriggers"]
        if plugin_type == "drc":
            return base + ["begin", "check", "end", "getDefaultAvailability", "getDefaultSeverity"]
        return base

    def _mock_generation(self, requirement: str, plugin_type: str, evidence_cards: List[Dict]) -> str:
        if plugin_type == "action":
            return self._mock_action(requirement, evidence_cards)
        if plugin_type == "drc":
            return self._mock_drc(requirement, evidence_cards)
        return self._mock_constraint(requirement, evidence_cards)

    def _mock_action(self, requirement: str, evidence_cards: List[Dict]) -> str:
        return f"""## Requirement analysis
- type: action
- requirement: {requirement}

```java
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXLogicAction;

public class GeneratedAction implements IXLogicAction {{
    public GeneratedAction() {{}}

    public boolean execute(IXApplicationContext applicationContext) {{
        IXOutputWindow out = applicationContext.getOutputWindow();
        out.println("Generated action executed.");
        return true;
    }}

    public boolean isAvailable(IXApplicationContext applicationContext) {{ return true; }}
    public boolean isReadOnly() {{ return true; }}
    public Trigger[] getTriggers() {{ return new Trigger[]{{Trigger.MainMenu, Trigger.ContextMenu}}; }}
    public String getLongDescription() {{ return "Generated action"; }}
    public Integer getMnemonicKey() {{ return null; }}
    public javax.swing.Icon getSmallIcon() {{ return null; }}

    public String getDescription() {{ return "Generated action plugin"; }}
    public String getName() {{ return "Generated Action"; }}
    public String getVersion() {{ return "0.1"; }}
}}
```
"""

    def _mock_drc(self, requirement: str, evidence_cards: List[Dict]) -> str:
        return f"""## Requirement analysis
- type: drc
- requirement: {requirement}

```java
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXLogicDRCheck;

public class GeneratedDRC implements IXLogicDRCheck {{
    public GeneratedDRC() {{}}

    public void begin(IXDRCViolationReporter vReporter) {{}}
    public void check(IXDRCViolationReporter vReporter, IXObject xObject) {{
        // TODO: implement custom rule logic
    }}
    public void end(IXDRCViolationReporter vReporter) {{}}

    public boolean getDefaultAvailability() {{ return true; }}
    public Severity getDefaultSeverity() {{ return Severity.Warning; }}

    public String getDescription() {{ return "Generated DRC plugin"; }}
    public String getName() {{ return "Generated DRC"; }}
    public String getVersion() {{ return "0.1"; }}
}}
```
"""

    def _mock_constraint(self, requirement: str, evidence_cards: List[Dict]) -> str:
        return f"""## Requirement analysis
- type: constraint
- requirement: {requirement}

```java
import com.mentor.chs.plugin.constraint.IXDoDontResult;
import com.mentor.chs.plugin.constraint.IXSetAttributeConstraint;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.api.IXObject;

public class GeneratedConstraint implements IXSetAttributeConstraint {{
    public GeneratedConstraint() {{}}

    // TODO: replace with actual method required by selected constraint interface
    public void apply(IXApplicationContext context, IXObject object, IXDoDontResult result) {{
        // constraint logic
    }}

    public String getDescription() {{ return "Generated constraint plugin"; }}
    public String getName() {{ return "Generated Constraint"; }}
    public String getVersion() {{ return "0.1"; }}
}}
```
"""
