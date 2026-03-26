from __future__ import annotations

import json
import re
import sqlite3
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional


class Storage:
    def __init__(self, db_path: Path):
        self.db_path = db_path
        self.db_path.parent.mkdir(parents=True, exist_ok=True)
        self._init_db()

    def _conn(self) -> sqlite3.Connection:
        conn = sqlite3.connect(str(self.db_path))
        conn.row_factory = sqlite3.Row
        return conn

    def _init_db(self) -> None:
        with self._conn() as conn:
            conn.execute(
                """
                CREATE TABLE IF NOT EXISTS chunks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    chunk_id TEXT UNIQUE,
                    source_path TEXT,
                    source_type TEXT,
                    plugin_type TEXT,
                    package_name TEXT,
                    class_name TEXT,
                    method_name TEXT,
                    signature TEXT,
                    text TEXT,
                    symbols_json TEXT,
                    title TEXT,
                    meta_json TEXT
                )
                """
            )
            conn.execute(
                """
                CREATE VIRTUAL TABLE IF NOT EXISTS chunks_fts
                USING fts5(chunk_id, plugin_type, text)
                """
            )
            conn.execute(
                """
                CREATE TABLE IF NOT EXISTS symbols (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    symbol TEXT,
                    plugin_type TEXT,
                    kind TEXT,
                    source_chunk_id TEXT
                )
                """
            )

    def clear_all(self) -> None:
        with self._conn() as conn:
            conn.execute("DELETE FROM chunks")
            conn.execute("DELETE FROM chunks_fts")
            conn.execute("DELETE FROM symbols")

    def insert_chunks(self, chunks: List[Dict[str, Any]]) -> None:
        with self._conn() as conn:
            for c in chunks:
                conn.execute(
                    """
                    INSERT OR REPLACE INTO chunks (
                        chunk_id, source_path, source_type, plugin_type,
                        package_name, class_name, method_name, signature,
                        text, symbols_json, title, meta_json
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    (
                        c["chunk_id"],
                        c["source_path"],
                        c["source_type"],
                        c["plugin_type"],
                        c.get("package_name", ""),
                        c.get("class_name", ""),
                        c.get("method_name", ""),
                        c.get("signature", ""),
                        c["text"],
                        json.dumps(c.get("symbols", []), ensure_ascii=True),
                        c.get("title", ""),
                        json.dumps(c.get("meta", {}), ensure_ascii=True),
                    ),
                )
            conn.execute("DELETE FROM chunks_fts")
            fts_rows = conn.execute("SELECT chunk_id, plugin_type, text FROM chunks").fetchall()
            for row in fts_rows:
                conn.execute(
                    "INSERT INTO chunks_fts (chunk_id, plugin_type, text) VALUES (?, ?, ?)",
                    (row["chunk_id"], row["plugin_type"], row["text"]),
                )

            conn.execute("DELETE FROM symbols")
            symbol_rows = conn.execute("SELECT chunk_id, plugin_type, symbols_json FROM chunks").fetchall()
            for row in symbol_rows:
                for s in json.loads(row["symbols_json"] or "[]"):
                    conn.execute(
                        "INSERT INTO symbols (symbol, plugin_type, kind, source_chunk_id) VALUES (?, ?, ?, ?)",
                        (s, row["plugin_type"], "symbol", row["chunk_id"]),
                    )

    def get_chunk_count(self) -> int:
        with self._conn() as conn:
            row = conn.execute("SELECT COUNT(1) AS cnt FROM chunks").fetchone()
        return int(row["cnt"])

    def get_symbol_count(self) -> int:
        with self._conn() as conn:
            row = conn.execute("SELECT COUNT(DISTINCT symbol) AS cnt FROM symbols").fetchone()
        return int(row["cnt"])

    def fetch_all_chunks(self) -> List[Dict[str, Any]]:
        with self._conn() as conn:
            rows = conn.execute("SELECT * FROM chunks").fetchall()
        out: List[Dict[str, Any]] = []
        for r in rows:
            out.append(
                {
                    "chunk_id": r["chunk_id"],
                    "source_path": r["source_path"],
                    "source_type": r["source_type"],
                    "plugin_type": r["plugin_type"],
                    "package_name": r["package_name"],
                    "class_name": r["class_name"],
                    "method_name": r["method_name"],
                    "signature": r["signature"],
                    "text": r["text"],
                    "symbols": json.loads(r["symbols_json"] or "[]"),
                    "title": r["title"],
                    "meta": json.loads(r["meta_json"] or "{}"),
                }
            )
        return out

    def get_chunks_by_ids(self, chunk_ids: Iterable[str]) -> Dict[str, Dict[str, Any]]:
        ids = list(chunk_ids)
        if not ids:
            return {}
        placeholders = ",".join(["?"] * len(ids))
        query = f"SELECT * FROM chunks WHERE chunk_id IN ({placeholders})"
        with self._conn() as conn:
            rows = conn.execute(query, ids).fetchall()
        out: Dict[str, Dict[str, Any]] = {}
        for r in rows:
            out[r["chunk_id"]] = {
                "chunk_id": r["chunk_id"],
                "source_path": r["source_path"],
                "source_type": r["source_type"],
                "plugin_type": r["plugin_type"],
                "text": r["text"],
                "symbols": json.loads(r["symbols_json"] or "[]"),
                "title": r["title"],
                "package_name": r["package_name"],
                "class_name": r["class_name"],
                "method_name": r["method_name"],
                "signature": r["signature"],
                "meta": json.loads(r["meta_json"] or "{}"),
            }
        return out

    def get_chunks_by_source_paths(
        self,
        source_paths: Iterable[str],
        plugin_types: Optional[List[str]] = None,
    ) -> List[Dict[str, Any]]:
        paths = list(dict.fromkeys(str(x) for x in source_paths if x))
        if not paths:
            return []

        clauses = []
        params: List[Any] = []

        placeholders = ",".join(["?"] * len(paths))
        clauses.append(f"source_path IN ({placeholders})")
        params.extend(paths)

        if plugin_types:
            type_placeholders = ",".join(["?"] * len(plugin_types))
            clauses.append(f"plugin_type IN ({type_placeholders})")
            params.extend(plugin_types)

        sql = "SELECT * FROM chunks WHERE " + " AND ".join(clauses)
        with self._conn() as conn:
            rows = conn.execute(sql, params).fetchall()

        out: List[Dict[str, Any]] = []
        for r in rows:
            out.append(
                {
                    "chunk_id": r["chunk_id"],
                    "source_path": r["source_path"],
                    "source_type": r["source_type"],
                    "plugin_type": r["plugin_type"],
                    "text": r["text"],
                    "symbols": json.loads(r["symbols_json"] or "[]"),
                    "title": r["title"],
                    "package_name": r["package_name"],
                    "class_name": r["class_name"],
                    "method_name": r["method_name"],
                    "signature": r["signature"],
                    "meta": json.loads(r["meta_json"] or "{}"),
                }
            )
        return out

    def get_chunk_ids_for_symbols(
        self,
        symbols: Iterable[str],
        plugin_types: Optional[List[str]] = None,
        limit: int = 80,
    ) -> List[str]:
        names = [str(x) for x in symbols if str(x).strip()]
        if not names:
            return []

        clauses = []
        params: List[Any] = []
        placeholders = ",".join(["?"] * len(names))
        clauses.append(f"symbol IN ({placeholders})")
        params.extend(names)

        if plugin_types:
            type_placeholders = ",".join(["?"] * len(plugin_types))
            clauses.append(f"plugin_type IN ({type_placeholders})")
            params.extend(plugin_types)

        sql = (
            "SELECT source_chunk_id, COUNT(1) AS cnt "
            "FROM symbols WHERE "
            + " AND ".join(clauses)
            + " GROUP BY source_chunk_id ORDER BY cnt DESC, source_chunk_id ASC LIMIT ?"
        )
        params.append(limit)
        with self._conn() as conn:
            rows = conn.execute(sql, params).fetchall()
        return [str(r["source_chunk_id"]) for r in rows]

    def get_chunk_ids_for_class_names(
        self,
        class_names: Iterable[str],
        plugin_types: Optional[List[str]] = None,
        limit: int = 80,
    ) -> List[str]:
        names = [str(x) for x in class_names if str(x).strip()]
        if not names:
            return []

        clauses = []
        params: List[Any] = []
        placeholders = ",".join(["?"] * len(names))
        clauses.append(f"class_name IN ({placeholders})")
        params.extend(names)

        if plugin_types:
            type_placeholders = ",".join(["?"] * len(plugin_types))
            clauses.append(f"plugin_type IN ({type_placeholders})")
            params.extend(plugin_types)

        sql = "SELECT chunk_id FROM chunks WHERE " + " AND ".join(clauses) + " LIMIT ?"
        params.append(limit)
        with self._conn() as conn:
            rows = conn.execute(sql, params).fetchall()
        return [str(r["chunk_id"]) for r in rows]

    def get_chunk_ids_for_method_names(
        self,
        method_names: Iterable[str],
        plugin_types: Optional[List[str]] = None,
        limit: int = 80,
    ) -> List[str]:
        names = [str(x) for x in method_names if str(x).strip()]
        if not names:
            return []

        clauses = []
        params: List[Any] = []
        placeholders = ",".join(["?"] * len(names))
        clauses.append(f"method_name IN ({placeholders})")
        params.extend(names)

        if plugin_types:
            type_placeholders = ",".join(["?"] * len(plugin_types))
            clauses.append(f"plugin_type IN ({type_placeholders})")
            params.extend(plugin_types)

        sql = "SELECT chunk_id FROM chunks WHERE " + " AND ".join(clauses) + " LIMIT ?"
        params.append(limit)
        with self._conn() as conn:
            rows = conn.execute(sql, params).fetchall()
        return [str(r["chunk_id"]) for r in rows]

    def get_related_chunk_ids(
        self,
        type_names: Iterable[str],
        plugin_types: Optional[List[str]] = None,
        limit: int = 120,
    ) -> List[str]:
        names = [str(x) for x in type_names if str(x).strip()]
        if not names:
            return []

        with self._conn() as conn:
            rows = conn.execute("SELECT * FROM chunks").fetchall()

        scored: List[tuple[float, str]] = []
        lowered_names = [name.lower() for name in names]
        for row in rows:
            if plugin_types and row["plugin_type"] not in plugin_types:
                continue

            meta = json.loads(row["meta_json"] or "{}")
            class_name = str(row["class_name"] or "")
            method_name = str(row["method_name"] or "")
            signature = str(row["signature"] or "")
            title = str(row["title"] or "")
            text = str(row["text"] or "")
            return_types = [str(x) for x in meta.get("return_types", [])]
            param_types = [str(x) for x in meta.get("param_types", [])]
            related_types = [str(x) for x in meta.get("related_types", [])]
            super_types = [str(x) for x in meta.get("super_types", [])]

            class_low = class_name.lower()
            method_low = method_name.lower()
            title_low = title.lower()
            signature_low = signature.lower()
            text_low = text.lower()
            return_low = {x.lower() for x in return_types}
            param_low = {x.lower() for x in param_types}
            related_low = {x.lower() for x in related_types}
            super_low = {x.lower() for x in super_types}

            score = 0.0
            for name in lowered_names:
                if class_low == name:
                    score += 1.8
                if name in title_low:
                    score += 0.8
                if name in return_low:
                    score += 1.4
                if name in param_low:
                    score += 0.9
                if name in related_low or name in super_low:
                    score += 0.9
                if name in signature_low:
                    score += 0.4
                if name in text_low:
                    score += 0.18
                if method_low.startswith(("get", "find", "select")) and (name in return_low or name in related_low):
                    score += 0.35

            if score > 0:
                scored.append((score, str(row["chunk_id"])))

        scored.sort(key=lambda item: (-item[0], item[1]))
        return [chunk_id for _, chunk_id in scored[:limit]]

    def get_chunk_ids_for_titles(
        self,
        titles: Iterable[str],
        plugin_types: Optional[List[str]] = None,
        limit: int = 80,
    ) -> List[str]:
        names = [str(x) for x in titles if str(x).strip()]
        if not names:
            return []

        clauses = []
        params: List[Any] = []
        placeholders = ",".join(["?"] * len(names))
        clauses.append(f"title IN ({placeholders})")
        params.extend(names)

        if plugin_types:
            type_placeholders = ",".join(["?"] * len(plugin_types))
            clauses.append(f"plugin_type IN ({type_placeholders})")
            params.extend(plugin_types)

        sql = "SELECT chunk_id FROM chunks WHERE " + " AND ".join(clauses) + " LIMIT ?"
        params.append(limit)
        with self._conn() as conn:
            rows = conn.execute(sql, params).fetchall()
        return [str(r["chunk_id"]) for r in rows]

    def search_bm25(self, query: str, plugin_types: List[str], limit: int = 40) -> List[Dict[str, Any]]:
        safe = self._fts_query(query)
        if not safe:
            return []

        clauses = []
        params: List[Any] = [safe]
        if plugin_types:
            placeholders = ",".join(["?"] * len(plugin_types))
            clauses.append(f"plugin_type IN ({placeholders})")
            params.extend(plugin_types)

        where = " AND ".join(["chunks_fts MATCH ?"] + clauses)
        sql = (
            "SELECT chunk_id, plugin_type, bm25(chunks_fts) AS score "
            f"FROM chunks_fts WHERE {where} ORDER BY score ASC LIMIT ?"
        )
        params.append(limit)

        with self._conn() as conn:
            try:
                rows = conn.execute(sql, params).fetchall()
            except sqlite3.OperationalError:
                return []

        out = []
        for rank, r in enumerate(rows, start=1):
            out.append(
                {
                    "chunk_id": r["chunk_id"],
                    "plugin_type": r["plugin_type"],
                    "score": float(r["score"]),
                    "rank": rank,
                }
            )
        return out

    def _fts_query(self, query: str) -> str:
        terms = []
        seen = set()
        for t in re.findall(r"[A-Za-z_][A-Za-z0-9_\.]{0,80}", query):
            clean = "".join(ch for ch in t if ch.isalnum() or ch in "._")
            if not clean:
                continue
            key = clean.lower()
            if key in seen:
                continue
            seen.add(key)
            terms.append('"' + clean.replace('"', '""') + '"')
        if not terms:
            return ""
        return " OR ".join(terms[:12])

    def list_symbols(self, plugin_types: Optional[List[str]] = None) -> List[str]:
        params: List[Any] = []
        where = ""
        if plugin_types:
            placeholders = ",".join(["?"] * len(plugin_types))
            where = f"WHERE plugin_type IN ({placeholders})"
            params.extend(plugin_types)

        with self._conn() as conn:
            rows = conn.execute(
                f"SELECT DISTINCT symbol FROM symbols {where}",
                params,
            ).fetchall()
        return sorted([str(r["symbol"]) for r in rows])
