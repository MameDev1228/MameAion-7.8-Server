@echo off
setlocal EnableExtensions
chcp 65001 >nul

rem ============================================================
rem MameAion 7.8.0 / JDK25 full build batch
rem Usage:
rem   build_jdk25_all.bat           build all
rem   build_jdk25_all.bat commons   build commons only
rem   build_jdk25_all.bat login     build commons + login
rem   build_jdk25_all.bat game      build commons + game
rem   build_jdk25_all.bat chat      build commons + chat
rem Optional:
rem   set JDK25_HOME=C:\Program Files\Java\jdk-25.0.3
rem   set MAME_BUILD_NOPAUSE=1
rem ============================================================

set "MODE=%~1"
if "%MODE%"=="" set "MODE=all"

for %%I in ("%~dp0..\..") do set "ROOT=%%~fI"
set "LOG_DIR=%ROOT%\build_logs\jdk25"
set "OUT_DIR=%ROOT%\dist_jdk25"
set "ANT_HOME=%ROOT%\Tools\Ant"
set "ANT_BAT=%ANT_HOME%\bin\ant.bat"
set "RUNTIME_CACHE=%ROOT%\Tools\MameJDK25\runtime_libs_cache"
set "FAILED_LOG="

if not exist "%ROOT%\AL-Commons\build.xml" (
  echo [ERROR] Root detection failed.
  echo         Expected: %ROOT%\AL-Commons\build.xml
  exit /b 1
)

call :validate_mode "%MODE%"
if errorlevel 1 exit /b 1

call :detect_jdk25
if errorlevel 1 goto :fail_no_jdk

