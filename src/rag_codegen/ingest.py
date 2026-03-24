from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional

import numpy as np

from .config import Settings
from .storage import Storage
from .text_utils import extract_symbols, hashed_dense_embed, strip_html, truncate_text

IGNORED_HTML = {
    "index.html",
    "index-all.html",
    "overview-summary.html",
    "overview-tree.html",
    "help-doc.html",
    "allclasses-frame.html",
    "allclasses-noframe.html",
    "overview-frame.html",
    "deprecated-list.html",
    "constant-values.html",
}


@dataclass
class IngestStats:
    doc_count: int = 0
    chunk_count: int = 0


class Ingestor:
    def __init__(self, settings: Settings, storage: Storage):
        self.settings = settings
        self.storage = storage

    def ingest(self, doc_root: str, rebuild: bool = True, enable_pdf: bool = False) -> Dict[str, int | str]:
        root = Path(doc_root)
        if not root.exists():
            raise FileNotFoundError(f"doc_root not found: {doc_root}")

        if rebuild:
            self.storage.clear_all()

        chunks: List[Dict] = []
        stats = IngestStats()

        html_files = sorted(root.rglob("*.html"))
        java_files = sorted(root.rglob("*.java"))
        pdf_files = sorted(root.rglob("*.pdf")) if enable_pdf else []

        for f in html_files:
            parsed = self._parse_html_file(f)
            if not parsed:
                continue
            stats.doc_count += 1
            chunks.extend(parsed)

        for f in java_files:
            parsed = self._parse_java_file(f)
            if not parsed:
                continue
            stats.doc_count += 1
            chunks.extend(parsed)

        if enable_pdf:
            for f in pdf_files:
                parsed = self._parse_pdf_file(f)
                if not parsed:
                    continue
                stats.doc_count += 1
                chunks.extend(parsed)

        if not chunks:
            return {
                "doc_count": 0,
                "chunk_count": 0,
                "symbol_count": 0,
                "status": "no_chunks",
            }

        self.storage.insert_chunks(chunks)
        self._build_dense_index(chunks)
        stats.chunk_count = self.storage.get_chunk_count()

        return {
            "doc_count": stats.doc_count,
            "chunk_count": stats.chunk_count,
            "symbol_count": self.storage.get_symbol_count(),
            "status": "ok",
        }

    def _parse_html_file(self, path: Path) -> List[Dict]:
        if path.name in IGNORED_HTML:
            return []
        plugin_type = self._plugin_type_from_path(path)
        if plugin_type is None:
            return []

        raw = path.read_text(errors="ignore")
        stripped = strip_html(raw)
        if not stripped:
            return []

        package = self._extract_first(raw, r'<div class="subTitle">([^<]+)</div>') or ""
        title = self._extract_first(raw, r'<h2 title="(?:Interface|Class) ([^"]+)" class="title">')
        if not title:
            title = self._extract_first(raw, r"<title>([^<]+)</title>") or path.stem

        class_name = title.split()[-1] if title else ""

        chunks: List[Dict] = []

        # Class-level chunk
        class_text = truncate_text(stripped, 5000)
        class_symbols = extract_symbols(raw)
        chunks.append(
            {
                "chunk_id": self._chunk_id(path, "class", class_name or path.stem),
                "source_path": str(path),
                "source_type": "javadoc_html",
                "plugin_type": plugin_type,
                "package_name": package,
                "class_name": class_name,
                "method_name": "",
                "signature": "",
                "title": title,
                "text": class_text,
                "symbols": class_symbols,
                "meta": {"kind": "class"},
            }
        )

        # Method-level chunks
        for method_name, signature in re.findall(r"<h4>([^<]+)</h4>\s*<pre>([\s\S]*?)</pre>", raw):
            sig_clean = strip_html(signature)
            method_symbols = extract_symbols(sig_clean + " " + raw)
            method_text = f"{title} {method_name} {sig_clean}"
            chunks.append(
                {
                    "chunk_id": self._chunk_id(path, "method", f"{class_name}.{method_name}"),
                    "source_path": str(path),
                    "source_type": "javadoc_html",
                    "plugin_type": plugin_type,
                    "package_name": package,
                    "class_name": class_name,
                    "method_name": method_name,
                    "signature": sig_clean,
                    "title": f"{class_name}.{method_name}",
                    "text": truncate_text(method_text, 1200),
                    "symbols": method_symbols,
                    "meta": {"kind": "method"},
                }
            )

        return chunks

    def _parse_java_file(self, path: Path) -> List[Dict]:
        plugin_type = self._plugin_type_from_path(path)
        if plugin_type is None:
            return []

        raw = path.read_text(errors="ignore")
        if not raw.strip():
            return []

        code_no_comments = self._strip_java_comments(raw)
        package = self._extract_first(code_no_comments, r"package\s+([A-Za-z0-9_\.]+)\s*;") or ""
        class_name = (
            self._extract_first(
                code_no_comments,
                r"(?:public|protected|private|abstract|final|static|\s)*(?:class|interface|enum)\s+([A-Za-z0-9_]+)\b",
            )
            or path.stem
        )
        title = f"{class_name} (example)"

        implements = re.findall(r"implements\s+([A-Za-z0-9_,\s\.]+)", code_no_comments)
        methods = re.findall(
            r"public\s+(?:static\s+)?(?:final\s+)?[A-Za-z0-9_<>,\[\]\s\.]+\s+([A-Za-z0-9_]+)\s*\(",
            code_no_comments,
        )
        summary = " ".join(implements[:2] + methods[:20])

        text = truncate_text(strip_html(raw), 6000)
        symbols = extract_symbols(raw)

        return [
            {
                "chunk_id": self._chunk_id(path, "java", class_name),
                "source_path": str(path),
                "source_type": "java_example",
                "plugin_type": plugin_type,
                "package_name": package,
                "class_name": class_name,
                "method_name": "",
                "signature": summary,
                "title": title,
                "text": text,
                "symbols": symbols,
                "meta": {"kind": "example"},
            }
        ]

    def _parse_pdf_file(self, path: Path) -> List[Dict]:
        try:
            from pypdf import PdfReader  # type: ignore
        except Exception:
            return []

        plugin_type = self._plugin_type_from_filename(path.name)
        if plugin_type is None:
            return []

        try:
            reader = PdfReader(str(path))
            pages = []
            for i, page in enumerate(reader.pages[:20]):
                txt = page.extract_text() or ""
                if txt.strip():
                    pages.append(txt)
            text = "\n".join(pages)
        except Exception:
            return []

        text = truncate_text(strip_html(text), 5000)
        if not text.strip():
            return []

        return [
            {
                "chunk_id": self._chunk_id(path, "pdf", path.stem),
                "source_path": str(path),
                "source_type": "pdf",
                "plugin_type": plugin_type,
                "package_name": "",
                "class_name": "",
                "method_name": "",
                "signature": "",
                "title": path.stem,
                "text": text,
                "symbols": extract_symbols(text),
                "meta": {"kind": "guide"},
            }
        ]

    def _build_dense_index(self, chunks: List[Dict]) -> None:
        ids = [c["chunk_id"] for c in chunks]
        vectors = np.vstack([hashed_dense_embed(c["text"], self.settings.dense_dim) for c in chunks])
        self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)
        np.savez(
            self.settings.dense_index_path,
            ids=np.array(ids),
            vectors=vectors.astype(np.float32),
        )

    def _plugin_type_from_path(self, path: Path) -> Optional[str]:
        p = str(path).replace("\\\\", "/").lower()
        if "/plugin/action/" in p or "/plugin/examples/java/src/com/example/plugin/action/" in p:
            return "action"
        if "/plugin/drc/" in p or "/plugin/examples/java/src/com/example/plugin/drc/" in p:
            return "drc"
        if "/plugin/constraint/" in p or "/plugin/examples/java/src/com/example/plugin/constraint/" in p:
            return "constraint"

        # Keep core plugin APIs as shared dependencies.
        if "/plugin/api/com/mentor/chs/plugin/" in p and all(
            x not in p for x in ["/plugin/action/", "/plugin/drc/", "/plugin/constraint/"]
        ):
            return "core"
        return None

    def _plugin_type_from_filename(self, name: str) -> Optional[str]:
        n = name.lower()
        if "action" in n:
            return "action"
        if "drc" in n:
            return "drc"
        if "constraint" in n:
            return "constraint"
        return None

    def _extract_first(self, text: str, pattern: str) -> Optional[str]:
        m = re.search(pattern, text)
        return m.group(1).strip() if m else None

    def _strip_java_comments(self, code: str) -> str:
        code = re.sub(r"/\*[\s\S]*?\*/", " ", code)
        code = re.sub(r"//.*?$", " ", code, flags=re.MULTILINE)
        return code

    def _chunk_id(self, path: Path, kind: str, suffix: str) -> str:
        clean_suffix = re.sub(r"[^A-Za-z0-9_\.\-]+", "_", suffix)
        return f"{path.as_posix()}::{kind}::{clean_suffix}"
