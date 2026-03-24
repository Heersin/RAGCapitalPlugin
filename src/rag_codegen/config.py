from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class Settings:
    runtime_dir: Path = Path("runtime")
    db_path: Path = Path("runtime/raglocal.db")
    dense_index_path: Path = Path("runtime/dense_index.npz")
    dense_dim: int = 256

    llm_base_url: str = os.getenv("LLM_BASE_URL", "")
    llm_api_key: str = os.getenv("LLM_API_KEY", "")
    llm_model: str = os.getenv("LLM_MODEL", "")
    llm_timeout_seconds: int = int(os.getenv("LLM_TIMEOUT_SECONDS", "60"))

    input_top_k_default: int = 12
    rrf_k: int = 60


SETTINGS = Settings()
