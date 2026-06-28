#!/usr/bin/env python3
"""Summarize 7.x packet audit logs generated under log/packets."""
from __future__ import annotations

import collections
import re
import sys
from pathlib import Path

HEADER_RE = re.compile(r"opcode=(0x[0-9A-Fa-f]{4})\s+state=([^\s]+).*?(?:player=([^,\s]+))?")
INVALID_RE = re.compile(r"state=([^\s]+)\s+reason=([^\n]+)")


def summarize_unknown(path: Path) -> None:
    counts: collections.Counter[tuple[str, str]] = collections.Counter()
    players: dict[tuple[str, str], set[str]] = collections.defaultdict(set)
    if not path.exists():
        print(f"unknown packet log not found: {path}")
        return
    for line in path.read_text(errors="replace").splitlines():
        m = HEADER_RE.search(line)
        if not m:
            continue
        opcode, state, player = m.group(1).upper(), m.group(2), m.group(3) or "-"
        key = (opcode, state)
        counts[key] += 1
        players[key].add(player)
    print("== Unknown packet summary ==")
    if not counts:
        print("no unknown packet entries")
        return
    for (opcode, state), count in counts.most_common():
        sample_players = ",".join(sorted(players[(opcode, state)])[:5])
        print(f"{opcode} state={state:<12} count={count:<6} players={sample_players}")


def summarize_invalid(path: Path) -> None:
    counts: collections.Counter[tuple[str, str]] = collections.Counter()
    if not path.exists():
        print(f"invalid packet log not found: {path}")
        return
    for line in path.read_text(errors="replace").splitlines():
        m = INVALID_RE.search(line)
        if not m:
            continue
        state, reason = m.group(1), m.group(2)
        counts[(state, reason)] += 1
    print("\n== Invalid packet summary ==")
    if not counts:
        print("no invalid packet entries")
        return
    for (state, reason), count in counts.most_common():
        print(f"state={state:<12} count={count:<6} reason={reason}")


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(".")
    packet_dir = root / "log" / "packets"
    summarize_unknown(packet_dir / "unknown_packets.log")
    summarize_invalid(packet_dir / "invalid_packets.log")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
