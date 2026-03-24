from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Literal, Optional

import numpy as np

from .config import Settings
from .storage import Storage
from .text_utils import (
    QueryProfile,
    build_query_profile,
    detect_plugin_type,
    hashed_dense_embed,
    keyword_overlap_score,
    tokenize,
    truncate_text,
)

RetrievalMode = Literal["none", "dense", "hybrid"]


@dataclass
class RetrievalResult:
    plugin_type: str
    evidence_cards: List[Dict]
    trace: Dict


class Retriever:
    def __init__(self, settings: Settings, storage: Storage):
        self.settings = settings
        self.storage = storage

    def retrieve(
        self,
        query: str,
        plugin_type_hint: Optional[str] = None,
        top_k: int = 12,
        mode: RetrievalMode = "hybrid",
    ) -> RetrievalResult:
        plugin_type, confidence = (
            (plugin_type_hint, 1.0)
            if plugin_type_hint in {"action", "drc", "constraint"}
            else detect_plugin_type(query)
        )

        allowed_types = [plugin_type, "core"]
        top_k = max(1, min(40, top_k))
        profile = build_query_profile(query, plugin_type)
        candidate_limit = max(40, top_k * 5)

        bm25_hits = []
        if mode in {"hybrid"}:
            bm25_hits = self.storage.search_bm25(
                profile.retrieval_query,
                allowed_types,
                limit=candidate_limit,
            )

        dense_hits = []
        if mode in {"dense", "hybrid"}:
            dense_hits = self._dense_search(
                profile.retrieval_query,
                allowed_types,
                limit=candidate_limit,
            )

        fused = self._rrf_fuse(bm25_hits, dense_hits, profile, plugin_type, k=self.settings.rrf_k)
        fused_ids = [x["chunk_id"] for x in fused]
        chunk_map = self.storage.get_chunks_by_ids(fused_ids)
        selected = self._select_diverse_hits(fused, chunk_map, top_k)

        cards = []
        for item in selected:
            c = chunk_map.get(item["chunk_id"])
            if not c:
                continue
            cards.append(
                {
                    "chunk_id": c["chunk_id"],
                    "source_type": c["source_type"],
                    "source_path": c["source_path"],
                    "plugin_type": c["plugin_type"],
                    "score": float(item["score"]),
                    "title": c.get("title") or c.get("class_name") or c["chunk_id"],
                    "text": truncate_text(c["text"], 1400),
                    "symbols": c.get("symbols", [])[:30],
                }
            )

        trace = {
            "mode": mode,
            "plugin_type": plugin_type,
            "route_confidence": confidence,
            "retrieval_query": profile.retrieval_query,
            "query_expansions": profile.expansions[:12],
            "bm25_candidates": len(bm25_hits),
            "dense_candidates": len(dense_hits),
            "fused_candidates": len(fused),
        }

        return RetrievalResult(plugin_type=plugin_type, evidence_cards=cards, trace=trace)

    def _dense_search(self, query: str, plugin_types: List[str], limit: int = 40) -> List[Dict]:
        dense_path = Path(self.settings.dense_index_path)
        if not dense_path.exists():
            return []

        try:
            blob = np.load(dense_path, allow_pickle=False)
            ids = blob["ids"]
            vectors = blob["vectors"]
        except Exception:
            return []

        chunk_map = self.storage.get_chunks_by_ids([str(x) for x in ids.tolist()])
        if vectors.size == 0:
            return []

        # Use float64 + nan_to_num to avoid BLAS numeric warnings on some macOS builds.
        qv = hashed_dense_embed(query, self.settings.dense_dim).astype(np.float64, copy=False)
        dense_matrix = np.nan_to_num(vectors.astype(np.float64, copy=False), nan=0.0, posinf=0.0, neginf=0.0)
        sims = np.sum(dense_matrix * qv[np.newaxis, :], axis=1)

        scored = []
        for i, score in enumerate(sims.tolist()):
            chunk_id = str(ids[i])
            chunk = chunk_map.get(chunk_id)
            if not chunk:
                continue
            if chunk["plugin_type"] not in plugin_types:
                continue
            scored.append((chunk_id, float(score)))

        scored.sort(key=lambda x: x[1], reverse=True)
        out = []
        for rank, (chunk_id, score) in enumerate(scored[:limit], start=1):
            out.append(
                {
                    "chunk_id": chunk_id,
                    "score": score,
                    "rank": rank,
                }
            )
        return out

    def _rrf_fuse(
        self,
        bm25_hits: List[Dict],
        dense_hits: List[Dict],
        profile: QueryProfile,
        plugin_type: str,
        k: int = 60,
    ) -> List[Dict]:
        by_id: Dict[str, Dict] = {}

        for hit in bm25_hits:
            cid = hit["chunk_id"]
            row = by_id.setdefault(cid, {"chunk_id": cid, "score": 0.0, "bm25_rank": None, "dense_rank": None})
            row["bm25_rank"] = hit["rank"]
            row["score"] += 1.0 / (k + hit["rank"])

        for hit in dense_hits:
            cid = hit["chunk_id"]
            row = by_id.setdefault(cid, {"chunk_id": cid, "score": 0.0, "bm25_rank": None, "dense_rank": None})
            row["dense_rank"] = hit["rank"]
            row["score"] += 1.0 / (k + hit["rank"])

        if not by_id:
            return []

        query_tokens = profile.query_tokens or tokenize(profile.raw_query)
        chunk_map = self.storage.get_chunks_by_ids(by_id.keys())

        for cid, row in by_id.items():
            chunk = chunk_map.get(cid, {})
            symbols = chunk.get("symbols", [])
            bonus = keyword_overlap_score(query_tokens, symbols)
            row["score"] += 0.14 * bonus
            row["score"] += self._metadata_bonus(profile, plugin_type, chunk, query_tokens)

        fused = list(by_id.values())
        fused.sort(key=lambda x: x["score"], reverse=True)
        return fused

    def _metadata_bonus(
        self,
        profile: QueryProfile,
        plugin_type: str,
        chunk: Dict,
        query_tokens: List[str],
    ) -> float:
        if not chunk:
            return 0.0

        title_tokens = tokenize(
            " ".join(
                [
                    chunk.get("title", ""),
                    chunk.get("class_name", ""),
                    chunk.get("method_name", ""),
                    chunk.get("signature", ""),
                ]
            )
        )
        text_low = chunk.get("text", "").lower()
        meta = chunk.get("meta", {}) or {}
        kind = meta.get("kind", "")
        bonus = 0.0

        bonus += 0.18 * keyword_overlap_score(query_tokens, title_tokens)

        if chunk.get("plugin_type") == plugin_type:
            bonus += 0.05

        if profile.wants_examples and chunk.get("source_type") == "java_example":
            bonus += 0.09
        if profile.wants_api_docs and chunk.get("source_type") == "javadoc_html":
            bonus += 0.07
        if profile.prefers_methods and kind == "method":
            bonus += 0.08
        if profile.wants_examples and kind == "example":
            bonus += 0.05

        if profile.read_only and ("isreadonly" in text_low or "read only" in text_low):
            bonus += 0.08
        if profile.output_window and ("ixoutputwindow" in text_low or "output window" in text_low):
            bonus += 0.08

        if chunk.get("source_type") == "pdf":
            bonus -= 0.03

        return bonus

    def _select_diverse_hits(self, fused: List[Dict], chunk_map: Dict[str, Dict], top_k: int) -> List[Dict]:
        selected: List[Dict] = []
        per_source: Dict[str, int] = {}
        deferred: List[Dict] = []

        for item in fused:
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk:
                continue
            source_path = chunk.get("source_path", "")
            used = per_source.get(source_path, 0)
            if used >= 2:
                deferred.append(item)
                continue
            selected.append(item)
            per_source[source_path] = used + 1
            if len(selected) >= top_k:
                return selected

        for item in deferred:
            if len(selected) >= top_k:
                break
            selected.append(item)

        return selected
