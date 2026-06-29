@echo off
setlocal EnableExtensions
chcp 65001 >nul
cd /d "%~dp0"

rem MameAion 7.8.0 JDK25 build entry point.
rem This wrapper pauses at the end unless MAME_BUILD_NOPAUSE=1.

if not exist "%~dp0Tools\MameJDK25\build_jdk25_all.bat" (
  echo [ERROR] Tools\MameJDK25\build_jdk25_all.bat was not found.
  echo Run this file from the aion-server-7.8 root folder.
  echo.
  pause
  exit /b 1
)

call "%~dp0Tools\MameJDK25\build_jdk25_all.bat" %*
set "RC=%ERRORLEVEL%"

echo.
if "%RC%"=="0" (
  echo ============================================================
  echo  BUILD SUCCESS
  echo ============================================================
) else (
  echo ============================================================
  echo  BUILD FAILED  ExitCode=%RC%
  echo ============================================================
  echo  Logs: build_logs\jdk25\*.log
)
echo.
if /I not "%MAME_BUILD_NOPAUSE%"=="1" pause
exit /b %RC%
