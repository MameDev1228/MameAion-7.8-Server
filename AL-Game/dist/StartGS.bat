@ECHO off
mode con:cols=150
color 1B
TITLE MameAion 7.8.0 - Game Server Console

IF NOT "%JDK25_HOME%" == "" (
  SET "JAVA_CMD=%JDK25_HOME%\bin\java.exe"
)
IF "%JAVA_CMD%" == "" SET "JAVA_CMD=java"

:START
CLS
SET NUMAENABLE=false
IF "%MODE%" == "" (
CALL PanelGS.bat
)

SET "JAVA_OPTS=-XX:+UseG1GC -Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8 %JAVA_OPTS%"
IF "%NUMAENABLE%" == "true" (
SET "JAVA_OPTS=-XX:+UseNUMA %JAVA_OPTS%"
)
ECHO Starting MameAion 7.8.0 Game Server in %MODE% mode.
ECHO Java: %JAVA_CMD%
"%JAVA_CMD%" %JAVA_OPTS% -ea -javaagent:./libs/al-commons.jar -cp "./libs/*;AL-Game.jar" com.aionemu.gameserver.GameServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Game Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Game Server is terminated!
ECHO.
PAUSE
EXIT
