@echo off
echo Starting Elasticsearch...

REM 检查 Docker 是否运行
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not running. Please start Docker first.
    pause
    exit /b 1
)

REM 检查 Elasticsearch 容器是否已存在
docker ps -a | findstr "tracing-elasticsearch" >nul
if %errorlevel% equ 0 (
    echo Elasticsearch container already exists.
    echo Starting existing container...
    docker start tracing-elasticsearch
) else (
    echo Creating new Elasticsearch container...
    docker-compose up -d
)

echo Elasticsearch is starting...
echo Please wait for about 30 seconds for Elasticsearch to be ready.
echo You can check the status by visiting: http://localhost:9200

pause 