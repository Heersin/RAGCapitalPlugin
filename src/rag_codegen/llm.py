from __future__ import annotations

import json
import urllib.error
import urllib.request
from dataclasses import dataclass
from typing import Any, Dict, List, Tuple


@dataclass
class LLMConfig:
    base_url: str
    api_key: str
    model: str
    timeout_seconds: int = 60


class LLMClient:
    def __init__(self, cfg: LLMConfig):
        self.cfg = cfg

    @property
    def enabled(self) -> bool:
        return bool(self.cfg.base_url and self.cfg.api_key and self.cfg.model)

    def _endpoint(self) -> str:
        if self.cfg.base_url.endswith("/chat/completions"):
            return self.cfg.base_url
        return self.cfg.base_url.rstrip("/") + "/chat/completions"

    def chat(
        self,
        messages: List[Dict[str, str]],
        temperature: float = 0.1,
        max_tokens: int = 1800,
    ) -> Tuple[str, Dict[str, Any]]:
        payload = {
            "model": self.cfg.model,
            "messages": messages,
            "temperature": temperature,
            "max_tokens": max_tokens,
        }

        req = urllib.request.Request(
            url=self._endpoint(),
            method="POST",
            data=json.dumps(payload).encode("utf-8"),
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {self.cfg.api_key}",
            },
        )

        try:
            with urllib.request.urlopen(req, timeout=self.cfg.timeout_seconds) as resp:
                body = resp.read().decode("utf-8")
        except urllib.error.HTTPError as err:
            detail = err.read().decode("utf-8", errors="ignore")
            raise RuntimeError(f"LLM HTTP error {err.code}: {detail[:500]}") from err
        except Exception as err:
            raise RuntimeError(f"LLM request failed: {err}") from err

        parsed = json.loads(body)
        try:
            content = parsed["choices"][0]["message"]["content"]
        except Exception as err:
            raise RuntimeError(f"Unexpected LLM response: {body[:800]}") from err

        usage = parsed.get("usage", {})
        return content, usage
