#!/bin/bash

echo "=== Starting Local CI/CD Pipeline ==="

# Exit on any error
set -e

echo "=== 1. Backend Tests ==="
echo "Building and testing backend..."
cd backend
mvn clean compile
mvn test

echo "=== 2. Frontend Tests ==="
echo "Building and testing frontend..."
cd ../frontend
npm ci
npm run build
npm run test
npm run lint

echo "=== 3. Code Coverage ==="
echo "Running backend coverage..."
cd ../backend
mvn jacoco:report

echo "Running frontend coverage..."
cd ../frontend
npm run test:coverage

echo "=== All Tests Passed ==="
cd ..
echo "=== CI/CD Pipeline Completed Successfully ===" 