@echo off
setlocal EnableExtensions EnableDelayedExpansion
chcp 65001 >nul
cd /d "%~dp0\..\.."
set "ROOT=%CD%"
set "CACHE=%ROOT%\Tools\MameJDK25\runtime_libs_cache"
if not exist "%CACHE%" mkdir "%CACHE%"

echo ============================================================
echo  MameAion 7.8.0 JDK25 Runtime Lib Downloader
echo ============================================================
echo Root : %ROOT%
echo Cache: %CACHE%
echo.

call :download jaxb-api-2.3.1.jar https://repo1.maven.org/maven2/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar
if errorlevel 1 goto :failed
call :download jaxb-runtime-2.3.8.jar https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-runtime/2.3.8/jaxb-runtime-2.3.8.jar
if errorlevel 1 goto :failed
call :download jaxb-core-2.3.0.1.jar https://repo1.maven.org/maven2/com/sun/xml/bind/jaxb-core/2.3.0.1/jaxb-core-2.3.0.1.jar
if errorlevel 1 goto :failed
call :download txw2-2.3.8.jar https://repo1.maven.org/maven2/org/glassfish/jaxb/txw2/2.3.8/txw2-2.3.8.jar
if errorlevel 1 goto :failed
call :download istack-commons-runtime-3.0.12.jar https://repo1.maven.org/maven2/com/sun/istack/istack-commons-runtime/3.0.12/istack-commons-runtime-3.0.12.jar
if errorlevel 1 goto :failed
call :download stax-ex-1.8.3.jar https://repo1.maven.org/maven2/org/jvnet/staxex/stax-ex/1.8.3/stax-ex-1.8.3.jar
if errorlevel 1 goto :failed
call :download FastInfoset-1.2.16.jar https://repo1.maven.org/maven2/com/sun/xml/fastinfoset/FastInfoset/1.2.16/FastInfoset-1.2.16.jar
if errorlevel 1 goto :failed
call :download javax.activation-api-1.2.0.jar https://repo1.maven.org/maven2/javax/activation/javax.activation-api/1.2.0/javax.activation-api-1.2.0.jar
if errorlevel 1 goto :failed
call :download mysql-connector-j-8.4.0.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar
if errorlevel 1 goto :failed
call :download HikariCP-5.1.0.jar https://repo1.maven.org/maven2/com/zaxxer/HikariCP/5.1.0/HikariCP-5.1.0.jar
if errorlevel 1 goto :failed

call "%ROOT%\Tools\MameJDK25\sync_runtime_libs_jdk25.bat"
if errorlevel 1 goto :failed

echo.
echo Done. Runtime libs were copied to source libs, module build dist, and dist_jdk25.
echo Run build_jdk25_all.bat once more after this if you want a fresh dist.
echo.
if /I not "%MAME_LIBS_NOPAUSE%"=="1" if /I not "%MAME_BUILD_NOPAUSE%"=="1" pause
exit /b 0

:failed
echo.
echo [ERROR] Runtime lib download/sync failed.
if /I not "%MAME_LIBS_NOPAUSE%"=="1" if /I not "%MAME_BUILD_NOPAUSE%"=="1" pause
exit /b 1

:download
set "NAME=%~1"
set "URL=%~2"
if exist "%CACHE%\%NAME%" (
  echo OK   %NAME%
  exit /b 0
)
echo GET  %NAME%
powershell -NoProfile -ExecutionPolicy Bypass -Command "try { Invoke-WebRequest -Uri '%URL%' -OutFile '%CACHE%\%NAME%' -UseBasicParsing; exit 0 } catch { Write-Host $_; exit 1 }"
if errorlevel 1 (
  echo [ERROR] Failed to download %NAME%
  exit /b 1
)
exit /b 0
