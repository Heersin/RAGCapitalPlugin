# RAGLocal Plugin Codegen (action/drc/constraint)

A local-first RAG prototype for generating closed-source plugin code from docs.

## Scope
- Supported plugin types: `action`, `drc`, `constraint`
- Input docs: JavaDoc HTML + Java examples (+ supported PDF via `pypdf`)
- Output style: `core class + template`

## API
- `GET /`
- `POST /ingest`
- `POST /retrieve`
- `POST /generate`
- `POST /chat`
- `POST /evaluate/run`
- `GET /health`

## Quick Start
1. Create/sync environment with `uv`:
```bash
uv sync --extra dev
```

2. Start server:
```bash
PYTHONPATH=src uv run uvicorn rag_codegen.app:app --host 0.0.0.0 --port 8000
```

3. Open the simple chat UI:
```text
http://localhost:8000/
```

After ingesting docs, you can ask questions directly in the browser and read a formatted answer instead of raw JSON.

4. Ingest sample docs:
```bash
curl -X POST http://localhost:8000/ingest \
  -H 'Content-Type: application/json' \
  -d '{"doc_root":"sample/plugin","rebuild":true,"enable_pdf":false}'
```

Enable PDF parsing by setting `enable_pdf` to `true` when your doc root contains PDF guides.

5. Retrieve evidence:
```bash
curl -X POST http://localhost:8000/retrieve \
  -H 'Content-Type: application/json' \
  -d '{"query":"生成一个只读action，在输出窗口打印选中对象属性","plugin_type_hint":"action","top_k":8}'
```

6. Generate code:
```bash
curl -X POST http://localhost:8000/generate \
  -H 'Content-Type: application/json' \
  -d '{"requirement":"生成一个只读action，在输出窗口打印选中对象属性","plugin_type":"action","context_budget":10}'
```

7. Generate a simplified chat answer:
```bash
curl -X POST http://localhost:8000/chat \
  -H 'Content-Type: application/json' \
  -d '{"question":"生成一个只读action，在输出窗口打印选中对象属性","plugin_type":"action","context_budget":10}'
```

8. Run evaluation:
```bash
curl -X POST http://localhost:8000/evaluate/run \
  -H 'Content-Type: application/json' \
  -d '{"experiment_id":"exp-v1","task_set_id":"eval/tasks_seed.json","variants":["B0","B1","B2","B3"]}'
```

## Workflow Tutorial
- Chinese tutorial: `docs/workflow_tutorial.md`
- Technical tutorial: `docs/technical_tutorial.md`
- Dokploy deployment: `docs/dokploy_deploy.md`

## Current Workflow
1. `/ingest` scans your doc root, parses HTML/Java/PDF, stores chunks in SQLite, and rebuilds the dense index.
2. `/chat` is the browser-friendly question-answer entrypoint. It wraps retrieval, generation, and self-check into a simpler response.
3. `/retrieve` exposes evidence cards when you want to inspect recall quality.
4. `/generate` exposes the full structured response when you need raw fields for automation.
5. `/evaluate/run` replays evaluation tasks from `eval/tasks_seed.json`.

## AI Behavior
1. If `LLM_BASE_URL`, `LLM_API_KEY`, and `LLM_MODEL` are all configured, generation uses an external OpenAI-compatible `chat/completions` API.
2. If LLM config is missing, or the remote call fails, the service falls back to deterministic local mock generation.
3. The current chat page keeps visible history in the browser, but the backend still handles each `/chat` request independently unless conversation memory is implemented later.

## RAG Improvements In This Version
1. Retrieval now expands mixed Chinese/English requests into API-oriented query hints before BM25 and dense search.
2. Fusion now adds metadata-aware reranking for titles, methods, examples, read-only semantics, and output-window intent.
3. Final evidence selection now reduces duplicate hits from the same source file.
4. Chunk FTS and symbol indexes are rebuilt from the canonical chunks table to avoid drift after repeated ingest runs.

## Development Script
Use `scripts/raglocal.py` to simplify common workflows:

```bash
uv run python scripts/raglocal.py setup
uv run python scripts/raglocal.py ingest --doc-root sample/plugin
uv run python scripts/raglocal.py serve --host 0.0.0.0 --port 8000
uv run python scripts/raglocal.py dev --doc-root sample/plugin
```

## Dependency Management (uv)
- Add/update dependencies in `pyproject.toml`
- Refresh lock file:
```bash
uv lock
```
- Sync local environment:
```bash
uv sync --extra dev
```

## LLM API Config (Optional)
If not configured, service uses deterministic local mock generation.

- `LLM_BASE_URL` (OpenAI-compatible base URL)
- `LLM_API_KEY`
- `LLM_MODEL`
- `LLM_TIMEOUT_SECONDS` (default 60)

## Runtime
- SQLite DB: `runtime/raglocal.db`
- Dense index: `runtime/dense_index.npz`

## Notes
- PDF ingest is supported and controlled by the `enable_pdf` ingest flag.
- This prototype prioritizes API correctness and hallucination reduction.
