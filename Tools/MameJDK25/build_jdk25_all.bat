@echo off
setlocal EnableExtensions

rem ============================================================
rem MameAion 7.8.0 / JDK25 full build batch
rem Default: build AL-Commons -> copy al-commons.jar -> AL-Login -> AL-Game -> AL-Chat
rem Usage:
rem   build_jdk25_all.bat           build all
rem   build_jdk25_all.bat commons   build commons only
rem   build_jdk25_all.bat login     build commons + login
rem   build_jdk25_all.bat game      build commons + game
rem   build_jdk25_all.bat chat      build commons + chat
rem
rem Optional:
rem   set JDK25_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot
rem ============================================================

set "MODE=%~1"
if "%MODE%"=="" set "MODE=all"

for %%I in ("%~dp0..\..") do set "ROOT=%%~fI"
set "LOG_DIR=%ROOT%\build_logs\jdk25"
set "OUT_DIR=%ROOT%\dist_jdk25"
set "ANT_HOME=%ROOT%\Tools\Ant"
set "ANT_BAT=%ANT_HOME%\bin\ant.bat"
set "FAILED_LOG="

if not exist "%ROOT%\AL-Commons\build.xml" (
  echo [ERROR] AionLightning-7.8.0 root was not detected.
  echo         Expected: %ROOT%\AL-Commons\build.xml
  exit /b 1
)

call :detect_jdk25
if errorlevel 1 goto :fail_no_jdk

if not exist "%ANT_BAT%" (
  echo [ERROR] Ant was not found: %ANT_BAT%
  echo         Expected Tools\Ant from the source tree.
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%"
set "ANT_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

call :banner
call :validate_mode "%MODE%"
if errorlevel 1 exit /b 1

if /I "%MODE%"=="commons" (
  call :build_module "AL-Commons" "%ROOT%\AL-Commons"
  if errorlevel 1 goto :build_failed
  call :collect_outputs
  goto :success
)

rem For all non-commons modes, always rebuild commons first so dependent modules get the latest jar.
call :build_module "AL-Commons" "%ROOT%\AL-Commons"
if errorlevel 1 goto :build_failed

call :copy_commons
if errorlevel 1 goto :build_failed

if /I "%MODE%"=="login" (
  call :build_module "AL-Login" "%ROOT%\AL-Login"
  if errorlevel 1 goto :build_failed
  call :collect_outputs
  goto :success
)

if /I "%MODE%"=="game" (
  call :build_module "AL-Game" "%ROOT%\AL-Game"
  if errorlevel 1 goto :build_failed
  call :collect_outputs
  goto :success
)

if /I "%MODE%"=="chat" (
  call :build_module "AL-Chat" "%ROOT%\AL-Chat"
  if errorlevel 1 goto :build_failed
  call :collect_outputs
  goto :success
)

rem Default: full build.
call :build_module "AL-Login" "%ROOT%\AL-Login"
if errorlevel 1 goto :build_failed

call :build_module "AL-Game" "%ROOT%\AL-Game"
if errorlevel 1 goto :build_failed

call :build_module "AL-Chat" "%ROOT%\AL-Chat"
if errorlevel 1 goto :build_failed

call :collect_outputs
goto :success

:banner
echo.
echo ============================================================
echo  MameAion 7.8.0 JDK25 Build
echo ============================================================
echo  Root      : %ROOT%
echo  Mode      : %MODE%
echo  JAVA_HOME : %JAVA_HOME%
echo  ANT_HOME  : %ANT_HOME%
echo  Logs      : %LOG_DIR%
echo  Output    : %OUT_DIR%
echo ------------------------------------------------------------
"%JAVA_HOME%\bin\java.exe" -version
"%JAVA_HOME%\bin\javac.exe" -version
call "%ANT_BAT%" -version
echo ============================================================
echo.
exit /b 0

:validate_mode
set "_mode=%~1"
if /I "%_mode%"=="all" exit /b 0
if /I "%_mode%"=="commons" exit /b 0
if /I "%_mode%"=="login" exit /b 0
if /I "%_mode%"=="game" exit /b 0
if /I "%_mode%"=="chat" exit /b 0
echo [ERROR] Unknown mode: %_mode%
echo         Use: all, commons, login, game, chat
exit /b 1

:detect_jdk25
set "FOUND_JDK="

if defined JDK25_HOME (
  if exist "%JDK25_HOME%\bin\javac.exe" (
    call :is_jdk25 "%JDK25_HOME%"
    if not errorlevel 1 (
      set "JAVA_HOME=%JDK25_HOME%"
      exit /b 0
    )
  )
)

if defined JAVA_HOME (
  if exist "%JAVA_HOME%\bin\javac.exe" (
    call :is_jdk25 "%JAVA_HOME%"
    if not errorlevel 1 exit /b 0
  )
)

for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-25*" "C:\Program Files\Java\jdk-25*" "C:\Program Files\Microsoft\jdk-25*" "C:\Program Files\BellSoft\LibericaJDK-25*" "C:\Program Files\Amazon Corretto\jdk25*") do (
  if exist "%%~fD\bin\javac.exe" (
    call :is_jdk25 "%%~fD"
    if not errorlevel 1 (
      set "FOUND_JDK=%%~fD"
      goto :found_jdk25
    )
  )
)

