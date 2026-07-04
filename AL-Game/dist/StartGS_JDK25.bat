@ECHO off
TITLE MameAion75 - Game Server JDK25
color 0A
SETLOCAL EnableExtensions

:START
CLS
SET "MODE=PRODUCTION"
CALL :RESOLVE_JAVA
IF ERRORLEVEL 1 GOTO ERROR

IF "%JAVA_OPTS%" == "" SET "JAVA_OPTS=-Xms4G -Xmx8G -server"
SET "JDK25_OPTS=-Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8 --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED"

ECHO ============================================================
ECHO MameAion75 Game Server - JDK25 launcher
ECHO WorkDir : %CD%
ECHO Java    : %JAVA_EXE%
ECHO Mode    : %MODE%
ECHO Opts    : %JAVA_OPTS%
IF /I "%MAMEAION75_DISABLE_JAVAAGENT%" == "1" (
  SET "JAVA_AGENT_OPTS="
  ECHO Agent   : DISABLED by MAMEAION75_DISABLE_JAVAAGENT=1
) ELSE (
  SET "JAVA_AGENT_OPTS=-javaagent:.\libs\al-commons-1.0.jar"
  ECHO Agent   : ENABLED ^(al-commons-1.0.jar^)
)
ECHO ============================================================
"%JAVA_EXE%" %JDK25_OPTS% %JAVA_OPTS% -ea %JAVA_AGENT_OPTS% -cp ".\libs\*" com.aionemu.gameserver.GameServer
SET "EXIT_CODE=%ERRORLEVEL%"

IF "%EXIT_CODE%" == "2" GOTO START
IF NOT "%EXIT_CODE%" == "0" GOTO ERROR
GOTO END

:RESOLVE_JAVA
IF NOT "%JDK25_HOME%" == "" IF EXIST "%JDK25_HOME%\bin\java.exe" SET "JAVA_EXE=%JDK25_HOME%\bin\java.exe" & EXIT /B 0
IF EXIST "C:\Program Files\Java\jdk-25.0.3\bin\java.exe" SET "JAVA_EXE=C:\Program Files\Java\jdk-25.0.3\bin\java.exe" & EXIT /B 0
IF EXIST "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" SET "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" & EXIT /B 0
IF NOT "%JAVA_HOME%" == "" IF EXIST "%JAVA_HOME%\bin\java.exe" SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe" & EXIT /B 0
FOR /F "delims=" %%J IN ('where java 2^>NUL') DO SET "JAVA_EXE=%%J" & EXIT /B 0
ECHO [ERROR] java.exe was not found. Set JDK25_HOME or JAVA_HOME to JDK 25.
EXIT /B 1

:ERROR
ECHO.
ECHO Game Server has terminated abnormaly! ExitCode=%EXIT_CODE%
ECHO.
PAUSE
EXIT /B 1

:END
ECHO.
ECHO Game Server is terminated. ExitCode=0
ECHO.
PAUSE
EXIT /B 0
