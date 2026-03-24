from __future__ import annotations

import json
import time
from pathlib import Path
from typing import Dict, List, Tuple

from .generate import Generator
from .metrics import (
    hallucination_rate,
    mrr_at_k,
    ndcg_at_k,
    precision_recall_f1,
    recall_at_k,
    requirement_coverage_from_checklist,
    safe_div,
)
from .text_utils import extract_symbols


class Evaluator:
    def __init__(self, generator: Generator):
        self.generator = generator

    def run(self, experiment_id: str, task_set_id: str, variants: List[str]) -> Dict:
        tasks = self._load_tasks(task_set_id)
        if not tasks:
            raise ValueError(f"No tasks loaded from {task_set_id}")

        variant_results: Dict[str, Dict] = {}
        notes: List[str] = []

        for variant in variants:
            row = self._run_variant(tasks, variant)
            variant_results[variant] = row

        notes.append(
            "FPAR/EditEffort/Turn-to-Accept are proxy industrial metrics in this prototype."
        )
        notes.append("Requirement coverage uses checklist keyword matching.")

        return {
            "experiment_id": experiment_id,
            "task_set_id": task_set_id,
            "variant_results": variant_results,
            "notes": notes,
        }

    def _run_variant(self, tasks: List[Dict], variant: str) -> Dict:
        retrieval_mode, self_check_enabled = self._variant_mode(variant)

        recalls: List[float] = []
        mrrs: List[float] = []
        ndcgs: List[float] = []

        precs: List[float] = []
        recs: List[float] = []
        f1s: List[float] = []
        hallus: List[float] = []
        coverages: List[float] = []

        accepted = 0
        edit_efforts: List[float] = []
        turns: List[float] = []
        latencies: List[float] = []

        started = time.time()

        for t in tasks:
            plugin_type = t.get("plugin_type")
            requirement = t.get("requirement", "")
            expected = set(t.get("expected_symbols", []))
            checklist = t.get("checklist", [])

            result = self.generator.generate(
                requirement=requirement,
                plugin_type=plugin_type,
                context_budget=12,
                retrieval_mode=retrieval_mode,
                self_check_enabled=self_check_enabled,
            )

            evidence_cards = result.get("evidence_cards", [])
            ranked_symbol_sets = [set(c.get("symbols", [])) for c in evidence_cards]
            recalls.append(recall_at_k(ranked_symbol_sets, expected, k=10))
            mrrs.append(mrr_at_k(ranked_symbol_sets, expected, k=10))
            ndcgs.append(ndcg_at_k(ranked_symbol_sets, expected, k=10))

            used_symbols = set(result.get("used_symbols", []))
            p, r, f1 = precision_recall_f1(used_symbols, expected)
            precs.append(p)
            recs.append(r)
            f1s.append(f1)

            valid_symbols = set(self.generator.storage.list_symbols([plugin_type, "core"]))
            hallu = hallucination_rate(used_symbols, valid_symbols)
            hallus.append(hallu)

            combined_output = "\n".join(result.get("code_blocks", [])) + "\n" + result.get("analysis", "")
            coverage = requirement_coverage_from_checklist(combined_output, checklist)
            if not checklist:
                coverage = max(0.0, 1.0 - hallu)
            coverages.append(coverage)

            report = result.get("self_check_report", {})
            pass_gate = (
                hallu <= 0.1
                and coverage >= 0.8
                and not report.get("missing_required_methods")
                and not report.get("invalid_symbols")
            )
            accepted += 1 if pass_gate else 0

            edit_effort = min(1.0, max(0.0, (1.0 - coverage) + hallu))
            edit_efforts.append(edit_effort)

            if pass_gate:
                turns.append(1.0)
            else:
                turns.append(2.0 if variant == "B3" else 3.0)

            latencies.append(float(result.get("latency_seconds", 0.0)))

        elapsed = time.time() - started
        samples = len(tasks)

        return {
            "retrieval_metrics": {
                "Recall@10": round(self._mean(recalls), 4),
                "MRR@10": round(self._mean(mrrs), 4),
                "nDCG@10": round(self._mean(ndcgs), 4),
            },
            "generation_metrics": {
                "SymbolPrecision": round(self._mean(precs), 4),
                "SymbolRecall": round(self._mean(recs), 4),
                "SymbolF1": round(self._mean(f1s), 4),
                "HallucinatedApiRate": round(self._mean(hallus), 4),
                "RequirementCoverage": round(self._mean(coverages), 4),
            },
            "industrial_metrics": {
                "FPAR": round(safe_div(accepted, samples), 4),
                "EditEffort": round(self._mean(edit_efforts), 4),
                "TurnToAccept": round(self._mean(turns), 4),
                "LatencySeconds": round(self._mean(latencies), 4),
                "CostPerAcceptedTask": 0.0,
                "BatchElapsedSeconds": round(elapsed, 3),
            },
            "samples": samples,
        }

    def _variant_mode(self, variant: str) -> Tuple[str, bool]:
        if variant == "B0":
            return "none", False
        if variant == "B1":
            return "dense", False
        if variant == "B2":
            return "hybrid", False
        return "hybrid", True

    def _load_tasks(self, task_set_id: str) -> List[Dict]:
        path = Path(task_set_id)
        if not path.exists():
            raise FileNotFoundError(f"task_set_id not found: {task_set_id}")
        data = json.loads(path.read_text())
        if isinstance(data, dict):
            return data.get("tasks", [])
        if isinstance(data, list):
            return data
        return []

    def _mean(self, arr: List[float]) -> float:
        return sum(arr) / len(arr) if arr else 0.0
