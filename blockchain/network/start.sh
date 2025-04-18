#!/bin/bash

# 生成证书
cryptogen generate --config=./crypto-config.yaml

# 生成创世区块
configtxgen -profile RumorChannel -channelID system-channel -outputBlock ./channel-artifacts/genesis.block

# 生成通道配置
configtxgen -profile RumorChannel -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID rumor-channel

# 生成锚节点配置
configtxgen -profile RumorChannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors.tx -channelID rumor-channel -asOrg Org1MSP

# 启动网络
docker-compose up -d

# 等待网络启动
sleep 10

# 创建通道
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp" peer0.org1.example.com peer channel create -o orderer.example.com:7050 -c rumor-channel -f /etc/hyperledger/fabric/channel-artifacts/channel.tx --tls --cafile /etc/hyperledger/fabric/tls/ca.crt

# 加入通道
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp" peer0.org1.example.com peer channel join -b rumor-channel.block

# 更新锚节点
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/fabric/msp" peer0.org1.example.com peer channel update -o orderer.example.com:7050 -c rumor-channel -f /etc/hyperledger/fabric/channel-artifacts/Org1MSPanchors.tx --tls --cafile /etc/hyperledger/fabric/tls/ca.crt 