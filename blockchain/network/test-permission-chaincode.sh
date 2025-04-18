#!/bin/bash

# 设置环境变量
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp
export CORE_PEER_ADDRESS=peer0.org1.example.com:7051
export CORE_PEER_TLS_ROOTCERT_FILE=/etc/hyperledger/fabric/tls/ca.crt
export ORDERER_CA=/etc/hyperledger/fabric/tls/ca.crt

# 测试创建角色
echo "Creating custom role..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["createRole","MODERATOR","[\"VERIFY_RUMOR\",\"VIEW_RUMOR\",\"MANAGE_COMMENTS\"]"]}'
sleep 3

# 测试分配角色
echo "Assigning role to user..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["assignRole","user1","MODERATOR"]}'
sleep 3

# 测试检查权限
echo "Checking user permission..."
peer chaincode query -C rumor-channel -n permission -c '{"Args":["checkPermission","user1","VERIFY_RUMOR"]}'

# 测试获取用户角色
echo "Getting user roles..."
peer chaincode query -C rumor-channel -n permission -c '{"Args":["getUserRoles","user1"]}'

# 测试获取角色权限
echo "Getting role permissions..."
peer chaincode query -C rumor-channel -n permission -c '{"Args":["getRolePermissions","MODERATOR"]}'

# 测试更新角色权限
echo "Updating role permissions..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["updateRole","MODERATOR","[\"VERIFY_RUMOR\",\"VIEW_RUMOR\",\"MANAGE_COMMENTS\",\"DELETE_COMMENTS\"]"]}'
sleep 3

# 测试撤销角色
echo "Revoking role from user..."
peer chaincode invoke -o orderer.example.com:7050 --tls --cafile $ORDERER_CA -C rumor-channel -n permission -c '{"Args":["revokeRole","user1","MODERATOR"]}'
sleep 3

# 再次检查权限
echo "Checking user permission after revocation..."
peer chaincode query -C rumor-channel -n permission -c '{"Args":["checkPermission","user1","VERIFY_RUMOR"]}' 