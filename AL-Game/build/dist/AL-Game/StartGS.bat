@ECHO off
mode con:cols=150 
color 1B 
TITLE Aion German - Game Server Console
:START
CLS
SET JAVAVER=25
SET NUMAENABLE=false
CLS
IF "%MODE%" == "" (
CALL PanelGS.bat
)

SET JAVA_OPTS=-XX:+UseG1GC -Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8 %JAVA_OPTS%
IF "%NUMAENABLE%" == "true" (
SET JAVA_OPTS=-XX:+UseNUMA %JAVA_OPTS%
)
ECHO Starting Aion German Game Server in %MODE% mode.
JAVA %JAVA_OPTS% -ea -javaagent:./libs/al-commons.jar -cp ./libs/*;AL-Game.jar com.aionemu.gameserver.GameServer
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