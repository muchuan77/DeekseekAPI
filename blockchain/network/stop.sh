#!/bin/bash

# 停止并删除容器
docker-compose down

# 删除证书和通道配置
rm -rf crypto-config
rm -rf channel-artifacts 