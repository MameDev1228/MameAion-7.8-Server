@echo off
setlocal EnableExtensions
for %%D in (AL-Commons AL-Login AL-Game AL-Chat) do (
  if exist "%~dp0..\..\%%D\libs\javassist-3.15.0-GA.jar" (
    del /F /Q "%~dp0..\..\%%D\libs\javassist-3.15.0-GA.jar"
    echo [OK] Removed %%D\libs\javassist-3.15.0-GA.jar
  )
)
echo [DONE] Runtime source libs cleanup complete.
