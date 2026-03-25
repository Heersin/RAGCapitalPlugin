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
2. `/retrieve` now follows an API-first staged plan:
   - decompose the request into subgoals
   - expand Chinese intent into English/API lookup terms
   - search Javadoc first
   - expand related classes/methods/superinterfaces
   - then blend in examples
3. `/retrieve` exposes evidence cards when you want to inspect recall quality.
4. `/generate` exposes the full structured response when you need raw fields for automation.
5. `/chat` is the browser-friendly question-answer entrypoint. It wraps retrieval, generation, self-check, and now returns both `retrieval_trace` and `llm_trace`.
6. `/evaluate/run` replays evaluation tasks from `eval/tasks_seed.json`.

## AI Behavior
1. If `LLM_BASE_URL`, `LLM_API_KEY`, and `LLM_MODEL` are all configured, generation uses an external OpenAI-compatible `chat/completions` API.
2. If LLM config is missing, or the remote call fails, the service falls back to deterministic local mock generation.
3. The current chat page keeps visible history in the browser, but the backend still handles each `/chat` request independently unless conversation memory is implemented later.
4. The chat response now exposes whether a remote LLM was actually used for that answer.
5. Both `/chat` and `/generate` now expose `llm_trace`, including the prompt messages, request payload summary, response text, usage, and any fallback reason.
6. When a remote LLM is enabled, retrieved evidence is sent as grounding context, but the final answer is no longer forced into a rigid normalized template.

## RAG Improvements In This Version
1. Retrieval now expands mixed Chinese/English requests into a structured search plan with `subgoals`, `english_terms`, `api_terms`, `relation_terms`, and `example_terms`.
2. The retrieval chain is API-first: Javadoc lookup runs before relation expansion and example lookup, which better matches the manual plugin-writing workflow.
3. HTML ingest now preserves more Javadoc structure:
   - class signatures
   - superinterfaces
   - inherited method lists
   - method summary descriptions
4. Overloaded methods now get stable chunk ids, reducing collisions and recall loss in the SQLite index.
5. Fusion now adds metadata-aware reranking for titles, methods, examples, read-only semantics, output-window intent, and explicit API symbol matches.
6. Final evidence selection now reduces duplicate hits from the same source file and keeps more API docs ahead of example code.
7. Chunk FTS and symbol indexes are rebuilt from the canonical chunks table to avoid drift after repeated ingest runs.

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

## Docker Port Notes
- The app listens on container port `8000`.
- For Dokploy, prefer internal proxying and do not publish a host port from Docker Compose.
- In Dokploy Domains, set the container port to `8000`.

## Notes
- PDF ingest is supported and controlled by the `enable_pdf` ingest flag.
- This prototype prioritizes API correctness and hallucination reduction.
