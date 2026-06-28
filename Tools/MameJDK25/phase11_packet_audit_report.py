#!/usr/bin/env python3
"""Build a Phase11 opcode report from 7.8 runtime packet audit logs.

Inputs:
  log/packets/unknown_packets.log
  log/packets/invalid_packets.log
  log/packets/packet_audit.tsv
  AL-Game/src/.../AionPacketHandlerFactory.java

Outputs:
  log/packets/phase11_opcode_report.md
  log/packets/phase11_opcode_candidates.tsv
"""
from __future__ import annotations

import collections
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path
from typing import Iterable

UNKNOWN_RE = re.compile(r"opcode=(0x[0-9A-Fa-f]{4})\s+state=([^\s]+).*?(?:player=([^,\s]+))?")
INVALID_RE = re.compile(r"state=([^\s]+)\s+reason=([^\n]+)")
FACTORY_RE = re.compile(r"addPacket\(new\s+([A-Za-z0-9_]+)\((0x[0-9A-Fa-f]+),\s*State\.([A-Z_]+)")
FIELD_RE = re.compile(r"([A-Za-z0-9_]+)=([^\t]+)")


@dataclass
class AuditGroup:
    count: int = 0
    players: set[str] = field(default_factory=set)
    notes: collections.Counter[str] = field(default_factory=collections.Counter)
    actions: collections.Counter[str] = field(default_factory=collections.Counter)
    first_seen: str = ""
    last_seen: str = ""

    def add(self, stamp: str, player: str, note: str = "", action: str = "") -> None:
        self.count += 1
        if player and player != "-":
            self.players.add(player)
        if note:
            self.notes[note] += 1
        if action:
            self.actions[action] += 1
        if not self.first_seen:
            self.first_seen = stamp
        self.last_seen = stamp


def parse_factory(root: Path) -> dict[str, tuple[str, str]]:
    factory = root / "AL-Game" / "src" / "com" / "aionemu" / "gameserver" / "network" / "factories" / "AionPacketHandlerFactory.java"
    result: dict[str, tuple[str, str]] = {}
    if not factory.exists():
        return result
    for line in factory.read_text(errors="replace").splitlines():
        m = FACTORY_RE.search(line)
        if m:
            cls, opcode, state = m.groups()
            result[opcode.upper().replace("0X", "0x")] = (cls, state)
    return result


def parse_packet_audit(path: Path) -> dict[tuple[str, str, str], AuditGroup]:
    groups: dict[tuple[str, str, str], AuditGroup] = collections.defaultdict(AuditGroup)
    if not path.exists():
        return groups
    for line in path.read_text(errors="replace").splitlines():
        if not line.strip():
            continue
        stamp = line.split("\t", 1)[0]
        fields = dict(FIELD_RE.findall(line))
        opcode = fields.get("opcode", "-").upper().replace("0X", "0x")
        packet = fields.get("packet", "-")
        typ = fields.get("type", "-")
        note = fields.get("note", "")
        player = fields.get("player", "-")
        action = fields.get("action", "")
        groups[(opcode, packet, typ)].add(stamp, player, note, action)
    return groups


def parse_unknown(path: Path) -> dict[tuple[str, str], AuditGroup]:
    groups: dict[tuple[str, str], AuditGroup] = collections.defaultdict(AuditGroup)
    if not path.exists():
        return groups
    for line in path.read_text(errors="replace").splitlines():
        m = UNKNOWN_RE.search(line)
        if not m:
            continue
        opcode, state, player = m.groups()
        opcode = opcode.upper().replace("0X", "0x")
        groups[(opcode, state)].add("", player or "-", "unknown_packet", "")
    return groups


def parse_invalid(path: Path) -> collections.Counter[tuple[str, str]]:
    counts: collections.Counter[tuple[str, str]] = collections.Counter()
    if not path.exists():
        return counts
    for line in path.read_text(errors="replace").splitlines():
        m = INVALID_RE.search(line)
        if m:
            counts[(m.group(1), m.group(2))] += 1
    return counts


def top(counter: collections.Counter[str], n: int = 3) -> str:
    if not counter:
        return "-"
    return ", ".join(f"{k}:{v}" for k, v in counter.most_common(n))


def suggest(opcode: str, packet: str, typ: str, group: AuditGroup, registry: dict[str, tuple[str, str]]) -> tuple[str, str]:
    registered = registry.get(opcode)
    if typ == "UNKNOWN":
        if registered:
            return ("opcode_collision_or_header_shift", f"Registered as {registered[0]} but reached UNKNOWN; check 7.8 header/dispatch state")
        return ("create_named_clientpacket", "Add safe CM_* parser after reproducing one UI action at a time")
    if packet.startswith("CM_UNK"):
        return ("rename_registered_packet", "Promote CM_UNK_* to a descriptive CM_* class once action notes are stable")
    if "unmapped" in top(group.notes, 10):
        return ("implement_action_variant", "Known packet has unmapped action variants")
    return ("watch", "No immediate action; keep sampling")


