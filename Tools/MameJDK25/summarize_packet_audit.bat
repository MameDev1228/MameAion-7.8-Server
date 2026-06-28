@echo off
setlocal
set ROOT=%~dp0..\..
if not "%~1"=="" set ROOT=%~1
python "%~dp0summarize_packet_audit.py" "%ROOT%"
endlocal
