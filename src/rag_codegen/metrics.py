from __future__ import annotations

import math
from typing import Iterable, List, Sequence, Set


def safe_div(a: float, b: float) -> float:
    return a / b if b else 0.0


def precision_recall_f1(pred: Set[str], gold: Set[str]) -> tuple[float, float, float]:
    tp = len(pred.intersection(gold))
    p = safe_div(tp, len(pred))
    r = safe_div(tp, len(gold))
    f1 = safe_div(2 * p * r, p + r) if (p + r) else 0.0
    return p, r, f1


def recall_at_k(ranked_symbols: Sequence[Set[str]], expected: Set[str], k: int) -> float:
    seen: Set[str] = set()
    for symset in ranked_symbols[:k]:
        seen.update(symset.intersection(expected))
    return safe_div(len(seen), len(expected))


def mrr_at_k(ranked_symbols: Sequence[Set[str]], expected: Set[str], k: int) -> float:
    if not expected:
        return 0.0
    for i, symset in enumerate(ranked_symbols[:k], start=1):
        if symset.intersection(expected):
            return 1.0 / i
    return 0.0


def ndcg_at_k(ranked_symbols: Sequence[Set[str]], expected: Set[str], k: int) -> float:
    if not expected:
        return 0.0

    gains: List[float] = []
    for symset in ranked_symbols[:k]:
        rel = 1.0 if symset.intersection(expected) else 0.0
        gains.append(rel)

    dcg = 0.0
    for i, g in enumerate(gains, start=1):
        dcg += g / math.log2(i + 1)

    ideal_rels = [1.0] * min(k, len(expected))
    idcg = 0.0
    for i, g in enumerate(ideal_rels, start=1):
        idcg += g / math.log2(i + 1)

    return safe_div(dcg, idcg)


def hallucination_rate(used_symbols: Iterable[str], valid_symbols: Set[str]) -> float:
    used = [s for s in used_symbols if s]
    if not used:
        return 0.0
    invalid = [s for s in used if s not in valid_symbols]
    return safe_div(len(invalid), len(used))


def requirement_coverage_from_checklist(output_text: str, checklist: Sequence[str]) -> float:
    if not checklist:
        return 0.0
    low = output_text.lower()
    hits = 0
    for item in checklist:
        if item.lower() in low:
            hits += 1
    return safe_div(hits, len(checklist))