def write_outputs(root: Path, registry: dict[str, tuple[str, str]], unknown: dict[tuple[str, str], AuditGroup], invalid: collections.Counter[tuple[str, str]], audit: dict[tuple[str, str, str], AuditGroup]) -> None:
    out_dir = root / "log" / "packets"
    out_dir.mkdir(parents=True, exist_ok=True)
    md = out_dir / "phase11_opcode_report.md"
    tsv = out_dir / "phase11_opcode_candidates.tsv"

    with tsv.open("w", encoding="utf-8") as f:
        f.write("opcode\tpacket\ttype\tcount\tplayers\tnotes\tactions\trecommendation\treason\n")
        for (opcode, packet, typ), group in sorted(audit.items(), key=lambda item: item[1].count, reverse=True):
            rec, reason = suggest(opcode, packet, typ, group, registry)
            f.write("\t".join([
                opcode, packet, typ, str(group.count), ",".join(sorted(group.players)[:8]) or "-", top(group.notes, 5), top(group.actions, 5), rec, reason
            ]) + "\n")
        for (opcode, state), group in sorted(unknown.items(), key=lambda item: item[1].count, reverse=True):
            registered = registry.get(opcode, ("-", state))[0]
            f.write("\t".join([opcode, registered, "UNKNOWN_LEGACY", str(group.count), ",".join(sorted(group.players)[:8]) or "-", "unknown_packet", "-", "create_named_clientpacket", "Unregistered opcode in unknown_packets.log"]) + "\n")

    with md.open("w", encoding="utf-8") as f:
        f.write("# Phase11 7.8 Opcode Audit Report\n\n")
        f.write("## Registered opcode count\n\n")
        f.write(f"{len(registry)} registered client opcodes parsed from AionPacketHandlerFactory.\n\n")
        f.write("## Highest-priority audit groups\n\n")
        f.write("| opcode | packet | type | count | notes | actions | recommendation |\n")
        f.write("|---|---|---:|---:|---|---|---|\n")
        for (opcode, packet, typ), group in sorted(audit.items(), key=lambda item: item[1].count, reverse=True)[:50]:
            rec, reason = suggest(opcode, packet, typ, group, registry)
            f.write(f"| {opcode} | {packet} | {typ} | {group.count} | {top(group.notes)} | {top(group.actions)} | {rec}: {reason} |\n")
        f.write("\n## Unknown packet log summary\n\n")
        if not unknown:
            f.write("No legacy unknown_packets.log entries found.\n")
        else:
            f.write("| opcode | state | count | players | registered-name |\n")
            f.write("|---|---|---:|---|---|\n")
            for (opcode, state), group in sorted(unknown.items(), key=lambda item: item[1].count, reverse=True):
                registered = registry.get(opcode, ("-", state))[0]
                f.write(f"| {opcode} | {state} | {group.count} | {','.join(sorted(group.players)[:8]) or '-'} | {registered} |\n")
        f.write("\n## Invalid packet summary\n\n")
        if not invalid:
            f.write("No invalid packet entries found.\n")
        else:
            f.write("| state | count | reason |\n|---|---:|---|\n")
            for (state, reason), count in invalid.most_common(30):
                f.write(f"| {state} | {count} | {reason} |\n")
        f.write("\n## Next workflow\n\n")
        f.write("1. Reproduce one client UI/system at a time.\n")
        f.write("2. Run `Tools/MameJDK25/phase11_packet_audit_report.bat`.\n")
        f.write("3. Open `log/packets/phase11_opcode_candidates.tsv`.\n")
        f.write("4. Promote repeated UNKNOWN or unmapped action groups into named CM_* classes/actions.\n")


def main(argv: list[str]) -> int:
    root = Path(argv[1]) if len(argv) > 1 else Path(".")
    registry = parse_factory(root)
    packet_dir = root / "log" / "packets"
    unknown = parse_unknown(packet_dir / "unknown_packets.log")
    invalid = parse_invalid(packet_dir / "invalid_packets.log")
    audit = parse_packet_audit(packet_dir / "packet_audit.tsv")
    write_outputs(root, registry, unknown, invalid, audit)
    print(f"Phase11 report written to: {packet_dir / 'phase11_opcode_report.md'}")
    print(f"Phase11 candidates written to: {packet_dir / 'phase11_opcode_candidates.tsv'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
