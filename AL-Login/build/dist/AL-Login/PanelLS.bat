@echo off
chcp 65001 >nul
color 0B
TITLE MameAion 7.8.0 - Login Server Panel
:MENU
CLS
ECHO.
ECHO   *--------------------------------------------------------------------------*
ECHO   ^|                  MameAion 7.8.0 - Login Server Panel                  ^|
ECHO   *--------------------------------------------------------------------------*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - 開発モード / Development                         3 - 終了        ^|
ECHO   ^|    2 - 本番モード / Production                                           ^|
ECHO   ^|                                                                          ^|
ECHO   *--------------------------------------------------------------------------*
ECHO.
SET /P OPTION=番号を入力して ENTER: 
IF "%OPTION%" == "1" (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms32m -Xmx32m -agentlib:jdwp=transport=dt_socket,address=8999,server=y,suspend=n -ea
CALL StartLS.bat
)
IF "%OPTION%" == "2" (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms64m -Xmx64m -server
CALL StartLS.bat
)
IF "%OPTION%" == "3" (
EXIT
)
GOTO :MENU
