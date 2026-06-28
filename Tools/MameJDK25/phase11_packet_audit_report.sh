#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
if [ "${1:-}" != "" ]; then
  ROOT="$1"
fi
python3 "$(dirname "$0")/phase11_packet_audit_report.py" "$ROOT"
