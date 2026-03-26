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
        class_signature = strip_html(
            self._extract_first(raw, r'<div class="description">[\s\S]*?<pre>([\s\S]*?)</pre>') or ""
        )
        super_types = self._extract_relation_symbols(raw, "All Superinterfaces")
        inherited_methods = self._extract_inherited_methods(raw)

        chunks: List[Dict] = []

        # Class-level chunk
        relation_lines: List[str] = []
        if class_signature:
            relation_lines.append(f"Signature: {class_signature}")
        if super_types:
            relation_lines.append("Superinterfaces: " + ", ".join(super_types))
        for owner, methods in inherited_methods.items():
            relation_lines.append(f"Inherited methods from {owner}: " + ", ".join(methods[:24]))

        class_text = truncate_text("\n".join(relation_lines + [stripped]), 5000)
        class_symbols = extract_symbols(raw + "\n" + "\n".join(relation_lines))
        chunks.append(
            {
                "chunk_id": self._chunk_id(path, "class", class_name or path.stem),
                "source_path": str(path),
                "source_type": "javadoc_html",
                "plugin_type": plugin_type,
                "package_name": package,
                "class_name": class_name,
                "method_name": "",
                "signature": class_signature,
                "title": title,
                "text": class_text,
                "symbols": class_symbols,
                "meta": {
                    "kind": "class",
                    "super_types": super_types,
                    "related_types": sorted(set(super_types + extract_symbols(class_signature))),
                    "inherited_methods": inherited_methods,
                },
            }
        )

        # Method-level chunks
        method_chunks: Dict[str, Dict] = {}
        for method_name, sig_clean, full_signature, description in self._extract_html_method_summaries(raw):
            suffix = self._method_chunk_suffix(class_name, method_name, sig_clean)
            method_symbols = extract_symbols(f"{full_signature}\n{description}\n{raw}")
            method_meta = self._method_signature_meta(method_name, sig_clean, full_signature, class_name)
            method_text = truncate_text(
                "\n".join(
                    [
                        f"{title} {method_name}",
                        full_signature,
                        sig_clean,
                        description,
                    ]
                ).strip(),
                1600,
            )
            method_chunks[suffix] = {
                "chunk_id": self._chunk_id(path, "method", suffix),
                "source_path": str(path),
                "source_type": "javadoc_html",
                "plugin_type": plugin_type,
                "package_name": package,
                "class_name": class_name,
                "method_name": method_name,
                "signature": sig_clean,
                "title": f"{class_name}.{method_name}",
                "text": method_text,
                "symbols": method_symbols,
                "meta": method_meta,
            }

        for method_name, signature in re.findall(r"<h4>([^<]+)</h4>\s*<pre>([\s\S]*?)</pre>", raw):
            full_signature = " ".join(strip_html(signature).split())
            sig_clean = self._normalize_method_signature(method_name, full_signature)
            suffix = self._method_chunk_suffix(class_name, method_name, sig_clean)
            existing = method_chunks.get(suffix)
            if existing:
                if sig_clean and sig_clean not in existing["text"]:
                    existing["text"] = truncate_text(existing["text"] + "\n" + sig_clean, 1600)
                continue

            method_symbols = extract_symbols(full_signature + " " + raw)
            method_text = f"{title} {method_name} {full_signature}"
            method_meta = self._method_signature_meta(method_name, sig_clean, full_signature, class_name)
            method_chunks[suffix] = {
                "chunk_id": self._chunk_id(path, "method", suffix),
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
                "meta": method_meta,
            }

        chunks.extend(method_chunks.values())

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
        if (
            "/plugin/api/com/mentor/chs/plugin/" in p
            or "/plugin/api/com/mentor/chs/api/" in p
        ) and all(
            x not in p for x in ["/plugin/action/", "/plugin/drc/", "/plugin/constraint/"]
        ):
            return "core"
        if "/plugin/examples/java/" in p and all(
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
        if "plugin" in n:
            return "core"
        return None

    def _extract_first(self, text: str, pattern: str) -> Optional[str]:
        m = re.search(pattern, text)
        return m.group(1).strip() if m else None

    def _extract_relation_symbols(self, raw: str, label: str) -> List[str]:
        m = re.search(rf"<dt>{re.escape(label)}:</dt>\s*<dd>([\s\S]*?)</dd>", raw)
        if not m:
            return []
        return sorted(set(re.findall(r">((?:IX|com\.mentor\.)[A-Za-z0-9_\.]+)<", m.group(1))))

    def _extract_inherited_methods(self, raw: str) -> Dict[str, List[str]]:
        inherited: Dict[str, List[str]] = {}
        for owner, body in re.findall(
            r"Methods inherited from (?:class|interface).*?<a [^>]*>([^<]+)</a></h3>\s*<code>([\s\S]*?)</code>",
            raw,
        ):
            methods = re.findall(r">([A-Za-z_][A-Za-z0-9_]*)</a>", body)
            if methods:
                inherited[owner] = sorted(set(methods))
        return inherited

    def _extract_html_method_summaries(self, raw: str) -> List[tuple[str, str, str, str]]:
        out: List[tuple[str, str, str, str]] = []
        pattern = re.compile(
            r'<tr[^>]*>\s*<td class="colFirst"><code>([\s\S]*?)</code></td>\s*<td class="colLast"><code><span class="memberNameLink"><a [^>]*>([^<]+)</a></span>\(([\s\S]*?)\)</code>\s*(?:<div class="block">([\s\S]*?)</div>)?',
            re.MULTILINE,
        )
        for return_part, method_name, signature_part, description in pattern.findall(raw):
            full_signature = " ".join(strip_html(f"{return_part} {method_name}({signature_part})").split())
            sig_clean = self._normalize_method_signature(method_name, full_signature)
            desc_clean = strip_html(description or "")
            out.append((method_name.strip(), sig_clean, full_signature, desc_clean))

        if out:
            return out

        fallback = re.compile(
            r'<td class="colLast"><code><span class="memberNameLink"><a [^>]*>([^<]+)</a></span>\(([\s\S]*?)\)</code>\s*(?:<div class="block">([\s\S]*?)</div>)?',
            re.MULTILINE,
        )
        for method_name, signature_part, description in fallback.findall(raw):
            full_signature = " ".join(strip_html(f"{method_name}({signature_part})").split())
            sig_clean = self._normalize_method_signature(method_name, full_signature)
            desc_clean = strip_html(description or "")
            out.append((method_name.strip(), sig_clean, full_signature, desc_clean))
        return out

    def _method_chunk_suffix(self, class_name: str, method_name: str, signature: str) -> str:
        sig_suffix = signature or method_name
        return f"{class_name}.{method_name}.{sig_suffix}"

    def _method_signature_meta(self, method_name: str, signature: str, full_signature: str, owner_class: str) -> Dict:
        flat = " ".join(signature.split())
        full_flat = " ".join(full_signature.split())
        marker = re.search(rf"\b{re.escape(method_name)}\s*\(", full_flat)
        prefix = full_flat[: marker.start()] if marker else ""
        params_match = re.search(r"\((.*)\)", full_flat)
        params = params_match.group(1) if params_match else ""
        return_types = extract_symbols(prefix)
        param_types = extract_symbols(params)
        generic_bounds = extract_symbols(full_flat)
        related_types = sorted(set(return_types + param_types))
        access_kind = (
            "getter"
            if method_name.startswith(("get", "find", "create", "load", "fetch"))
            else "selector"
            if method_name.startswith(("select", "current", "active"))
            else "other"
        )
        return {
            "kind": "method",
            "owner_class": owner_class,
            "return_types": return_types,
            "param_types": param_types,
            "related_types": sorted(set(related_types + generic_bounds)),
            "full_signature": full_flat,
            "access_kind": access_kind,
        }

    def _normalize_method_signature(self, method_name: str, signature: str) -> str:
        flat = " ".join(signature.split())
        marker = re.search(rf"\b{re.escape(method_name)}\s*\(", flat)
        if marker:
            return flat[marker.start() :]
        return flat

    def _strip_java_comments(self, code: str) -> str:
        code = re.sub(r"/\*[\s\S]*?\*/", " ", code)
        code = re.sub(r"//.*?$", " ", code, flags=re.MULTILINE)
        return code

    def _chunk_id(self, path: Path, kind: str, suffix: str) -> str:
        clean_suffix = re.sub(r"[^A-Za-z0-9_\.\-]+", "_", suffix)
        return f"{path.as_posix()}::{kind}::{clean_suffix}"
