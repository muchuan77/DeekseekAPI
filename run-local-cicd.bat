@echo off
chcp 65001
echo === Starting Local CI/CD Pipeline ===

REM Set environment variables
set "JAVA_HOME=D:\Program Files\Java\jdk-18.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo === 1. Backend Tests ===
echo Building and testing backend...
cd backend
call mvn clean compile
if %errorlevel% neq 0 (
    echo Backend compilation failed
    goto :error
)

call mvn test
if %errorlevel% neq 0 (
    echo Backend tests failed
    goto :error
)

echo === 2. Frontend Tests ===
echo Building and testing frontend...
cd ..\frontend
call npm ci
if %errorlevel% neq 0 (
    echo Frontend dependency installation failed
    goto :error
)

call npm run build
if %errorlevel% neq 0 (
    echo Frontend build failed
    goto :error
)

call npm run test
if %errorlevel% neq 0 (
    echo Frontend tests failed
    goto :error
)

call npm run lint
if %errorlevel% neq 0 (
    echo Frontend lint check failed
    goto :error
)

echo === 3. Code Coverage ===
echo Running backend coverage...
cd ..\backend
call mvn jacoco:report

echo Running frontend coverage...
cd ..\frontend
call npm run test:coverage

echo === All Tests Passed ===
cd ..
goto :end

:error
echo === Tests Failed ===
cd ..
exit /b 1

:end
echo === CI/CD Pipeline Completed Successfully === 