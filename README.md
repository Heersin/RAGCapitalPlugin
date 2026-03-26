# RAGLocal Plugin Codegen

Local-first RAG service for plugin-code assistance. It ingests JavaDoc HTML, Java examples, and optional PDF guides, then serves a browser chat UI plus JSON APIs.

## Supported Scope
- Plugin types: `action`, `drc`, `constraint`
- Inputs: HTML JavaDoc, Java examples, optional PDF
- Outputs: evidence-backed answers, code suggestions, retrieval traces, LLM traces

## Endpoints
- `GET /`
- `GET /health`
- `POST /ingest`
- `POST /retrieve`
- `POST /generate`
- `POST /chat`
- `POST /evaluate/run`

## Quick Start
1. Install dependencies:

```bash
uv sync --extra dev
```

2. Start the service:

```bash
PYTHONPATH=src uv run uvicorn rag_codegen.app:app --host 0.0.0.0 --port 8000
```

3. Open the browser UI:

```text
http://localhost:8000/
```

4. Ingest documents:

```bash
curl -X POST http://localhost:8000/ingest \
  -H 'Content-Type: application/json' \
  -d '{"doc_root":"sample/plugin","rebuild":true,"enable_pdf":true}'
```

## Chat Modes

The web UI and `POST /chat` now support two modes:

- `rag`
  Retrieve local evidence first, then generate an answer grounded in the retrieved API/docs/examples.
- `direct`
  Skip retrieval and forward the current user message directly to the configured LLM.

Example `POST /chat` request:

```bash
curl -X POST http://localhost:8000/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "question":"生成一个只读 action，在输出窗口打印选中对象属性",
    "plugin_type":"action",
    "context_budget":10,
    "mode":"rag"
  }'
```

Direct mode example:

```bash
curl -X POST http://localhost:8000/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "question":"请直接帮我规划一个插件实现思路",
    "mode":"direct"
  }'
```

## Current Workflow
1. `POST /ingest`
   Parse HTML, Java, and optional PDF into chunks and symbols, write them into SQLite, then rebuild the dense index.
2. `POST /retrieve`
   Build a staged retrieval plan, search API docs first, expand related classes and methods, then supplement with examples.
3. `POST /generate`
   Run RAG generation and self-check, returning structured fields for automation.
4. `POST /chat`
   Return a UI-friendly answer for human use.
   `rag` mode uses retrieval + generation.
   `direct` mode sends only the current input to the LLM.
5. `POST /evaluate/run`
   Replay benchmark tasks from the evaluation dataset.

## AI Behavior
- External LLM is enabled only when all of these are set:
  - `LLM_BASE_URL`
  - `LLM_API_KEY`
  - `LLM_MODEL`
- In `rag` mode:
  - if LLM is enabled, retrieved evidence is packed into the prompt
  - if LLM is unavailable, the service falls back to deterministic local mock generation
- In `direct` mode:
  - the current user message is sent directly to the LLM
  - no retrieval is performed
  - if LLM is unavailable, the API returns a clear configuration reminder instead of local mock generation

## Observability
- `GET /` shows:
  - chat history in the browser
  - reasoning cards
  - retrieval trace
  - LLM request/response trace
- `POST /chat` and `POST /generate` return:
  - `used_remote_llm`
  - `llm_trace`
- `POST /chat` also returns:
  - `mode`
  - `answer`
  - `retrieval_trace`
  - `sources`

## Development Script

```bash
uv run python scripts/raglocal.py setup
uv run python scripts/raglocal.py ingest --doc-root sample/plugin --enable-pdf
uv run python scripts/raglocal.py serve --host 0.0.0.0 --port 8000
uv run python scripts/raglocal.py dev --doc-root sample/plugin --enable-pdf
```

## Runtime Files
- SQLite DB: `runtime/raglocal.db`
- Dense index: `runtime/dense_index.npz`

## Documentation
- Workflow tutorial: `docs/workflow_tutorial.md`
- Technical tutorial: `docs/technical_tutorial.md`
- Architecture doc: `docs/architecture.md`
- Dokploy deployment: `docs/dokploy_deploy.md`
