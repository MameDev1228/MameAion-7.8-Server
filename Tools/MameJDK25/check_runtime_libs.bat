@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0\..\.."

set "ROOT=%CD%"
set "LOGDIR=%ROOT%\build_logs\jdk25"
if not exist "%LOGDIR%" mkdir "%LOGDIR%"
set "LOG=%LOGDIR%\runtime_libs_check.log"

>"%LOG%" echo === MameAion 7.8.0 JDK25 Runtime Lib Check ===
>>"%LOG%" echo Root: %ROOT%
>>"%LOG%" echo Date: %DATE% %TIME%

echo === Java version ===
if not "%JDK25_HOME%"=="" (
  set "JAVA_CMD=%JDK25_HOME%\bin\java.exe"
) else (
  set "JAVA_CMD=java"
)
"%JAVA_CMD%" -version > "%LOGDIR%\java_version.tmp" 2>&1
if errorlevel 1 (
  echo [ERROR] Java could not be executed. Set JDK25_HOME or put JDK25 java in PATH.
  echo [ERROR] Java could not be executed.>>"%LOG%"
  exit /b 1
)
type "%LOGDIR%\java_version.tmp"
type "%LOGDIR%\java_version.tmp" >> "%LOG%"

set "HAS_ERROR=0"
call :checkModule AL-Login
call :checkModule AL-Game
call :checkModule AL-Chat

call :scanFlags "AL-Login\dist\StartLS.bat"
call :scanFlags "AL-Game\dist\StartGS.bat"
call :scanFlags "AL-Chat\dist\StartCS.bat"

if "%HAS_ERROR%"=="1" (
  echo.
  echo Runtime dependency check FAILED. See %LOG%
  exit /b 1
)

echo.
echo Runtime dependency check OK or WARN-only. See %LOG%
exit /b 0

:checkModule
set "MOD=%~1"
set "LIB=%ROOT%\%MOD%\libs"
echo. & echo === %MOD% libs ===
>>"%LOG%" echo. 
>>"%LOG%" echo === %MOD% libs ===
if not exist "%LIB%" (
  echo [ERROR] %MOD% libs folder not found: %LIB%
  >>"%LOG%" echo [ERROR] %MOD% libs folder not found: %LIB%
  set "HAS_ERROR=1"
  exit /b 0
)
call :findJar "%LIB%" "jaxb-api*.jar" "JAXB API" required
call :findJar "%LIB%" "jaxb-runtime*.jar" "JAXB Runtime" required
call :findJar "%LIB%" "javax.activation*.jar activation*.jar" "Java Activation" required
call :findJar "%LIB%" "mysql-connector-j*.jar mysql-connector-java*.jar" "MySQL Connector/J" required
call :findJar "%LIB%" "HikariCP*.jar" "HikariCP" optional
exit /b 0

:findJar
set "DIR=%~1"
set "PATTERNS=%~2"
set "LABEL=%~3"
set "LEVEL=%~4"
set "FOUND="
for %%P in (%PATTERNS%) do (
  for %%F in ("%DIR%\%%~P") do (
    if exist "%%~fF" set "FOUND=%%~nxF"
  )
)
if defined FOUND (
  echo [OK] %LABEL%: !FOUND!
  >>"%LOG%" echo [OK] %LABEL%: !FOUND!
) else (
  if "%LEVEL%"=="required" (
    echo [ERROR] Missing %LABEL% in %DIR%
    >>"%LOG%" echo [ERROR] Missing %LABEL% in %DIR%
    set "HAS_ERROR=1"
  ) else (
    echo [WARN] Missing optional %LABEL% in %DIR% - database.pool=auto will fallback to BoneCP.
    >>"%LOG%" echo [WARN] Missing optional %LABEL% in %DIR% - database.pool=auto will fallback to BoneCP.
  )
)
exit /b 0

:scanFlags
set "FILE=%~1"
if not exist "%ROOT%\%FILE%" exit /b 0
findstr /I /C:"UseConcMarkSweepGC" /C:"UseParNewGC" /C:"UseSplitVerifier" /C:"Xbootclasspath" "%ROOT%\%FILE%" >nul
if not errorlevel 1 (
  echo [ERROR] Old JDK flags remain in %FILE%
  >>"%LOG%" echo [ERROR] Old JDK flags remain in %FILE%
  set "HAS_ERROR=1"
) else (
  echo [OK] No removed JDK flags in %FILE%
  >>"%LOG%" echo [OK] No removed JDK flags in %FILE%
)
exit /b 0
