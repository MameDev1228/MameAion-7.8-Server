#!/usr/bin/env bash
set -u

APP_NAME="GameServer"
MAIN_CLASS="com.aionemu.gameserver.GameServer"
PID_FILE="gameserver.pid"
SUPERVISOR_PID_FILE="gameserver.supervisor.pid"
LOG_DIR="log"
CONSOLE_LOG="$LOG_DIR/console.log"
LAUNCHER_LOG="$LOG_DIR/launcher.log"

# MameAion75 Linux JDK25 launcher
# Default memory: Xms 4G / Xmx 11G
JAVA_OPTS_DEFAULT="-Xms4G -Xmx11G -server -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=log/oom_gameserver.hprof"
JDK25_OPTS="-Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8 -Duser.timezone=Asia/Tokyo --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED"

die() { echo "[ERROR] $*" >&2; exit 1; }

resolve_java() {
  if [ -n "${JDK25_HOME:-}" ] && [ -x "$JDK25_HOME/bin/java" ]; then echo "$JDK25_HOME/bin/java"; return 0; fi
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then echo "$JAVA_HOME/bin/java"; return 0; fi
  for j in \
    /usr/lib/jvm/temurin-25-jdk-amd64/bin/java \
    /usr/lib/jvm/jdk-25*/bin/java \
    /usr/lib/jvm/java-25*/bin/java \
    /opt/jdk-25*/bin/java; do
    for candidate in $j; do
      [ -x "$candidate" ] && echo "$candidate" && return 0
    done
  done
  command -v java 2>/dev/null && return 0
  return 1
}

ensure_dirs() {
  mkdir -p "$LOG_DIR" "$LOG_DIR/backup"
}

rotate_console_log() {
  ensure_dirs
  if [ -f "$CONSOLE_LOG" ]; then
    mv "$CONSOLE_LOG" "$LOG_DIR/backup/$(date +%Y-%m-%d_%H-%M-%S)_console.log"
  fi
}

is_pid_running() {
  local pf="$1"
  [ -f "$pf" ] || return 1
  local pid
  pid="$(cat "$pf" 2>/dev/null || true)"
  [ -n "$pid" ] || return 1
  kill -0 "$pid" 2>/dev/null
}

status() {
  if is_pid_running "$SUPERVISOR_PID_FILE"; then
    echo "$APP_NAME supervisor running: $(cat "$SUPERVISOR_PID_FILE")"
  else
    echo "$APP_NAME supervisor stopped"
  fi
  if is_pid_running "$PID_FILE"; then
    echo "$APP_NAME java process running: $(cat "$PID_FILE")"
  else
    echo "$APP_NAME java process stopped"
  fi
}

stop_server() {
  echo "Stopping $APP_NAME..."
  if is_pid_running "$SUPERVISOR_PID_FILE"; then kill "$(cat "$SUPERVISOR_PID_FILE")" 2>/dev/null || true; fi
  if is_pid_running "$PID_FILE"; then kill "$(cat "$PID_FILE")" 2>/dev/null || true; fi
  sleep 2
  if is_pid_running "$PID_FILE"; then kill -9 "$(cat "$PID_FILE")" 2>/dev/null || true; fi
  rm -f "$PID_FILE" "$SUPERVISOR_PID_FILE"
  echo "$APP_NAME stopped."
}

run_loop() {
  ensure_dirs
  local JAVA_EXE
  JAVA_EXE="$(resolve_java)" || die "java was not found. Set JDK25_HOME or JAVA_HOME to JDK 25."
  local JAVA_OPTS_EFFECTIVE="${JAVA_OPTS:-$JAVA_OPTS_DEFAULT}"
  local JAVA_AGENT_OPTS="-javaagent:./libs/al-commons-1.0.jar"
  if [ "${MAMEAION75_DISABLE_JAVAAGENT:-0}" = "1" ]; then JAVA_AGENT_OPTS=""; fi

  echo "============================================================"
  echo "MameAion75 $APP_NAME - Linux JDK25 launcher"
  echo "WorkDir : $(pwd)"
  echo "Java    : $JAVA_EXE"
  echo "Opts    : $JAVA_OPTS_EFFECTIVE"
  echo "Agent   : ${JAVA_AGENT_OPTS:-DISABLED}"
  echo "Log     : $CONSOLE_LOG"
  echo "============================================================"

  local err=2
  while true; do
    rotate_console_log
    # shellcheck disable=SC2086
    "$JAVA_EXE" $JDK25_OPTS $JAVA_OPTS_EFFECTIVE -ea $JAVA_AGENT_OPTS -cp "./libs/*" "$MAIN_CLASS" > "$CONSOLE_LOG" 2>&1 &
    local child=$!
    echo "$child" > "$PID_FILE"
    wait "$child"
    err=$?
    rm -f "$PID_FILE"

    case "$err" in
      0) echo "$APP_NAME stopped normally."; break ;;
      2) echo "$APP_NAME requested restart. Restarting in 10 seconds..."; sleep 10 ;;
      130|137|143) echo "$APP_NAME stopped by signal. ExitCode=$err"; break ;;
      *) echo "$APP_NAME crashed. ExitCode=$err. Restarting in 10 seconds..."; sleep 10 ;;
    esac
  done
}

start_background() {
  ensure_dirs
  if is_pid_running "$SUPERVISOR_PID_FILE"; then
    echo "$APP_NAME is already running. supervisor pid=$(cat "$SUPERVISOR_PID_FILE")"
    exit 0
  fi
  nohup "$0" run >> "$LAUNCHER_LOG" 2>&1 &
  echo $! > "$SUPERVISOR_PID_FILE"
  echo "$APP_NAME started. supervisor pid=$(cat "$SUPERVISOR_PID_FILE")"
  echo "console log : $CONSOLE_LOG"
  echo "launcher log: $LAUNCHER_LOG"
}

case "${1:-start}" in
  start) start_background ;;
  run|console|noloop) run_loop ;;
  stop) stop_server ;;
  restart) stop_server; start_background ;;
  status) status ;;
  *) echo "Usage: $0 {start|run|console|stop|restart|status}"; exit 2 ;;
esac
