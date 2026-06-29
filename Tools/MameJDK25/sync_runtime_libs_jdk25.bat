@echo off
setlocal EnableExtensions
chcp 65001 >nul
cd /d "%~dp0\..\.."
set "ROOT=%CD%"
set "CACHE=%ROOT%\Tools\MameJDK25\runtime_libs_cache"

if not exist "%CACHE%\*.jar" (
  echo [ERROR] Runtime libs cache is empty: %CACHE%
  echo Run Tools\MameJDK25\download_runtime_libs_jdk25.bat first.
  exit /b 1
)

echo [SYNC] Runtime libs cache: %CACHE%

for %%D in (
  "AL-Login\libs"
  "AL-Game\libs"
  "AL-Chat\libs"
  "AL-Login\build\dist\AL-Login\libs"
  "AL-Game\build\dist\AL-Game\libs"
  "AL-Chat\build\dist\AL-Chat\libs"
  "dist_jdk25\AL-Login\libs"
  "dist_jdk25\AL-Game\libs"
  "dist_jdk25\AL-Chat\libs"
) do (
  if not exist "%ROOT%\%%~D" mkdir "%ROOT%\%%~D" >nul 2>&1
  copy /Y "%CACHE%\*.jar" "%ROOT%\%%~D\" >nul
  echo [OK] %%~D
)

echo [SYNC] Done.
exit /b 0
