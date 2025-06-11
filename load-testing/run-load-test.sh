#!/bin/bash

# 确保K6已安装
echo "检查K6是否已安装..."
if ! command -v k6 &> /dev/null; then
    echo "K6未安装，请先安装K6："
    echo "Windows: choco install k6"
    echo "macOS: brew install k6"
    echo "Linux: sudo apt-get install k6"
    exit 1
fi

# 检查后端服务是否运行
echo "检查后端服务是否运行..."
if ! curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "错误: 后端服务未运行，请先启动后端服务"
    echo "运行: docker-compose up backend 或 cd backend && mvn spring-boot:run"
    exit 1
fi

echo "开始负载测试..."
echo "===================="

# 运行负载测试
k6 run --out json=load-test-results.json ./k6-load-test.js

echo "===================="
echo "负载测试完成！"
echo "结果已保存到: load-test-results.json"
echo ""
echo "查看详细结果："
echo "1. 打开 http://localhost:3001 查看Grafana仪表板"
echo "2. 打开 http://localhost:9090 查看Prometheus指标" 