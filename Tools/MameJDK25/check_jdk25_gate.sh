#!/bin/bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
JAVA_BIN="${JAVA:-java}"
JAVAC_BIN="${JAVAC:-javac}"
ANT_BIN="${ANT:-ant}"

printf '== Java ==\n'
"$JAVA_BIN" -version
printf '\n== Javac ==\n'
"$JAVAC_BIN" -version
printf '\n== Ant ==\n'
"$ANT_BIN" -version

printf '\n== Static JDK25 source gate ==\n'
if grep -R 'source="1\.7"\|target="1\.7"\|-Xbootclasspath\|UseConcMarkSweepGC\|UseParNewGC\|UseSplitVerifier' "$ROOT" \
  --include='build.xml' --include='*.sh' --include='*.bat' --exclude-dir='build' --exclude-dir='.git'; then
  printf '\nFAILED: old Java 7 / removed JVM options still exist in build or start scripts.\n'
  exit 3
fi
printf 'OK: no Java 7 source/target or removed bootclasspath/GC flags found in patched build/start scripts.\n'

required_runtime_patterns=(
  'jaxb-api*.jar'
  'jaxb-runtime*.jar'
  'txw2*.jar'
  'istack-commons-runtime*.jar'
  'stax-ex*.jar'
  'FastInfoset*.jar'
  'javax.activation*.jar'
)

modules=(AL-Commons AL-Login AL-Game AL-Chat)
missing_runtime=0
for module in "${modules[@]}"; do
  printf '\n== Runtime dependency check: %s/libs ==\n' "$module"
  for pattern in "${required_runtime_patterns[@]}"; do
    if ! compgen -G "$ROOT/$module/libs/$pattern" > /dev/null; then
      printf 'MISSING-RUNTIME: %s/libs/%s\n' "$module" "$pattern"
      missing_runtime=1
    else
      printf 'OK: %s\n' "$pattern"
    fi
  done
done

cat <<'MSG'

NOTE:
- Build now uses javac --release 8, so javax.xml.bind.* can compile on JDK 25 without bundling JAXB at compile time.
- Server runtime on JDK 11+ / JDK 25 still needs the javax-compatible JAXB runtime jars above.
- Do not use jakarta.* JAXB jars for this phase; the source still imports javax.xml.bind.*.
MSG

printf '\n== Build order ==\n'
for module in AL-Commons AL-Login AL-Game AL-Chat; do
  printf '\n-- %s --\n' "$module"
  (cd "$ROOT/$module" && "$ANT_BIN" clean jar)
  if [ "$module" = "AL-Commons" ]; then
    cp -f "$ROOT/AL-Commons/build/al-commons.jar" "$ROOT/AL-Login/libs/al-commons.jar"
    cp -f "$ROOT/AL-Commons/build/al-commons.jar" "$ROOT/AL-Game/libs/al-commons.jar"
    cp -f "$ROOT/AL-Commons/build/al-commons.jar" "$ROOT/AL-Chat/libs/al-commons.jar"
  fi
done

if [ "$missing_runtime" -ne 0 ]; then
  cat <<'MSG'

JDK25 Gate compile completed, but runtime JAXB jars are still missing.
Next compile/debug step is OK. Before launching LS/GS/Chat on JDK25, add the listed JAXB 2.3.x runtime jars to every module libs folder.
MSG
else
  printf '\nJDK25 Gate compile completed and runtime JAXB jar names were found. Next: start LoginServer and GameServer with the same JAVA path.\n'
fi
