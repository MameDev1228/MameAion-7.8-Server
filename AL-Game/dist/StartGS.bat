@echo off
setlocal EnableExtensions
chcp 65001 >nul
color 0B
mode con:cols=150 lines=45

rem MameAion 7.8.0 JDK25 launcher.
set "JAVA_EXE="

if defined JDK25_HOME (
  if exist "%JDK25_HOME%\bin\java.exe" set "JAVA_EXE=%JDK25_HOME%\bin\java.exe"
)

if not defined JAVA_EXE (
  if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
      "%JAVA_HOME%\bin\java.exe" -version 2>&1 | findstr /C:"version \"25" >nul
      if not errorlevel 1 set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    )
  )
)

if not defined JAVA_EXE (
  if exist "C:\Program Files\Java\jdk-25.0.3\bin\java.exe" set "JAVA_EXE=C:\Program Files\Java\jdk-25.0.3\bin\java.exe"
)

if not defined JAVA_EXE (
  if exist "C:\Program Files\Java\latest\bin\java.exe" (
    "C:\Program Files\Java\latest\bin\java.exe" -version 2>&1 | findstr /C:"version \"25" >nul
    if not errorlevel 1 set "JAVA_EXE=C:\Program Files\Java\latest\bin\java.exe"
  )
)

if not defined JAVA_EXE (
  echo [ERROR] JDK25 java.exe が見つかりません。
  echo 例: set JDK25_HOME=C:\Program Files\Java\jdk-25.0.3
  echo.
  pause
  exit /b 1
)

set "JDK25_COMPAT_OPTS=--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED"
set "ENCODING_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -DconsoleEncoding=UTF-8 -Duser.language=ja -Duser.country=JP"

TITLE MameAion 7.8.0 - Game Server / ゲームサーバー
:START
CLS
SET NUMAENABLE=false
echo ================================================================================
echo   MameAion 7.8.0  Game Server / ゲームサーバー
echo   Theme: Black x Aqua  ^|  JDK25 Runtime
echo ================================================================================
echo Java: %JAVA_EXE%
"%JAVA_EXE%" -version

IF "%MODE%" == "" (
CALL PanelGS.bat
)
IF "%NUMAENABLE%" == "true" (
SET JAVA_OPTS=-XX:+UseNUMA %JAVA_OPTS%
)
ECHO.
ECHO [起動] MameAion 7.8.0 Game Server / %MODE%
"%JAVA_EXE%" %JDK25_COMPAT_OPTS% %ENCODING_OPTS% %JAVA_OPTS% -ea -javaagent:./libs/al-commons.jar -cp "./libs/*;AL-Game.jar" com.aionemu.gameserver.GameServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO [停止] Game Server が異常終了しました。
ECHO.
PAUSE
EXIT /b 1
:END
ECHO.
ECHO [停止] Game Server は正常終了しました。
ECHO.
PAUSE
EXIT /b 0
