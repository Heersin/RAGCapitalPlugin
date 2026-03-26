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
        expansion_budget = max(24, top_k * 4)
        planner = self._run_retrieval_planner(
            profile=profile,
            plugin_type=plugin_type,
            plugin_types=allowed_types,
            mode=mode,
            top_k=top_k,
        )

        general_hits = self._stage_search(
            profile=profile,
            plugin_type=plugin_type,
            plugin_types=allowed_types,
            mode=mode,
            query_text=profile.retrieval_query,
            limit=candidate_limit,
        )
        api_hits = self._scale_hits(
            self._stage_search(
                profile=build_query_profile(" ".join(profile.api_terms[:16]), plugin_type),
                plugin_type=plugin_type,
                plugin_types=allowed_types,
                mode=mode,
                query_text=" ".join(profile.api_terms[:16]),
                limit=max(24, top_k * 4),
                source_types=["javadoc_html"],
            ),
            factor=1.15,
            reason="api_lookup",
        )
        example_hits = self._scale_hits(
            self._stage_search(
                profile=build_query_profile(" ".join(profile.example_terms[:14]), plugin_type),
                plugin_type=plugin_type,
                plugin_types=allowed_types,
                mode=mode,
                query_text=" ".join(profile.example_terms[:14]),
                limit=max(16, top_k * 3),
                source_types=["java_example"],
            ),
            factor=0.45,
            reason="example_lookup",
        )
        guide_hits = self._scale_hits(
            self._stage_search(
                profile=build_query_profile(" ".join(profile.english_terms[:14]), plugin_type),
                plugin_type=plugin_type,
                plugin_types=allowed_types,
                mode=mode,
                query_text=" ".join(profile.english_terms[:14]),
                limit=max(12, top_k * 2),
                source_types=["pdf"],
            ),
            factor=0.4,
            reason="guide_lookup",
        )

        fused = self._merge_ranked_hits(general_hits, api_hits)
        fused = self._merge_ranked_hits(fused, example_hits)
        fused = self._merge_ranked_hits(fused, guide_hits)
        fused = self._merge_ranked_hits(fused, planner["combined_hits"])
        fused_ids = [x["chunk_id"] for x in fused]
        chunk_map = self.storage.get_chunks_by_ids(fused_ids)
        expanded = self._expand_neighborhood(
            query=query,
            plugin_types=allowed_types,
            profile=profile,
            initial_hits=fused,
            initial_chunk_map=chunk_map,
            top_k=expansion_budget,
        )
        expanded_seed = self._merge_ranked_hits(fused, expanded)
        expanded_seed_ids = [x["chunk_id"] for x in expanded_seed[: max(20, top_k * 4)]]
        expanded_seed_map = self.storage.get_chunks_by_ids(expanded_seed_ids)
        expanded_second_hop = self._scale_hits(
            self._expand_neighborhood(
                query=query,
                plugin_types=allowed_types,
                profile=profile,
                initial_hits=expanded_seed[: max(20, top_k * 4)],
                initial_chunk_map=expanded_seed_map,
                top_k=expansion_budget,
            ),
            factor=0.7,
            reason="second_hop",
        )
        combined = self._merge_ranked_hits(expanded_seed, expanded_second_hop)
        combined_ids = [x["chunk_id"] for x in combined]
        chunk_map = self.storage.get_chunks_by_ids(combined_ids)
        selected = self._select_diverse_hits(combined, chunk_map, top_k, profile)

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
        reasoning_cards = self._build_reasoning_cards(profile, selected, chunk_map)

        trace = {
            "mode": mode,
            "plugin_type": plugin_type,
            "route_confidence": confidence,
            "retrieval_query": profile.retrieval_query,
            "query_expansions": profile.expansions[:12],
            "plan": {
                "subgoals": profile.subgoals,
                "english_terms": profile.english_terms[:12],
                "api_terms": profile.api_terms[:14],
                "relation_terms": profile.relation_terms[:14],
                "example_terms": profile.example_terms[:12],
                "target_symbols": profile.target_symbols[:12],
                "access_path_terms": profile.access_path_terms[:12],
                "attribute_names": profile.attribute_names[:8],
            },
            "stage_candidates": {
                "general_lookup": len(general_hits),
                "api_lookup": len(api_hits),
                "example_lookup": len(example_hits),
                "guide_lookup": len(guide_hits),
                "planner": len(planner["combined_hits"]),
            },
            "planner": planner["trace"],
            "fused_candidates": len(fused),
            "expanded_candidates": len(expanded),
            "second_hop_candidates": len(expanded_second_hop),
            "final_candidates": len(combined),
            "selected_titles": [card["title"] for card in cards[:6]],
            "reasoning_cards": reasoning_cards,
        }

        return RetrievalResult(plugin_type=plugin_type, evidence_cards=cards, trace=trace)

    def _run_retrieval_planner(
        self,
        profile: QueryProfile,
        plugin_type: str,
        plugin_types: List[str],
        mode: RetrievalMode,
        top_k: int,
    ) -> Dict:
        stages: Dict[str, Dict] = {}

        def add_stage(name: str, query_text: str, hits: List[Dict]) -> None:
            stages[name] = {
                "query": query_text,
                "hits": hits,
                "titles": self._top_titles(hits, limit=6),
            }

        target_query = " ".join(profile.target_symbols[:8] + profile.relation_terms[:8])
        target_hits = self._scale_hits(
            self._stage_search(
                profile=build_query_profile(target_query or profile.raw_query, plugin_type),
                plugin_type=plugin_type,
                plugin_types=plugin_types,
                mode=mode,
                query_text=target_query,
                limit=max(20, top_k * 4),
                source_types=["javadoc_html"],
            ),
            factor=0.95,
            reason="planner_target",
        )
        add_stage("target_lookup", target_query, target_hits)

        interface_query = " ".join(profile.target_symbols[:6] + profile.api_terms[:10] + ["interface", plugin_type])
        interface_hits = self._scale_hits(
            self._stage_search(
                profile=build_query_profile(interface_query or profile.raw_query, plugin_type),
                plugin_type=plugin_type,
                plugin_types=plugin_types,
                mode=mode,
                query_text=interface_query,
                limit=max(18, top_k * 4),
                source_types=["javadoc_html"],
            ),
            factor=0.88,
            reason="planner_interface",
        )
        add_stage("interface_lookup", interface_query, interface_hits)

        access_query = " ".join(profile.target_symbols[:6] + profile.access_path_terms[:10] + ["selection", "current"])
        access_hits = self._scale_hits(
            self._stage_search(
                profile=build_query_profile(access_query or profile.raw_query, plugin_type),
                plugin_type=plugin_type,
                plugin_types=plugin_types,
                mode=mode,
                query_text=access_query,
                limit=max(18, top_k * 4),
                source_types=["javadoc_html"],
            ),
            factor=0.92,
            reason="planner_access",
        )
        add_stage("access_path_lookup", access_query, access_hits)

        attribute_query = " ".join(profile.target_symbols[:4] + ["IXObject", "IXAttributes", "getAttribute"] + profile.attribute_names[:4])
        attribute_hits = self._scale_hits(
            self._filter_attribute_hits(
                self._stage_search(
                    profile=build_query_profile(attribute_query or profile.raw_query, plugin_type),
                    plugin_type=plugin_type,
                    plugin_types=plugin_types,
                    mode=mode,
                    query_text=attribute_query,
                    limit=max(16, top_k * 4),
                    source_types=["javadoc_html"],
                ),
                profile,
                plugin_types,
            ),
            factor=0.94,
            reason="planner_attribute",
        )
        add_stage("attribute_lookup", attribute_query, attribute_hits)

        relation_hits = self._build_relation_chain_hits(
            profile=profile,
            plugin_types=plugin_types,
            top_k=top_k,
            mode=mode,
            plugin_type=plugin_type,
        )
        add_stage("relation_chain", "typed relation expansion", relation_hits)

        combined: List[Dict] = []
        for stage in stages.values():
            combined = self._merge_ranked_hits(combined, stage["hits"])

        return {
            "combined_hits": combined,
            "trace": {
                name: {
                    "query": stage["query"],
                    "candidates": len(stage["hits"]),
                    "titles": stage["titles"],
                }
                for name, stage in stages.items()
            },
        }

    def _filter_attribute_hits(
        self,
        hits: List[Dict],
        profile: QueryProfile,
        plugin_types: List[str],
    ) -> List[Dict]:
        if not hits or not profile.attribute_names:
            return hits

        chunk_map = self.storage.get_chunks_by_ids([item["chunk_id"] for item in hits])
        exact_titles = {f"IXAttributes.{name}" for name in profile.attribute_names[:6]}
        keeper_titles = exact_titles.union(
            {
                "IXAttributes",
                "IXObject",
                "IXObject.getAttribute",
                "IXObject.getAttributes",
                "IXAttributeSetter.getAttributes",
            }
        )
        filtered: List[Dict] = []
        for item in hits:
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk:
                continue
            title = chunk.get("title", "")
            if title in keeper_titles or any(name.lower() in title.lower() for name in profile.attribute_names):
                filtered.append(item)

        if filtered:
            return filtered

        add_back = self.storage.get_chunk_ids_for_titles(sorted(keeper_titles), plugin_types=plugin_types, limit=12)
        fallback = []
        seen = set()
        for chunk_id in add_back + [item["chunk_id"] for item in hits]:
            if chunk_id in seen:
                continue
            seen.add(chunk_id)
            fallback.append({"chunk_id": chunk_id, "score": 0.72, "reason": "attribute_filter_fallback", "reasons": ["attribute_filter_fallback"]})
        return fallback

    def _build_relation_chain_hits(
        self,
        profile: QueryProfile,
        plugin_types: List[str],
        top_k: int,
        mode: RetrievalMode,
        plugin_type: str,
    ) -> List[Dict]:
        if not profile.target_symbols and not profile.access_path_terms:
            return []

        chain_hits: Dict[str, Dict] = {}

        def add_hits(chunk_ids: List[str], score: float, reason: str) -> None:
            for chunk_id in chunk_ids:
                row = chain_hits.setdefault(
                    chunk_id,
                    {"chunk_id": chunk_id, "score": 0.0, "reason": reason, "reasons": [reason]},
                )
                row["score"] += score
                row["reason"] = reason
                row["reasons"] = sorted(set(row.get("reasons", []) + [reason]))

        add_hits(
            self.storage.get_chunk_ids_for_class_names(profile.target_symbols, plugin_types=plugin_types, limit=max(12, top_k * 3)),
            0.92,
            "target_class",
        )
        add_hits(
            self.storage.get_related_chunk_ids(profile.target_symbols, plugin_types=plugin_types, limit=max(24, top_k * 6)),
            0.84,
            "type_chain",
        )
        add_hits(
            self.storage.get_chunk_ids_for_method_names(
                profile.access_path_terms,
                plugin_types=plugin_types,
                limit=max(18, top_k * 4),
            ),
            0.76,
            "access_path",
        )

        if profile.attribute_names:
            attribute_titles = [f"IXAttributes.{name}" for name in profile.attribute_names[:4]]
            add_hits(
                self.storage.get_chunk_ids_for_titles(attribute_titles, plugin_types=plugin_types, limit=max(8, top_k * 2)),
                1.05,
                "attribute_constant",
            )
            attribute_query = " ".join(["IXAttributes", "getAttribute"] + profile.attribute_names[:4])
            attribute_hits = self._filter_attribute_hits(
                self._stage_search(
                    profile=build_query_profile(attribute_query, plugin_type),
                    plugin_type=plugin_type,
                    plugin_types=plugin_types,
                    mode=mode,
                    query_text=attribute_query,
                    limit=max(16, top_k * 3),
                    source_types=["javadoc_html"],
                ),
                profile,
                plugin_types,
            )
            for item in attribute_hits:
                row = chain_hits.setdefault(
                    item["chunk_id"],
                    {"chunk_id": item["chunk_id"], "score": 0.0, "reason": "attribute_chain", "reasons": ["attribute_chain"]},
                )
                row["score"] += float(item.get("score", 0.0)) * 0.6 + 0.24
                row["reason"] = "attribute_chain"

        out = list(chain_hits.values())
        out.sort(key=lambda item: item["score"], reverse=True)
        return out

    def _stage_search(
        self,
        profile: QueryProfile,
        plugin_type: str,
        plugin_types: List[str],
        mode: RetrievalMode,
        query_text: str,
        limit: int,
        source_types: Optional[List[str]] = None,
        kinds: Optional[List[str]] = None,
    ) -> List[Dict]:
        if not query_text.strip():
            return []

        bm25_hits = []
        if mode in {"hybrid"}:
            bm25_hits = self.storage.search_bm25(query_text, plugin_types, limit=limit)

        dense_hits = []
        if mode in {"dense", "hybrid"}:
            dense_hits = self._dense_search(query_text, plugin_types, limit=limit)

        fused = self._rrf_fuse(bm25_hits, dense_hits, profile, plugin_type, k=self.settings.rrf_k)
        if not source_types and not kinds:
            return fused

        chunk_map = self.storage.get_chunks_by_ids([item["chunk_id"] for item in fused])
        filtered: List[Dict] = []
        for item in fused:
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk:
                continue
            if source_types and chunk.get("source_type") not in source_types:
                continue
            if kinds and chunk.get("meta", {}).get("kind") not in kinds:
                continue
            filtered.append(item)
        return filtered

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
            row = by_id.setdefault(
                cid,
                {"chunk_id": cid, "score": 0.0, "bm25_rank": None, "dense_rank": None, "reasons": []},
            )
            row["bm25_rank"] = hit["rank"]
            row["score"] += 1.0 / (k + hit["rank"])
            row["reasons"] = sorted(set(row.get("reasons", []) + ["bm25"]))

        for hit in dense_hits:
            cid = hit["chunk_id"]
            row = by_id.setdefault(
                cid,
                {"chunk_id": cid, "score": 0.0, "bm25_rank": None, "dense_rank": None, "reasons": []},
            )
            row["dense_rank"] = hit["rank"]
            row["score"] += 1.0 / (k + hit["rank"])
            row["reasons"] = sorted(set(row.get("reasons", []) + ["dense"]))

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

    def _expand_neighborhood(
        self,
        query: str,
        plugin_types: List[str],
        profile: QueryProfile,
        initial_hits: List[Dict],
        initial_chunk_map: Dict[str, Dict],
        top_k: int,
    ) -> List[Dict]:
        if not initial_hits:
            return []

        seed_hits = initial_hits[: max(4, min(10, top_k * 2))]
        source_paths = []
        symbols = set()
        class_names = set()
        method_names = set()

        for hit in seed_hits:
            chunk = initial_chunk_map.get(hit["chunk_id"])
            if not chunk:
                continue
            source_paths.append(chunk.get("source_path", ""))
            symbols.update(chunk.get("symbols", [])[:16])
            if chunk.get("class_name"):
                class_names.add(chunk["class_name"])
            if chunk.get("method_name"):
                method_names.add(chunk["method_name"])
            class_names.update([tok for tok in tokenize(chunk.get("signature", "")) if tok.startswith("IX")])
            class_names.update([tok for tok in tokenize(chunk.get("text", "")) if tok.startswith("IX")][:12])
            meta = chunk.get("meta", {}) or {}
            class_names.update(meta.get("super_types", [])[:12])
            class_names.update(meta.get("related_types", [])[:12])
            for owner, methods in (meta.get("inherited_methods", {}) or {}).items():
                class_names.add(owner)
                method_names.update(methods[:16])
            class_names.update(meta.get("return_types", [])[:12])
            class_names.update(meta.get("param_types", [])[:12])

        symbols.update([tok for tok in tokenize(query) if tok.startswith("IX")])
        symbols.update([tok for tok in profile.query_tokens if tok.startswith("IX")])
        symbols.update(profile.target_symbols[:12])
        class_names.update(profile.target_symbols[:12])
        method_names.update(profile.access_path_terms[:12])
        method_names.update(
            [
                tok
                for tok in tokenize(query)
                if tok in {"execute", "check", "begin", "end", "println", "isAvailable", "isReadOnly", "getTriggers"}
            ]
        )

        expanded_by_id: Dict[str, Dict] = {}

        related_chunks = self.storage.get_chunks_by_source_paths(source_paths, plugin_types=plugin_types)
        for chunk in related_chunks:
            reason_bonus = 0.12 if chunk.get("meta", {}).get("kind") == "class" else 0.08
            expanded_by_id[chunk["chunk_id"]] = {
                "chunk_id": chunk["chunk_id"],
                "score": reason_bonus,
                "reason": "same_source",
            }

        for chunk_id in self.storage.get_chunk_ids_for_symbols(symbols, plugin_types=plugin_types, limit=max(30, top_k * 6)):
            row = expanded_by_id.setdefault(
                chunk_id,
                {"chunk_id": chunk_id, "score": 0.0, "reason": "symbol_link"},
            )
            row["score"] += 0.09

        for chunk_id in self.storage.get_chunk_ids_for_class_names(
            class_names,
            plugin_types=plugin_types,
            limit=max(20, top_k * 4),
        ):
            row = expanded_by_id.setdefault(
                chunk_id,
                {"chunk_id": chunk_id, "score": 0.0, "reason": "class_link"},
            )
            row["score"] += 0.1

        for chunk_id in self.storage.get_chunk_ids_for_method_names(
            method_names,
            plugin_types=plugin_types,
            limit=max(24, top_k * 5),
        ):
            row = expanded_by_id.setdefault(
                chunk_id,
                {"chunk_id": chunk_id, "score": 0.0, "reason": "method_link"},
            )
            row["score"] += 0.08

        for hit in seed_hits:
            expanded_by_id.pop(hit["chunk_id"], None)

        expanded = list(expanded_by_id.values())
        expanded.sort(key=lambda x: x["score"], reverse=True)
        return expanded

    def _merge_ranked_hits(self, primary: List[Dict], expanded: List[Dict]) -> List[Dict]:
        merged: Dict[str, Dict] = {}
        for item in primary:
            merged[item["chunk_id"]] = dict(item)
        for item in expanded:
            row = merged.setdefault(
                item["chunk_id"],
                {"chunk_id": item["chunk_id"], "score": 0.0, "bm25_rank": None, "dense_rank": None, "reasons": []},
            )
            row["score"] += item["score"]
            if "reason" in item:
                row["reason"] = item["reason"]
            if "reasons" in item:
                row["reasons"] = sorted(set(row.get("reasons", []) + item["reasons"]))
            elif "reason" in item:
                row["reasons"] = sorted(set(row.get("reasons", []) + [item["reason"]]))
        out = list(merged.values())
        out.sort(key=lambda x: x["score"], reverse=True)
        return out

    def _scale_hits(self, hits: List[Dict], factor: float, reason: str) -> List[Dict]:
        scaled: List[Dict] = []
        for item in hits:
            row = dict(item)
            row["score"] = float(row.get("score", 0.0)) * factor
            row["reason"] = reason
            row["reasons"] = sorted(set(row.get("reasons", []) + [reason]))
            scaled.append(row)
        return scaled

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
        title_token_set = {tok.lower() for tok in title_tokens}
        text_low = chunk.get("text", "").lower()
        meta = chunk.get("meta", {}) or {}
        kind = meta.get("kind", "")
        bonus = 0.0

        bonus += 0.18 * keyword_overlap_score(query_tokens, title_tokens)
        explicit_api_symbols = [term.lower() for term in profile.api_terms if term.startswith("IX")]
        symbol_hits = sum(1 for term in explicit_api_symbols if term.lower() in title_token_set)
        bonus += min(0.24, 0.06 * symbol_hits)
        target_symbol_hits = sum(1 for term in profile.target_symbols if term.lower() in title_token_set)
        bonus += min(0.32, 0.1 * target_symbol_hits)

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
        if profile.output_window and (
            "ixoutputwindow" in title_token_set or "println" in title_token_set or "getoutputwindow" in title_token_set
        ):
            bonus += 0.24
        if profile.attribute_names and ("getattribute" in title_token_set or "ixattributes" in title_token_set):
            bonus += 0.18
        if profile.access_path_terms and any(term.lower() == chunk.get("method_name", "").lower() for term in profile.access_path_terms):
            bonus += 0.18

        if chunk.get("source_type") == "pdf":
            bonus -= 0.03

        return bonus

    def _select_diverse_hits(
        self,
        fused: List[Dict],
        chunk_map: Dict[str, Dict],
        top_k: int,
        profile: QueryProfile,
    ) -> List[Dict]:
        selected: List[Dict] = []
        per_source: Dict[str, int] = {}
        deferred: List[Dict] = []
        selected_ids = set()
        selected_titles = set()
        api_quota = min(top_k, max(3, top_k // 2))
        example_quota = max(2, top_k // 3) if profile.wants_examples else max(1, top_k // 4)

        def _try_select(item: Dict) -> bool:
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk:
                return False
            source_path = chunk.get("source_path", "")
            used = per_source.get(source_path, 0)
            if used >= 2:
                deferred.append(item)
                return False
            title = chunk.get("title") or item["chunk_id"]
            if title in selected_titles:
                deferred.append(item)
                return False
            if profile.attribute_names and title.startswith("IXAttributes.") and title != "IXAttributes":
                if not any(name.lower() in title.lower() for name in profile.attribute_names):
                    deferred.append(item)
                    return False
            example_count = sum(
                1 for existing in selected if chunk_map.get(existing["chunk_id"], {}).get("source_type") == "java_example"
            )
            if chunk.get("source_type") == "java_example" and example_count >= example_quota:
                deferred.append(item)
                return False
            selected.append(item)
            selected_ids.add(item["chunk_id"])
            selected_titles.add(title)
            per_source[source_path] = used + 1
            return True

        priority_reasons = [
            "planner_target",
            "planner_access",
            "planner_attribute",
            "planner_interface",
            "attribute_constant",
            "type_chain",
        ]
        for reason in priority_reasons:
            if len(selected) >= min(top_k, 5):
                break
            for item in fused:
                if item["chunk_id"] in selected_ids:
                    continue
                reasons = item.get("reasons", [])
                if reason not in reasons:
                    continue
                if _try_select(item):
                    break

        for item in fused:
            if len(selected) >= api_quota:
                break
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk or chunk.get("source_type") != "javadoc_html":
                continue
            _try_select(item)

        for item in fused:
            if item["chunk_id"] in selected_ids:
                continue
            _try_select(item)
            if len(selected) >= top_k:
                return selected

        for item in deferred:
            if len(selected) >= top_k:
                break
            if item["chunk_id"] in selected_ids:
                continue
            selected.append(item)
            selected_ids.add(item["chunk_id"])
            chunk = chunk_map.get(item["chunk_id"], {})
            selected_titles.add(chunk.get("title") or item["chunk_id"])

        return selected

    def _top_titles(self, hits: List[Dict], limit: int = 6) -> List[str]:
        if not hits:
            return []
        chunk_map = self.storage.get_chunks_by_ids([item["chunk_id"] for item in hits[:limit]])
        titles: List[str] = []
        for item in hits:
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk:
                continue
            titles.append(chunk.get("title") or item["chunk_id"])
            if len(titles) >= limit:
                break
        return titles

    def _build_reasoning_cards(
        self,
        profile: QueryProfile,
        selected: List[Dict],
        chunk_map: Dict[str, Dict],
    ) -> List[Dict]:
        ordered_chunks: List[Dict] = []
        for item in selected:
            chunk = chunk_map.get(item["chunk_id"])
            if not chunk:
                continue
            ordered_chunks.append(chunk)

        def pick_titles(predicate, limit: int = 4) -> List[str]:
            titles: List[str] = []
            seen = set()
            for chunk in ordered_chunks:
                title = chunk.get("title") or chunk.get("chunk_id")
                if title in seen or not predicate(chunk):
                    continue
                seen.add(title)
                titles.append(title)
                if len(titles) >= limit:
                    break
            return titles

        target_symbol = next((sym for sym in profile.target_symbols if sym not in {"IXObject", "IXAttributes"}), "")
        attribute_name = profile.attribute_names[0] if profile.attribute_names else ""
        target_focus_symbols = [sym for sym in profile.target_symbols if sym not in {"IXObject", "IXAttributes"}]
        if target_symbol.endswith("Design"):
            target_focus_symbols.append(target_symbol + "Action")
        target_titles = pick_titles(
            lambda chunk: any(
                sym.lower() in (chunk.get("title", "") + " " + chunk.get("class_name", "")).lower()
                for sym in target_focus_symbols
            )
        )
        access_method_names = {"getSelectedObjects", "getCurrentDesign"}
        access_titles = pick_titles(
            lambda chunk: chunk.get("method_name") in access_method_names
        )
        attribute_titles = pick_titles(
            lambda chunk: chunk.get("title") in {f"IXAttributes.{name}" for name in profile.attribute_names}
            or chunk.get("title") in {"IXObject.getAttribute", "IXObject.getAttributes", "IXAttributes"}
        )
        output_titles = pick_titles(
            lambda chunk: "outputwindow" in (chunk.get("title", "") + " " + chunk.get("class_name", "")).lower()
            or "println" == chunk.get("method_name", "")
        )

        cards: List[Dict] = []

        if target_titles:
            cards.append(
                {
                    "id": "target_object",
                    "title": "1. 目标对象",
                    "summary": f"当前问题的核心对象更像是 `{target_symbol or 'IXObject'}`，插件接口优先落在 `{target_titles[0]}` 这一侧。",
                    "evidence_titles": target_titles,
                    "suggested_api": target_titles[0],
                }
            )

        if access_titles:
            candidate_calls: List[str] = []
            if target_symbol and "IXBaseCurrentContext.getSelectedObjects" in access_titles:
                candidate_calls.append(f"applicationContext.getSelectedObjects({target_symbol}.class)")
            if target_symbol and "IXCurrentContext.getSelectedObjects" in access_titles:
                candidate_calls.append(f"currentContext.getSelectedObjects({target_symbol}.class)")
            if target_symbol.endswith("Design") and "IXCurrentContext.getCurrentDesign" in access_titles:
                candidate_calls.append(f"{target_symbol} design = ({target_symbol}) applicationContext.getCurrentDesign()")
            cards.append(
                {
                    "id": "access_path",
                    "title": "2. 获取路径",
                    "summary": "先找到能拿到目标对象的方法，再决定后续属性读取是基于当前设计还是当前选中对象。",
                    "evidence_titles": access_titles,
                    "candidate_calls": candidate_calls[:3],
                }
            )

        if attribute_titles:
            candidate_attr_calls: List[str] = []
            if attribute_name:
                candidate_attr_calls.append(f"xObj.getAttribute(IXAttributes.{attribute_name})")
                candidate_attr_calls.append(f'xObj.getAttribute("{attribute_name}")')
            cards.append(
                {
                    "id": "attribute_access",
                    "title": "3. 属性读取",
                    "summary": f"属性读取证据集中在 `IXObject.getAttribute(...)`，而属性常量优先对应 `IXAttributes.{attribute_name}`。" if attribute_name else "属性读取证据集中在 `IXObject.getAttribute(...)`。",
                    "evidence_titles": attribute_titles,
                    "candidate_calls": candidate_attr_calls[:2],
                }
            )

        if output_titles:
            cards.append(
                {
                    "id": "output_path",
                    "title": "4. 输出路径",
                    "summary": "结果输出仍然建议走 output window 这条链。",
                    "evidence_titles": output_titles,
                    "candidate_calls": ["applicationContext.getOutputWindow().println(value)"],
                }
            )

        synthesis_lines: List[str] = []
        if target_symbol:
            synthesis_lines.append(f"实现 `{target_symbol}Action` 一类接口")
        if access_titles:
            synthesis_lines.append("先从 current/selection context 拿到目标对象")
        if attribute_titles and attribute_name:
            synthesis_lines.append(f"再通过 `getAttribute(IXAttributes.{attribute_name})` 读取属性")
        if output_titles:
            synthesis_lines.append("最后用 output window 打印")
        if synthesis_lines:
            cards.append(
                {
                    "id": "synthesis",
                    "title": "5. 组合建议",
                    "summary": " -> ".join(synthesis_lines),
                    "evidence_titles": list(dict.fromkeys(target_titles + access_titles + attribute_titles + output_titles))[:6],
                }
            )

        return cards
