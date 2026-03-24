#!/bin/sh
set -eu

cd /app

AUTO_INGEST="${AUTO_INGEST:-true}"
INGEST_DOC_ROOT="${INGEST_DOC_ROOT:-sample/plugin}"
INGEST_REBUILD="${INGEST_REBUILD:-false}"
INGEST_ENABLE_PDF="${INGEST_ENABLE_PDF:-false}"
HOST="${HOST:-0.0.0.0}"
PORT="${PORT:-8000}"

if [ "$AUTO_INGEST" = "true" ]; then
  echo "Auto ingest enabled. Building local index from ${INGEST_DOC_ROOT}."
  if [ "$INGEST_REBUILD" = "true" ]; then
    REBUILD_FLAG="--rebuild"
  else
    REBUILD_FLAG="--no-rebuild"
  fi

  if [ "$INGEST_ENABLE_PDF" = "true" ]; then
    PDF_FLAG="--enable-pdf"
  else
    PDF_FLAG="--no-enable-pdf"
  fi

  uv run python scripts/raglocal.py ingest --doc-root "$INGEST_DOC_ROOT" $REBUILD_FLAG $PDF_FLAG
fi

exec sh -c "PYTHONPATH=src uv run uvicorn rag_codegen.app:app --host ${HOST} --port ${PORT}"
