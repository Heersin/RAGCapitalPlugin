from __future__ import annotations

from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse

from .config import SETTINGS
from .evaluate import Evaluator
from .generate import Generator
from .ingest import Ingestor
from .retrieve import Retriever
from .schemas import (
    ChatRequest,
    ChatResponse,
    ChatSource,
    EvaluateRunRequest,
    EvaluateRunResponse,
    GenerateRequest,
    GenerateResponse,
    IngestRequest,
    IngestResponse,
    RetrieveRequest,
    RetrieveResponse,
    SelfCheckReport,
    VariantSummary,
)
from .storage import Storage
from .webui import CHAT_UI_HTML, build_chat_answer, source_cards_for_chat

app = FastAPI(title="RAGLocal Plugin Codegen", version="0.1.0")

storage = Storage(SETTINGS.db_path)
ingestor = Ingestor(SETTINGS, storage)
retriever = Retriever(SETTINGS, storage)
generator = Generator(SETTINGS, storage, retriever)
evaluator = Evaluator(generator)


@app.get("/health")
def health() -> dict:
    return {
        "status": "ok",
        "chunks": storage.get_chunk_count(),
        "symbols": storage.get_symbol_count(),
        "llm_enabled": generator.llm.enabled,
    }


@app.get("/", response_class=HTMLResponse)
def home() -> HTMLResponse:
    return HTMLResponse(CHAT_UI_HTML)


@app.post("/ingest", response_model=IngestResponse)
def ingest(req: IngestRequest) -> IngestResponse:
    try:
        result = ingestor.ingest(req.doc_root, rebuild=req.rebuild, enable_pdf=req.enable_pdf)
        return IngestResponse(**result)
    except Exception as err:
        raise HTTPException(status_code=400, detail=str(err)) from err


@app.post("/retrieve", response_model=RetrieveResponse)
def retrieve(req: RetrieveRequest) -> RetrieveResponse:
    try:
        res = retriever.retrieve(
            query=req.query,
            plugin_type_hint=req.plugin_type_hint,
            top_k=req.top_k,
            mode="hybrid",
        )
        return RetrieveResponse(
            plugin_type=res.plugin_type,
            evidence_cards=res.evidence_cards,
            trace=res.trace,
        )
    except Exception as err:
        raise HTTPException(status_code=400, detail=str(err)) from err


@app.post("/generate", response_model=GenerateResponse)
def generate(req: GenerateRequest) -> GenerateResponse:
    try:
        res = generator.generate(
            requirement=req.requirement,
            plugin_type=req.plugin_type,
            context_budget=req.context_budget,
            retrieval_mode="hybrid",
            self_check_enabled=True,
        )
        return GenerateResponse(
            plugin_type=res["plugin_type"],
            analysis=res["analysis"],
            code_blocks=res["code_blocks"],
            used_symbols=res["used_symbols"],
            self_check_report=SelfCheckReport(**res["self_check_report"]),
            evidence_cards=res["evidence_cards"],
            retrieval_trace=res["trace"],
            latency_seconds=res["latency_seconds"],
            used_remote_llm=res["used_remote_llm"],
            llm_trace=res["llm_trace"],
        )
    except Exception as err:
        raise HTTPException(status_code=400, detail=str(err)) from err


@app.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest) -> ChatResponse:
    try:
        if req.mode == "direct":
            res = generator.direct_chat(
                question=req.question,
                plugin_type=req.plugin_type,
            )
        else:
            res = generator.generate(
                requirement=req.question,
                plugin_type=req.plugin_type,
                context_budget=req.context_budget,
                retrieval_mode="hybrid",
                self_check_enabled=True,
            )
        return ChatResponse(
            mode=res.get("chat_mode", req.mode),
            plugin_type=res["plugin_type"],
            answer=res["raw_answer"] if res.get("chat_mode") == "direct" else build_chat_answer(res),
            analysis=res["analysis"],
            code_blocks=res["code_blocks"],
            used_symbols=res["used_symbols"],
            self_check_report=SelfCheckReport(**res["self_check_report"]),
            sources=[ChatSource(**item) for item in source_cards_for_chat(res["evidence_cards"])],
            retrieval_trace=res["trace"],
            latency_seconds=res["latency_seconds"],
            used_remote_llm=res["used_remote_llm"],
            llm_trace=res["llm_trace"],
        )
    except Exception as err:
        raise HTTPException(status_code=400, detail=str(err)) from err


@app.post("/evaluate/run", response_model=EvaluateRunResponse)
def evaluate_run(req: EvaluateRunRequest) -> EvaluateRunResponse:
    try:
        out = evaluator.run(
            experiment_id=req.experiment_id,
            task_set_id=req.task_set_id,
            variants=req.variants,
        )
        variant_results = {
            k: VariantSummary(
                retrieval_metrics=v["retrieval_metrics"],
                generation_metrics=v["generation_metrics"],
                industrial_metrics=v["industrial_metrics"],
                samples=v["samples"],
            )
            for k, v in out["variant_results"].items()
        }
        return EvaluateRunResponse(
            experiment_id=out["experiment_id"],
            task_set_id=out["task_set_id"],
            variant_results=variant_results,
            notes=out["notes"],
        )
    except Exception as err:
        raise HTTPException(status_code=400, detail=str(err)) from err
