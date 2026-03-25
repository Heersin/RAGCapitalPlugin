from __future__ import annotations

from typing import Any, Dict, List, Literal, Optional

from pydantic import BaseModel, Field


PluginType = Literal["action", "drc", "constraint"]


class IngestRequest(BaseModel):
    doc_root: str
    rebuild: bool = True
    enable_pdf: bool = False


class IngestResponse(BaseModel):
    doc_count: int
    chunk_count: int
    symbol_count: int
    status: str


class RetrieveRequest(BaseModel):
    query: str
    plugin_type_hint: Optional[PluginType] = None
    top_k: int = 12


class EvidenceCard(BaseModel):
    chunk_id: str
    source_type: str
    source_path: str
    plugin_type: str
    score: float
    title: str
    text: str
    symbols: List[str]


class RetrieveResponse(BaseModel):
    plugin_type: PluginType
    evidence_cards: List[EvidenceCard]
    trace: Dict[str, Any]


class GenerateRequest(BaseModel):
    requirement: str
    plugin_type: Optional[PluginType] = None
    context_budget: int = 12


class SelfCheckReport(BaseModel):
    invalid_symbols: List[str] = Field(default_factory=list)
    missing_required_methods: List[str] = Field(default_factory=list)
    fixed: bool = False


class GenerateResponse(BaseModel):
    plugin_type: PluginType
    analysis: str
    code_blocks: List[str]
    used_symbols: List[str]
    self_check_report: SelfCheckReport
    evidence_cards: List[EvidenceCard]
    latency_seconds: float
    used_remote_llm: bool = False


class ChatRequest(BaseModel):
    question: str
    plugin_type: Optional[PluginType] = None
    context_budget: int = 10


class ChatSource(BaseModel):
    title: str
    source_path: str
    plugin_type: str
    score: float


class ChatResponse(BaseModel):
    plugin_type: PluginType
    answer: str
    analysis: str
    code_blocks: List[str]
    used_symbols: List[str]
    self_check_report: SelfCheckReport
    sources: List[ChatSource]
    latency_seconds: float
    used_remote_llm: bool = False


class EvaluateRunRequest(BaseModel):
    experiment_id: str
    task_set_id: str
    variants: List[Literal["B0", "B1", "B2", "B3"]] = Field(
        default_factory=lambda: ["B0", "B1", "B2", "B3"]
    )


class VariantSummary(BaseModel):
    retrieval_metrics: Dict[str, float]
    generation_metrics: Dict[str, float]
    industrial_metrics: Dict[str, float]
    samples: int


class EvaluateRunResponse(BaseModel):
    experiment_id: str
    task_set_id: str
    variant_results: Dict[str, VariantSummary]
    notes: List[str] = Field(default_factory=list)
