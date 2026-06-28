@echo off
setlocal

rem MameAion 7.8.0 JDK25 build wrapper.
rem Run this from the AionLightning-7.8.0 root folder.

call "%~dp0Tools\MameJDK25\build_jdk25_all.bat" %*
exit /b %ERRORLEVEL%
