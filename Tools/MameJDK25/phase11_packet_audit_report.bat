@echo off
setlocal
set ROOT=%~dp0..\..
if not "%~1"=="" set ROOT=%~1
python "%~dp0phase11_packet_audit_report.py" "%ROOT%"
endlocal
