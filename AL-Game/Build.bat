@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

rem Standalone module build logging. BuildAll_JDK25.bat already captures a single root log,
rem so do not create nested logs when it is driving the build.
rem IMPORTANT: delayed expansion is required in this parenthesized IF block.
if /I not "%MAMEAION75_BUILD_LOGGED%"=="1" if /I not "%MAMEAION75_MODULE_BUILD_LOGGED%"=="1" (
  set "ROOT_DIR=!CD!\.."
  for %%I in ("!CD!") do set "MODULE_NAME=%%~nxI"
  set "MODULE_LOG_NAME=!MODULE_NAME: =_!"
  set "LOG_DIR=!CD!\..\logs"
  if not exist "!LOG_DIR!" mkdir "!LOG_DIR!"
  set "LOG_TS="
  for /f %%T in ('powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-Date -Format yyyyMMdd-HHmmss"') do set "LOG_TS=%%T"
  if not defined LOG_TS set "LOG_TS=unknown"
  set "BUILD_LOG=!LOG_DIR!\build-!MODULE_LOG_NAME!-jdk25-!LOG_TS!.log"
  echo ============================================================
  echo MameAion75 !MODULE_NAME! JDK25 Build - logging enabled
  echo Log: !BUILD_LOG!
  echo ============================================================
  set "MAMEAION75_MODULE_BUILD_LOGGED=1"
  set "MAMEAION75_BUILD_LOG=!BUILD_LOG!"
  powershell -NoProfile -ExecutionPolicy Bypass -File "!ROOT_DIR!\tools\run-bat-with-log.ps1" -BatchPath "%~f0" -LogPath "!BUILD_LOG!" %*
  set "PS_RC=!ERRORLEVEL!"
  echo.
  echo ============================================================
  echo MameAion75 !MODULE_NAME! JDK25 Build finished. ExitCode=!PS_RC!
  echo Log: !BUILD_LOG!
  echo ============================================================
  if /I not "!MAMEAION75_NO_PAUSE!"=="1" (
    echo.
    pause
  )
  exit /b !PS_RC!
)

call "..\tools\set-jdk25-env.bat" || exit /b 1
set "PATH=%CD%\..\tools\Ant\bin;%PATH%"
if defined MAMEAION75_BUILD_LOG echo LOG=%MAMEAION75_BUILD_LOG%
call "..\tools\download-jdk25-deps.bat" || exit /b 1
del /q "%CD%\lib\mysql-connector-java-5*.jar" 2>nul
del /q "%CD%\lib\javassist-3.15*.jar" 2>nul

set "ANT_TARGET=jar"
if not "%~1"=="" set "ANT_TARGET=%~1"
call "..\tools\Ant\bin\ant.bat" %ANT_TARGET%
exit /b %ERRORLEVEL%
