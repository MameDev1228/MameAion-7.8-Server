#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
for d in AL-Commons AL-Login AL-Game AL-Chat; do
  if [ -f "$ROOT/$d/libs/javassist-3.15.0-GA.jar" ]; then
    rm -f "$ROOT/$d/libs/javassist-3.15.0-GA.jar"
    echo "[OK] Removed $d/libs/javassist-3.15.0-GA.jar"
  fi
done
echo "[DONE] Runtime source libs cleanup complete."
