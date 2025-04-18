#!/bin/bash

# 设置环境变量
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp
export CORE_PEER_ADDRESS=peer0.org1.example.com:7051
export CORE_PEER_TLS_ROOTCERT_FILE=/etc/hyperledger/fabric/tls/ca.crt
export ORDERER_CA=/etc/hyperledger/fabric/tls/ca.crt

# 为测试用户分配角色
echo "Assigning roles to test users..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["assignRole","user1","USER"]}'
sleep 3
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["assignRole","user2","VERIFIER"]}'
sleep 3

# 测试创建谣言（使用普通用户）
echo "Creating rumor as normal user..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n rumor -c '{"Args":["createRumor","rumor1","This is a test rumor","user1"]}'
sleep 3

# 测试验证谣言（使用验证者）
echo "Verifying rumor as verifier..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n rumor -c '{"Args":["verifyRumor","rumor1","user2","true","This rumor is verified"]}'
sleep 3

# 测试查询谣言（使用普通用户）
echo "Querying rumor as normal user..."
peer chaincode query -C rumor-channel -n rumor -c '{"Args":["getRumor","rumor1","user1"]}'

# 测试查询谣言历史（使用普通用户）
echo "Querying rumor history as normal user..."
peer chaincode query -C rumor-channel -n rumor -c '{"Args":["getRumorHistory","rumor1","user1"]}'

# 测试无权限操作
echo "Testing unauthorized operations..."

# 尝试使用普通用户验证谣言（应该失败）
echo "Attempting to verify rumor as normal user (should fail)..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n rumor -c '{"Args":["verifyRumor","rumor1","user1","true","Unauthorized verification attempt"]}'
sleep 3

# 撤销用户角色后尝试查询
echo "Revoking user role and attempting to query..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["revokeRole","user1","USER"]}'
sleep 3
peer chaincode query -C rumor-channel -n rumor -c '{"Args":["getRumor","rumor1","user1"]}' 