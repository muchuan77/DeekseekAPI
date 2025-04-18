#!/bin/bash

# 设置环境变量
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp
export CORE_PEER_ADDRESS=peer0.org1.example.com:7051
export CORE_PEER_TLS_ROOTCERT_FILE=/etc/hyperledger/fabric/tls/ca.crt
export ORDERER_CA=/etc/hyperledger/fabric/tls/ca.crt

# 创建谣言
echo "Creating rumor..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n rumor -c '{"Args":["createRumor","rumor1","This is a test rumor","user1"]}'
sleep 3

# 验证谣言
echo "Verifying rumor..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n rumor -c '{"Args":["verifyRumor","rumor1","user2","true","This rumor is verified"]}'
sleep 3

# 查询谣言
echo "Querying rumor..."
peer chaincode query -C rumor-channel -n rumor -c '{"Args":["getRumor","rumor1"]}'

# 查询谣言历史
echo "Querying rumor history..."
peer chaincode query -C rumor-channel -n rumor -c '{"Args":["getRumorHistory","rumor1"]}' 