:found_jdk25
if defined FOUND_JDK (
  set "JAVA_HOME=%FOUND_JDK%"
  exit /b 0
)

exit /b 1

:is_jdk25
"%~1\bin\java.exe" -XshowSettings:properties -version 2>&1 | findstr /C:"java.specification.version = 25" >nul
exit /b %ERRORLEVEL%

:build_module
set "MODULE_NAME=%~1"
set "MODULE_DIR=%~2"
set "FAILED_LOG=%LOG_DIR%\%MODULE_NAME%.log"

echo.
echo [BUILD] %MODULE_NAME%
echo        %MODULE_DIR%
echo        log: %FAILED_LOG%

pushd "%MODULE_DIR%" >nul
call "%ANT_BAT%" clean dist > "%FAILED_LOG%" 2>&1
set "RC=%ERRORLEVEL%"
popd >nul

if not "%RC%"=="0" (
  echo [ERROR] %MODULE_NAME% build failed. ExitCode=%RC%
  exit /b %RC%
)

echo [OK] %MODULE_NAME%
exit /b 0

:copy_commons
set "COMMONS_JAR=%ROOT%\AL-Commons\build\al-commons.jar"
if not exist "%COMMONS_JAR%" (
  echo [ERROR] Missing commons jar: %COMMONS_JAR%
  exit /b 1
)

echo.
echo [COPY] al-commons.jar to module libs
for %%M in (AL-Login AL-Game AL-Chat) do (
  if not exist "%ROOT%\%%M\libs" mkdir "%ROOT%\%%M\libs"
  copy /Y "%COMMONS_JAR%" "%ROOT%\%%M\libs\al-commons.jar" >nul
  if errorlevel 1 (
    echo [ERROR] Failed to copy al-commons.jar to %%M\libs
    exit /b 1
  )
  echo [OK] %%M\libs\al-commons.jar
)
exit /b 0

:collect_outputs
echo.
echo [COLLECT] Build outputs
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

if exist "%ROOT%\AL-Commons\build\al-commons.jar" copy /Y "%ROOT%\AL-Commons\build\al-commons.jar" "%OUT_DIR%\al-commons.jar" >nul
if exist "%ROOT%\AL-Commons\build\AL-Commons.zip" copy /Y "%ROOT%\AL-Commons\build\AL-Commons.zip" "%OUT_DIR%\AL-Commons.zip" >nul

for %%M in (AL-Login AL-Game AL-Chat) do (
  if exist "%ROOT%\%%M\build\%%M.jar" copy /Y "%ROOT%\%%M\build\%%M.jar" "%OUT_DIR%\%%M.jar" >nul
  if exist "%ROOT%\%%M\build\%%M.zip" copy /Y "%ROOT%\%%M\build\%%M.zip" "%OUT_DIR%\%%M.zip" >nul
)

echo [OK] Output folder: %OUT_DIR%
exit /b 0

:success
echo.
echo ============================================================
echo  BUILD SUCCESS
echo ============================================================
echo  Logs   : %LOG_DIR%
echo  Output : %OUT_DIR%
echo.
echo  Next runtime note:
echo  JDK25 execution still needs JAXB 2.3.x runtime jars in module libs.
echo ============================================================
exit /b 0

:build_failed
echo.
echo ============================================================
echo  BUILD FAILED
echo ============================================================
echo  Failed log: %FAILED_LOG%
echo.
if exist "%FAILED_LOG%" (
  echo ---- last 80 lines ------------------------------------------------
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-Content -LiteralPath '%FAILED_LOG%' -Tail 80" 2>nul
  if errorlevel 1 type "%FAILED_LOG%"
  echo -------------------------------------------------------------------
)
echo.
echo Send this log if you want the next patch to continue from the error.
echo ============================================================
exit /b 1

:fail_no_jdk
echo [ERROR] JDK25 was not found.
echo.
echo Set one of these, then run again:
echo   set JDK25_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.x.x-hotspot
echo   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.x.x-hotspot
echo.
echo Or install JDK25 under one of the common locations:
echo   C:\Program Files\Eclipse Adoptium\jdk-25*
echo   C:\Program Files\Java\jdk-25*
echo   C:\Program Files\Microsoft\jdk-25*
echo   C:\Program Files\BellSoft\LibericaJDK-25*
echo   C:\Program Files\Amazon Corretto\jdk25*
exit /b 1
