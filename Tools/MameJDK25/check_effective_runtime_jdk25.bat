@echo off
setlocal EnableExtensions
cd /d "%~dp0\..\.."
set "ROOT=%CD%"
set "JDK25_HOME=C:\Program Files\Java\jdk-25.0.3"
if exist "%JDK25_HOME%\bin\java.exe" (
  echo JDK25_HOME=%JDK25_HOME%
  "%JDK25_HOME%\bin\java.exe" -version
) else (
  echo [WARN] Default JDK25 path missing: %JDK25_HOME%
)

echo.
echo Runtime dist java command check:
for %%F in ("AL-Login\build\dist\AL-Login\StartLS.bat" "AL-Game\build\dist\AL-Game\StartGS.bat" "AL-Chat\build\dist\AL-Chat\StartCS.bat") do (
  if exist "%ROOT%\%%~F" (
    echo ---- %%~F
    findstr /n /i "JAVA_EXE java.version jdk-25 java.exe" "%ROOT%\%%~F"
  ) else (
    echo Missing %%~F
  )
)

echo.
echo Runtime libs quick check:
for %%D in ("AL-Login\build\dist\AL-Login\libs" "AL-Game\build\dist\AL-Game\libs" "AL-Chat\build\dist\AL-Chat\libs") do (
  echo ---- %%~D
  if exist "%ROOT%\%%~D" (
    dir /b "%ROOT%\%%~D\*jaxb*.jar" 2>nul
    dir /b "%ROOT%\%%~D\*activation*.jar" 2>nul
    dir /b "%ROOT%\%%~D\mysql-connector-j*.jar" 2>nul
    dir /b "%ROOT%\%%~D\HikariCP*.jar" 2>nul
    dir /b "%ROOT%\%%~D\javassist*.jar" 2>nul
  )
)

echo.
pause
