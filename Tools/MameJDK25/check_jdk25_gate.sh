#!/bin/bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
JAVA_BIN="${JAVA:-java}"
JAVAC_BIN="${JAVAC:-javac}"

printf '== Java ==\n'
"$JAVA_BIN" -version
printf '\n== Javac ==\n'
"$JAVAC_BIN" -version

required_patterns=(
  'jaxb-api*.jar'
  'jaxb-runtime*.jar'
  'txw2*.jar'
  'istack-commons-runtime*.jar'
  'stax-ex*.jar'
  'FastInfoset*.jar'
  'javax.activation*.jar'
)

modules=(AL-Commons AL-Login AL-Game AL-Chat)
missing=0
for module in "${modules[@]}"; do
  printf '\n== Checking %s/libs ==\n' "$module"
  for pattern in "${required_patterns[@]}"; do
    if ! compgen -G "$ROOT/$module/libs/$pattern" > /dev/null; then
      printf 'MISSING: %s/libs/%s\n' "$module" "$pattern"
      missing=1
    else
      printf 'OK: %s\n' "$pattern"
    fi
  done
done

if [ "$missing" -ne 0 ]; then
  cat <<'MSG'

JDK25 Gate cannot continue yet.
Put the javax-compatible JAXB runtime jars into every module libs folder first.
Recommended family: JAXB 2.3.x, not jakarta.* packages, because this source imports javax.xml.bind.*.
MSG
  exit 2
fi

printf '\n== Build order ==\n'
for module in AL-Commons AL-Login AL-Game AL-Chat; do
  printf '\n-- %s --\n' "$module"
  (cd "$ROOT/$module" && ant clean jar)
  if [ "$module" = "AL-Commons" ]; then
    cp -f "$ROOT/AL-Commons/build/al-commons.jar" "$ROOT/AL-Login/libs/al-commons.jar"
    cp -f "$ROOT/AL-Commons/build/al-commons.jar" "$ROOT/AL-Game/libs/al-commons.jar"
    cp -f "$ROOT/AL-Commons/build/al-commons.jar" "$ROOT/AL-Chat/libs/al-commons.jar"
  fi
done

printf '\nJDK25 Gate compile completed. Next: start LoginServer and GameServer with the same JAVA path.\n'
