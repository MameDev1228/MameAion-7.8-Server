@echo off
chcp 65001 >nul
mode con:cols=150 lines=45
color 0B
TITLE MameAion 7.8.0 - Game Server Panel
:MENU
CLS
ECHO.
ECHO   *--------------------------------------------------------------------------*
ECHO   ^|                  MameAion 7.8.0 - Game Server Panel                   ^|
ECHO   *--------------------------------------------------------------------------*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - 開発モード / Development                         4 - 終了        ^|
ECHO   ^|    2 - 本番モード / Production X1                                      ^|
ECHO   ^|    3 - 本番モード / Production X2                                      ^|
ECHO   ^|                                                                          ^|
ECHO   *--------------------------------------------------------------------------*
ECHO.
SET /P OPTION=番号を入力して ENTER: 
IF "%OPTION%" == "1" (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms3072m -Xmx3072m -XX:MaxHeapSize=3072m -agentlib:jdwp=transport=dt_socket,address=8998,server=y,suspend=n -ea
CALL StartGS.bat
)
IF "%OPTION%" == "2" (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms1536m -Xmx1536m -server
CALL StartGS.bat
)
IF "%OPTION%" == "3" (
SET MODE=PRODUCTION X2
SET JAVA_OPTS=-Xms3872m -Xmx3872m -server
CALL StartGS.bat
)
IF "%OPTION%" == "4" (
EXIT
)
GOTO :MENU
