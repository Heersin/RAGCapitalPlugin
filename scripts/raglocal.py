#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import shutil
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src"
if str(SRC) not in sys.path:
    sys.path.insert(0, str(SRC))

from rag_codegen.config import SETTINGS
from rag_codegen.ingest import Ingestor
from rag_codegen.storage import Storage


def run(cmd: list[str]) -> None:
    print("$", " ".join(cmd))
    subprocess.run(cmd, cwd=ROOT, check=True)


def require_uv() -> str:
    uv = shutil.which("uv")
    if not uv:
        raise SystemExit("uv is required but was not found in PATH.")
    return uv


def ingest_docs(doc_root: str, rebuild: bool, enable_pdf: bool) -> dict:
    os.chdir(ROOT)
    storage = Storage(SETTINGS.db_path)
    ingestor = Ingestor(SETTINGS, storage)
    return ingestor.ingest(doc_root=doc_root, rebuild=rebuild, enable_pdf=enable_pdf)


def command_setup(_args: argparse.Namespace) -> None:
    uv = require_uv()
    run([uv, "sync", "--extra", "dev"])


def command_ingest(args: argparse.Namespace) -> None:
    result = ingest_docs(
        doc_root=args.doc_root,
        rebuild=args.rebuild,
        enable_pdf=args.enable_pdf,
    )
    print(json.dumps(result, ensure_ascii=False, indent=2))


def uvicorn_command(host: str, port: int) -> list[str]:
    uv = require_uv()
    return [
        uv,
        "run",
        "uvicorn",
        "rag_codegen.app:app",
        "--host",
        host,
        "--port",
        str(port),
    ]


def command_serve(args: argparse.Namespace) -> None:
    os.chdir(ROOT)
    env = os.environ.copy()
    env["PYTHONPATH"] = str(SRC)
    cmd = uvicorn_command(args.host, args.port)
    print("$", "PYTHONPATH=src", " ".join(cmd))
    os.execvpe(cmd[0], cmd, env)


def command_dev(args: argparse.Namespace) -> None:
    if not args.skip_setup:
        command_setup(args)
    if not args.skip_ingest:
        command_ingest(args)
    command_serve(args)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Helper script for local RAGLocal development.")
    subparsers = parser.add_subparsers(dest="command", required=True)

    setup = subparsers.add_parser("setup", help="Install or update local dependencies with uv.")
    setup.set_defaults(func=command_setup)

    ingest = subparsers.add_parser("ingest", help="Build the local RAG index without starting the HTTP server.")
    ingest.add_argument("--doc-root", default="sample/plugin", help="Document root to ingest.")
    ingest.add_argument("--rebuild", action=argparse.BooleanOptionalAction, default=True)
    ingest.add_argument("--enable-pdf", action=argparse.BooleanOptionalAction, default=False)
    ingest.set_defaults(func=command_ingest)

    serve = subparsers.add_parser("serve", help="Start the FastAPI server.")
    serve.add_argument("--host", default="0.0.0.0")
    serve.add_argument("--port", type=int, default=8000)
    serve.set_defaults(func=command_serve)

    dev = subparsers.add_parser("dev", help="Setup, ingest docs, then start the server.")
    dev.add_argument("--doc-root", default="sample/plugin", help="Document root to ingest.")
    dev.add_argument("--rebuild", action=argparse.BooleanOptionalAction, default=True)
    dev.add_argument("--enable-pdf", action=argparse.BooleanOptionalAction, default=False)
    dev.add_argument("--host", default="0.0.0.0")
    dev.add_argument("--port", type=int, default=8000)
    dev.add_argument("--skip-setup", action="store_true")
    dev.add_argument("--skip-ingest", action="store_true")
    dev.set_defaults(func=command_dev)

    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
