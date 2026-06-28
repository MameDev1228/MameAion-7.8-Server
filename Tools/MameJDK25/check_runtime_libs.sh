#!/usr/bin/env bash
set -u
cd "$(dirname "$0")/../.." || exit 1
ROOT="$PWD"
LOGDIR="$ROOT/build_logs/jdk25"
mkdir -p "$LOGDIR"
LOG="$LOGDIR/runtime_libs_check.log"

{
  echo "=== MameAion 7.8.0 JDK25 Runtime Lib Check ==="
  echo "Root: $ROOT"
  date
} > "$LOG"

JAVA_CMD="${JAVA:-java}"
if [ -n "${JDK25_HOME:-}" ]; then
  JAVA_CMD="$JDK25_HOME/bin/java"
fi

echo "=== Java version ==="
if ! "$JAVA_CMD" -version 2> "$LOGDIR/java_version.tmp"; then
  echo "[ERROR] Java could not be executed. Set JDK25_HOME or JAVA."
  echo "[ERROR] Java could not be executed." >> "$LOG"
  exit 1
fi
cat "$LOGDIR/java_version.tmp"
cat "$LOGDIR/java_version.tmp" >> "$LOG"

HAS_ERROR=0
find_jar() {
  local dir="$1" label="$2" level="$3"
  shift 3
  local found=""
  for pattern in "$@"; do
    for f in "$dir"/$pattern; do
      if [ -f "$f" ]; then
        found="$(basename "$f")"
      fi
    done
  done
  if [ -n "$found" ]; then
    echo "[OK] $label: $found" | tee -a "$LOG"
  else
    if [ "$level" = "required" ]; then
      echo "[ERROR] Missing $label in $dir" | tee -a "$LOG"
      HAS_ERROR=1
    else
      echo "[WARN] Missing optional $label in $dir - database.pool=auto will fallback to BoneCP." | tee -a "$LOG"
    fi
  fi
}

check_module() {
  local mod="$1" lib="$ROOT/$mod/libs"
  echo
  echo "=== $mod libs ===" | tee -a "$LOG"
  if [ ! -d "$lib" ]; then
    echo "[ERROR] $mod libs folder not found: $lib" | tee -a "$LOG"
    HAS_ERROR=1
    return
  fi
  find_jar "$lib" "JAXB API" required 'jaxb-api*.jar'
  find_jar "$lib" "JAXB Runtime" required 'jaxb-runtime*.jar'
  find_jar "$lib" "Java Activation" required 'javax.activation*.jar' 'activation*.jar'
  find_jar "$lib" "MySQL Connector/J" required 'mysql-connector-j*.jar' 'mysql-connector-java*.jar'
  find_jar "$lib" "HikariCP" optional 'HikariCP*.jar'
}

scan_flags() {
  local file="$ROOT/$1"
  [ -f "$file" ] || return
  if grep -Ei 'UseConcMarkSweepGC|UseParNewGC|UseSplitVerifier|Xbootclasspath' "$file" >/dev/null; then
    echo "[ERROR] Old JDK flags remain in $1" | tee -a "$LOG"
    HAS_ERROR=1
  else
    echo "[OK] No removed JDK flags in $1" | tee -a "$LOG"
  fi
}

check_module AL-Login
check_module AL-Game
check_module AL-Chat
scan_flags 'AL-Login/dist/StartLS.bat'
scan_flags 'AL-Game/dist/StartGS.bat'
scan_flags 'AL-Chat/dist/StartCS.bat'
scan_flags 'AL-Login/dist/StartLS.sh'
scan_flags 'AL-Game/dist/StartGS.sh'
scan_flags 'AL-Chat/dist/StartCS.sh'

if [ "$HAS_ERROR" -ne 0 ]; then
  echo
  echo "Runtime dependency check FAILED. See $LOG"
  exit 1
fi

echo
echo "Runtime dependency check OK or WARN-only. See $LOG"