if not exist "%ANT_BAT%" (
  echo [ERROR] Ant was not found: %ANT_BAT%
  echo         Make sure Tools\Ant exists in the source tree.
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%"
set "ANT_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

call :banner
call :clean_incompatible_libs

rem Runtime libs are required by JDK11+ and MySQL8/Hikari.
rem Download if missing, then sync them to module source libs.
call :ensure_runtime_libs
if errorlevel 1 goto :build_failed

if /I "%MODE%"=="commons" goto :do_commons_only

rem Every non-commons mode rebuilds Commons first, then copies al-commons.jar.
call :build_module "AL-Commons" "%ROOT%\AL-Commons"
if errorlevel 1 goto :build_failed

call :copy_commons
if errorlevel 1 goto :build_failed

if /I "%MODE%"=="login" goto :do_login_only
if /I "%MODE%"=="game" goto :do_game_only
if /I "%MODE%"=="chat" goto :do_chat_only

call :build_module "AL-Login" "%ROOT%\AL-Login"
if errorlevel 1 goto :build_failed
call :inject_runtime_to_module_dist "AL-Login"

call :build_module "AL-Game" "%ROOT%\AL-Game"
if errorlevel 1 goto :build_failed
call :inject_runtime_to_module_dist "AL-Game"

call :build_module "AL-Chat" "%ROOT%\AL-Chat"
if errorlevel 1 goto :build_failed
call :inject_runtime_to_module_dist "AL-Chat"

goto :collect_and_success

:do_commons_only
call :build_module "AL-Commons" "%ROOT%\AL-Commons"
if errorlevel 1 goto :build_failed
goto :collect_and_success

:do_login_only
call :build_module "AL-Login" "%ROOT%\AL-Login"
if errorlevel 1 goto :build_failed
call :inject_runtime_to_module_dist "AL-Login"
goto :collect_and_success

:do_game_only
call :build_module "AL-Game" "%ROOT%\AL-Game"
if errorlevel 1 goto :build_failed
call :inject_runtime_to_module_dist "AL-Game"
goto :collect_and_success

:do_chat_only
call :build_module "AL-Chat" "%ROOT%\AL-Chat"
if errorlevel 1 goto :build_failed
call :inject_runtime_to_module_dist "AL-Chat"
goto :collect_and_success

:collect_and_success
call :collect_outputs
if errorlevel 1 goto :build_failed

call :inject_runtime_to_out_dir
if errorlevel 1 goto :build_failed

echo.
echo ============================================================
echo  BUILD SUCCESS
echo ============================================================
echo  Output: %OUT_DIR%
echo  Logs  : %LOG_DIR%
echo ============================================================
exit /b 0

:build_failed
echo.
echo ============================================================
echo  BUILD FAILED
echo ============================================================
if defined FAILED_LOG (
  echo  Failed log: %FAILED_LOG%
  if exist "%FAILED_LOG%" (
    echo.
    echo ---------------- Last 120 lines ----------------
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-Content -LiteralPath '%FAILED_LOG%' -Tail 120" 2>nul
    echo -----------------------------------------------
  )
)
echo.
echo Send this log so the next patch can continue from the exact error.
echo ============================================================
exit /b 1

:fail_no_jdk
echo [ERROR] JDK25 was not found.
echo         Example:
echo           set JDK25_HOME=C:\Program Files\Java\jdk-25.0.3
echo           build_jdk25_all.bat
echo.
echo         Search paths include:
echo           C:\Program Files\Java\jdk-25*
echo           C:\Program Files\Java\latest
echo           C:\Program Files\Eclipse Adoptium\jdk-25*
echo           C:\Program Files\Microsoft\jdk-25*
exit /b 1

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

for /d %%D in ("C:\Program Files\Java\jdk-25*" "C:\Program Files\Java\latest" "C:\Program Files\Eclipse Adoptium\jdk-25*" "C:\Program Files\Microsoft\jdk-25*" "C:\Program Files\BellSoft\LibericaJDK-25*" "C:\Program Files\Amazon Corretto\jdk25*") do (
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

:clean_incompatible_libs
echo.
echo [CLEAN] Removing old JDK-incompatible Javassist jars
for %%M in (AL-Commons AL-Game AL-Login AL-Chat) do (
  if exist "%ROOT%\%%M\libs\javassist-3.15.0-GA.jar" (
    del /F /Q "%ROOT%\%%M\libs\javassist-3.15.0-GA.jar" >nul 2>&1
    echo [OK] Removed %%M\libs\javassist-3.15.0-GA.jar
  )
  if exist "%ROOT%\%%M\build\dist\%%M\libs\javassist-3.15.0-GA.jar" (
    del /F /Q "%ROOT%\%%M\build\dist\%%M\libs\javassist-3.15.0-GA.jar" >nul 2>&1
    echo [OK] Removed %%M build dist old Javassist
  )
  if exist "%OUT_DIR%\%%M\libs\javassist-3.15.0-GA.jar" (
    del /F /Q "%OUT_DIR%\%%M\libs\javassist-3.15.0-GA.jar" >nul 2>&1
    echo [OK] Removed dist_jdk25 %%M old Javassist
  )
)
exit /b 0

:ensure_runtime_libs
echo.
echo [RUNTIME] Checking JDK25 runtime libraries
if not exist "%RUNTIME_CACHE%\jaxb-api-2.3.1.jar" goto :download_runtime
if not exist "%RUNTIME_CACHE%\jaxb-runtime-2.3.8.jar" goto :download_runtime
if not exist "%RUNTIME_CACHE%\javax.activation-api-1.2.0.jar" goto :download_runtime
if not exist "%RUNTIME_CACHE%\mysql-connector-j-8.4.0.jar" goto :download_runtime
if not exist "%RUNTIME_CACHE%\HikariCP-5.1.0.jar" goto :download_runtime
goto :sync_runtime

:download_runtime
echo [RUNTIME] Missing cache jars. Running downloader...
set "MAME_LIBS_NOPAUSE=1"
call "%ROOT%\Tools\MameJDK25\download_runtime_libs_jdk25.bat"
if errorlevel 1 exit /b 1

:sync_runtime
call "%ROOT%\Tools\MameJDK25\sync_runtime_libs_jdk25.bat"
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

:inject_runtime_to_module_dist
set "MOD=%~1"
if not exist "%RUNTIME_CACHE%\*.jar" exit /b 0
if not exist "%ROOT%\%MOD%\build\dist\%MOD%\libs" mkdir "%ROOT%\%MOD%\build\dist\%MOD%\libs"
copy /Y "%RUNTIME_CACHE%\*.jar" "%ROOT%\%MOD%\build\dist\%MOD%\libs\" >nul
echo [RUNTIME] Injected runtime libs to %MOD% build dist
exit /b 0

:inject_runtime_to_out_dir
if not exist "%RUNTIME_CACHE%\*.jar" exit /b 0
for %%M in (AL-Login AL-Game AL-Chat) do (
  if exist "%OUT_DIR%\%%M" (
    if not exist "%OUT_DIR%\%%M\libs" mkdir "%OUT_DIR%\%%M\libs"
    copy /Y "%RUNTIME_CACHE%\*.jar" "%OUT_DIR%\%%M\libs\" >nul
    echo [RUNTIME] Injected runtime libs to dist_jdk25\%%M\libs
  )
)
exit /b 0

:collect_outputs
echo.
echo [COLLECT] Collecting build outputs to dist_jdk25
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

if exist "%ROOT%\AL-Commons\build\al-commons.jar" copy /Y "%ROOT%\AL-Commons\build\al-commons.jar" "%OUT_DIR%\al-commons.jar" >nul
if exist "%ROOT%\AL-Commons\build\AL-Commons.zip" copy /Y "%ROOT%\AL-Commons\build\AL-Commons.zip" "%OUT_DIR%\AL-Commons.zip" >nul

for %%M in (AL-Login AL-Game AL-Chat) do (
  if exist "%ROOT%\%%M\build\%%M.jar" copy /Y "%ROOT%\%%M\build\%%M.jar" "%OUT_DIR%\%%M.jar" >nul
  if exist "%ROOT%\%%M\build\%%M.zip" copy /Y "%ROOT%\%%M\build\%%M.zip" "%OUT_DIR%\%%M.zip" >nul
  if exist "%ROOT%\%%M\build\dist\%%M" (
    if exist "%OUT_DIR%\%%M" rmdir /S /Q "%OUT_DIR%\%%M"
    xcopy /E /I /Y "%ROOT%\%%M\build\dist\%%M" "%OUT_DIR%\%%M" >nul
    if errorlevel 1 exit /b 1
  )
)

echo [OK] Output folder: %OUT_DIR%
exit /b 0
