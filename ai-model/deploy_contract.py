from web3 import Web3
import json
import os
from dotenv import load_dotenv
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 加载环境变量
load_dotenv()

class ContractDeployer:
    def __init__(self):
        """初始化合约部署器"""
        # 连接到以太坊网络
        self.w3 = Web3(Web3.HTTPProvider(os.getenv('ETH_NODE_URL')))
        
        # 检查连接
        if not self.w3.is_connected():
            raise Exception("无法连接到以太坊节点")
        
        # 加载账户
        self.account = self.w3.eth.account.from_key(os.getenv('PRIVATE_KEY'))
        
        # 加载合约ABI和字节码
        with open('contracts/RumorTracing.json', 'r') as f:
            contract_data = json.load(f)
            self.contract_abi = contract_data['abi']
            self.contract_bytecode = contract_data['bytecode']
    
    def deploy_contract(self):
        """部署合约"""
        try:
            # 创建合约
            contract = self.w3.eth.contract(
                abi=self.contract_abi,
                bytecode=self.contract_bytecode
            )
            
            # 构建交易
            transaction = contract.constructor().build_transaction({
                'from': self.account.address,
                'nonce': self.w3.eth.get_transaction_count(self.account.address),
                'gas': 2000000,
                'gasPrice': self.w3.eth.gas_price
            })
            
            # 签名交易
            signed_txn = self.w3.eth.account.sign_transaction(
                transaction,
                os.getenv('PRIVATE_KEY')
            )
            
            # 发送交易
            tx_hash = self.w3.eth.send_raw_transaction(signed_txn.rawTransaction)
            logger.info(f"合约部署交易已发送: {tx_hash.hex()}")
            
            # 等待交易确认
            tx_receipt = self.w3.eth.wait_for_transaction_receipt(tx_hash)
            logger.info(f"合约部署成功! 合约地址: {tx_receipt.contractAddress}")
            
            return tx_receipt.contractAddress
            
        except Exception as e:
            logger.error(f"合约部署失败: {str(e)}")
            raise
    
    def initialize_contract(self, contract_address):
        """初始化合约实例"""
        try:
            contract = self.w3.eth.contract(
                address=contract_address,
                abi=self.contract_abi
            )
            logger.info(f"合约实例初始化成功")
            return contract
        except Exception as e:
            logger.error(f"合约实例初始化失败: {str(e)}")
            raise
    
    def test_contract(self, contract):
        """测试合约功能"""
        try:
            # 测试创建谣言
            tx_hash = contract.functions.createRumor(
                "测试谣言内容",
                "测试来源",
                "测试元数据"
            ).transact({
                'from': self.account.address,
                'gas': 2000000
            })
            tx_receipt = self.w3.eth.wait_for_transaction_receipt(tx_hash)
            logger.info(f"创建谣言成功: {tx_receipt.transactionHash.hex()}")
            
            # 获取谣言ID
            rumor_id = contract.functions.getRumorCount().call()
            logger.info(f"创建的谣言ID: {rumor_id}")
            
            # 测试获取谣言信息
            rumor_info = contract.functions.getRumor(rumor_id).call()
            logger.info(f"谣言信息: {rumor_info}")
            
            # 测试验证谣言
            tx_hash = contract.functions.verifyRumor(
                rumor_id,
                False,
                "测试证据",
                "测试评论"
            ).transact({
                'from': self.account.address,
                'gas': 2000000
            })
            tx_receipt = self.w3.eth.wait_for_transaction_receipt(tx_hash)
            logger.info(f"验证谣言成功: {tx_receipt.transactionHash.hex()}")
            
            # 获取验证信息
            verification_id = contract.functions.getVerificationCount().call()
            verification_info = contract.functions.getVerification(verification_id).call()
            logger.info(f"验证信息: {verification_info}")
            
        except Exception as e:
            logger.error(f"合约测试失败: {str(e)}")
            raise

def main():
    try:
        # 创建部署器实例
        deployer = ContractDeployer()
        
        # 部署合约
        contract_address = deployer.deploy_contract()
        
        # 初始化合约实例
        contract = deployer.initialize_contract(contract_address)
        
        # 测试合约功能
        deployer.test_contract(contract)
        
    except Exception as e:
        logger.error(f"部署过程出错: {str(e)}")

if __name__ == "__main__":
    main() 