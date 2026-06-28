#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(cd "$(dirname "$0")/../.." && pwd)}"
python3 "$(dirname "$0")/summarize_packet_audit.py" "$ROOT"
