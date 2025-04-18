import os
import json
import pandas as pd
import numpy as np
from typing import Dict, Any, List
import logging
from web3 import Web3
from sqlalchemy import create_engine
from deepseek_api import DeepSeekAPI

logger = logging.getLogger(__name__)

class DataCollector:
    """数据收集器"""
    
    def __init__(self):
        # 初始化DeepSeek API
        self.deepseek_api = DeepSeekAPI()
        
        # 初始化数据库连接
        self.db_engine = create_engine(
            f"postgresql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@"
            f"{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}"
        )
        
        # 初始化Web3连接
        self.w3 = Web3(Web3.HTTPProvider(os.getenv('ETHEREUM_RPC_URL')))
        
        # 加载智能合约
        with open('contracts/RumorTracing.json') as f:
            contract_abi = json.load(f)['abi']
        self.contract = self.w3.eth.contract(
            address=os.getenv('CONTRACT_ADDRESS'),
            abi=contract_abi
        )
    
    def collect_rumor_data(self) -> pd.DataFrame:
        """收集谣言数据"""
        try:
            # 从区块链获取谣言数据
            rumor_count = self.contract.functions.getRumorCount().call()
            rumors = []
            
            for i in range(rumor_count):
                rumor = self.contract.functions.getRumor(i).call()
                rumor_data = {
                    'id': i,
                    'content': rumor[0],
                    'creator': rumor[1],
                    'create_time': rumor[2],
                    'is_verified': rumor[3],
                    'verification_time': rumor[4],
                    'verification_comment': rumor[5]
                }
                
                # 使用DeepSeek API分析谣言
                analysis = self.deepseek_api.analyze_text(rumor_data['content'])
                features = self.deepseek_api.get_rumor_features(rumor_data['content'])
                
                # 合并分析结果
                rumor_data.update(analysis)
                rumor_data.update(features)
                
                rumors.append(rumor_data)
            
            return pd.DataFrame(rumors)
        except Exception as e:
            logger.error(f"收集谣言数据失败: {str(e)}")
            raise
    
    def collect_verification_data(self) -> pd.DataFrame:
        """收集验证数据"""
        try:
            # 从数据库获取验证数据
            query = """
                SELECT v.*, r.content as rumor_content
                FROM verifications v
                JOIN rumors r ON v.rumor_id = r.id
            """
            verifications = pd.read_sql(query, self.db_engine)
            
            # 使用DeepSeek API分析验证内容
            for idx, row in verifications.iterrows():
                analysis = self.deepseek_api.analyze_text(row['comment'])
                features = self.deepseek_api.get_rumor_features(row['comment'])
                
                # 合并分析结果
                verifications.loc[idx, 'sentiment'] = analysis.get('sentiment', 0)
                verifications.loc[idx, 'confidence'] = analysis.get('confidence', 0)
                verifications.loc[idx, 'credibility'] = features.get('credibility', 0)
                verifications.loc[idx, 'emotion'] = features.get('emotion', 0)
                verifications.loc[idx, 'consistency'] = features.get('consistency', 0)
                verifications.loc[idx, 'source_reliability'] = features.get('source_reliability', 0)
            
            return verifications
        except Exception as e:
            logger.error(f"收集验证数据失败: {str(e)}")
            raise
    
    def collect_user_data(self) -> pd.DataFrame:
        """收集用户数据"""
        try:
            # 从数据库获取用户数据
            query = """
                SELECT u.*, 
                       COUNT(DISTINCT v.id) as verification_count,
                       AVG(CASE WHEN v.is_correct THEN 1 ELSE 0 END) as success_rate
                FROM users u
                LEFT JOIN verifications v ON u.id = v.user_id
                GROUP BY u.id
            """
            users = pd.read_sql(query, self.db_engine)
            
            # 计算用户信誉分数
            for idx, row in users.iterrows():
                # 获取用户的验证历史
                history = self.deepseek_api.get_rumor_history(str(row['id']))
                
                # 计算基于历史的信誉分数
                credibility_scores = [h.get('credibility', 0) for h in history]
                users.loc[idx, 'reputation_score'] = np.mean(credibility_scores) if credibility_scores else 0
            
            return users
        except Exception as e:
            logger.error(f"收集用户数据失败: {str(e)}")
            raise
    
    def save_to_csv(self, data: pd.DataFrame, filename: str):
        """保存数据到CSV文件"""
        try:
            if not os.path.exists('data'):
                os.makedirs('data')
            data.to_csv(f'data/{filename}', index=False)
            logger.info(f"数据已保存到: data/{filename}")
        except Exception as e:
            logger.error(f"保存数据失败: {str(e)}")
            raise
    
    def collect_all_data(self):
        """收集所有数据"""
        try:
            logger.info("开始收集数据...")
            
            # 收集谣言数据
            rumors_df = self.collect_rumor_data()
            self.save_to_csv(rumors_df, 'rumors.csv')
            
            # 收集验证数据
            verifications_df = self.collect_verification_data()
            self.save_to_csv(verifications_df, 'verifications.csv')
            
            # 收集用户数据
            users_df = self.collect_user_data()
            self.save_to_csv(users_df, 'users.csv')
            
            logger.info("数据收集完成")
        except Exception as e:
            logger.error(f"数据收集失败: {str(e)}")
            raise 