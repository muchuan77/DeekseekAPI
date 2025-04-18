FROM hyperledger/fabric-peer:2.2.5

WORKDIR /app

# 复制配置文件
COPY config.yaml .
COPY crypto-config.yaml .

# 复制智能合约
COPY chaincode ./chaincode

# 暴露端口
EXPOSE 7051 7052

# 启动命令
CMD ["peer", "node", "start"] 