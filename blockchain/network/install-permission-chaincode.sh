#!/bin/bash

# 设置环境变量
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp
export CORE_PEER_ADDRESS=peer0.org1.example.com:7051
export CORE_PEER_TLS_ROOTCERT_FILE=/etc/hyperledger/fabric/tls/ca.crt
export ORDERER_CA=/etc/hyperledger/fabric/tls/ca.crt

# 安装链码
peer chaincode install -n permission -v 1.0 -p github.com/rumor/chaincode/permission

# 实例化链码
peer chaincode instantiate -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -v 1.0 -c '{"Args":[]}'

# 等待链码实例化完成
sleep 10

echo "Permission chaincode installed and instantiated successfully" 