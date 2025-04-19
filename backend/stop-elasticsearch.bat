@echo off
echo Stopping Elasticsearch...

docker stop tracing-elasticsearch
if %errorlevel% equ 0 (
    echo Elasticsearch stopped successfully.
) else (
    echo Elasticsearch is not running.
)

pause 