@echo off
rem 功能实现.md -> 功能实现.docx
rem 优先用装了 python-docx 的解释器，失败再退回系统 python
setlocal
set PY=C:\Users\15181\.dsh\dsh-runtimes\dsh-primary-runtime\dependencies\python\python.exe
if exist "%PY%" (
    "%PY%" "%~dp0build_doc.py" %*
) else (
    python "%~dp0build_doc.py" %*
)
endlocal