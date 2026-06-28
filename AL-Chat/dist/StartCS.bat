@ECHO off
TITLE MameAion 7.8.0 - Chat Server Console

IF NOT "%JDK25_HOME%" == "" (
  SET "JAVA_CMD=%JDK25_HOME%\bin\java.exe"
)
IF "%JAVA_CMD%" == "" SET "JAVA_CMD=java"

:START
CLS
IF "%MODE%" == "" (
CALL PanelCS.bat
)
SET "JAVA_OPTS=-XX:+UseG1GC -Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8 %JAVA_OPTS%"
ECHO Starting MameAion 7.8.0 Chat Server in %MODE% mode.
ECHO Java: %JAVA_CMD%
"%JAVA_CMD%" %JAVA_OPTS% -ea -cp "./libs/*;AL-Chat.jar" com.aionemu.chatserver.ChatServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Chat Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Chat Server is terminated!
ECHO.
PAUSE
EXIT
