import os
import json
import logging
import numpy as np
import pandas as pd
from typing import Dict, Any, List, Optional
from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
import uvicorn
from model_trainer import ModelTrainer
from data_preprocessor import DataPreprocessor
from deepseek_api import DeepSeekAPI
from web3 import Web3
from sqlalchemy import create_engine
from visualization import RumorVisualizer
import threading

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('deployment.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# 创建FastAPI应用
app = FastAPI(title="谣言检测模型部署接口")

# 加载环境变量
from dotenv import load_dotenv
load_dotenv()

# 初始化组件
model_trainer = ModelTrainer()
data_preprocessor = DataPreprocessor()
deepseek_api = DeepSeekAPI()

# 初始化数据库连接
db_engine = create_engine(
    f"postgresql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@"
    f"{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}"
)

# 初始化Web3连接
w3 = Web3(Web3.HTTPProvider(os.getenv('ETHEREUM_RPC_URL')))

# 加载智能合约
with open('contracts/RumorTracing.json') as f:
    contract_abi = json.load(f)['abi']
contract = w3.eth.contract(
    address=os.getenv('CONTRACT_ADDRESS'),
    abi=contract_abi
)

class RumorRequest(BaseModel):
    """谣言检测请求模型"""
    content: str
    source: Optional[str] = None
    metadata: Optional[Dict[str, Any]] = None

class RumorResponse(BaseModel):
    """谣言检测响应模型"""
    rumor_id: str
    is_rumor: bool
    confidence: float
    features: Dict[str, float]
    analysis: Dict[str, Any]
    verification_suggestions: List[str]

class VerificationRequest(BaseModel):
    """谣言验证请求模型"""
    rumor_id: str
    evidence: List[str]
    comment: Optional[str] = None

class VerificationResponse(BaseModel):
    """谣言验证响应模型"""
    verification_id: str
    is_verified: bool
    confidence: float
    analysis: Dict[str, Any]

@app.on_event("startup")
async def startup_event():
    """启动时加载模型"""
    try:
        model_path = 'models/random_forest_model'
        model_trainer.load_model(model_path)
        logger.info("模型加载成功")
    except Exception as e:
        logger.error(f"模型加载失败: {str(e)}")
        raise

@app.post("/detect", response_model=RumorResponse)
async def detect_rumor(request: RumorRequest) -> RumorResponse:
    """检测谣言"""
    try:
        # 使用DeepSeek API分析文本
        analysis = deepseek_api.analyze_text(request.content)
        features = deepseek_api.get_rumor_features(request.content)
        
        # 准备特征向量
        feature_vector = np.array(list(features.values())).reshape(1, -1)
        
        # 使用模型预测
        is_rumor = model_trainer.predict(feature_vector)[0]
        confidence = model_trainer.predict_proba(feature_vector)[0][1]
        
        # 生成验证建议
        verification_suggestions = generate_verification_suggestions(
            request.content, analysis, features
        )
        
        # 将谣言记录到区块链
        rumor_id = record_rumor_to_blockchain(
            request.content, 
            is_rumor, 
            confidence,
            request.source,
            request.metadata
        )
        
        return RumorResponse(
            rumor_id=rumor_id,
            is_rumor=bool(is_rumor),
            confidence=float(confidence),
            features=features,
            analysis=analysis,
            verification_suggestions=verification_suggestions
        )
    except Exception as e:
        logger.error(f"谣言检测失败: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/verify", response_model=VerificationResponse)
async def verify_rumor(request: VerificationRequest) -> VerificationResponse:
    """验证谣言"""
    try:
        # 使用DeepSeek API验证谣言
        verification_result = deepseek_api.verify_rumor(
            request.rumor_id,
            request.evidence
        )
        
        # 分析验证内容
        analysis = deepseek_api.analyze_text(request.comment or "")
        
        # 更新区块链上的谣言状态
        update_rumor_status(
            request.rumor_id,
            verification_result['status'],
            request.comment
        )
        
        return VerificationResponse(
            verification_id=verification_result['verification_id'],
            is_verified=verification_result['is_verified'],
            confidence=verification_result['confidence'],
            analysis=analysis
        )
    except Exception as e:
        logger.error(f"谣言验证失败: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/trace/{rumor_id}")
async def trace_rumor(rumor_id: str) -> Dict[str, Any]:
    """追踪谣言来源"""
    try:
        # 从区块链获取谣言追踪信息
        trace_result = deepseek_api.trace_rumor(rumor_id)
        return trace_result
    except Exception as e:
        logger.error(f"谣言追踪失败: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/history/{rumor_id}")
async def get_rumor_history(rumor_id: str) -> List[Dict[str, Any]]:
    """获取谣言历史记录"""
    try:
        # 获取谣言历史记录
        history = deepseek_api.get_rumor_history(rumor_id)
        return history
    except Exception as e:
        logger.error(f"获取谣言历史失败: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

def generate_verification_suggestions(
    content: str,
    analysis: Dict[str, Any],
    features: Dict[str, float]
) -> List[str]:
    """生成验证建议"""
    suggestions = []
    
    # 基于可信度
    if features['credibility'] < 0.5:
        suggestions.append("建议验证信息来源的可信度")
    
    # 基于情感分析
    if analysis.get('sentiment', 0) < -0.5:
        suggestions.append("内容包含强烈负面情绪，建议核实事实")
    
    # 基于一致性
    if features['consistency'] < 0.6:
        suggestions.append("内容可能存在不一致，建议交叉验证")
    
    # 基于来源可靠性
    if features['source_reliability'] < 0.7:
        suggestions.append("建议验证消息来源的可靠性")
    
    return suggestions

def record_rumor_to_blockchain(
    content: str,
    is_rumor: bool,
    confidence: float,
    source: Optional[str],
    metadata: Optional[Dict[str, Any]]
) -> str:
    """将谣言记录到区块链"""
    try:
        # 构建交易
        transaction = contract.functions.addRumor(
            content,
            is_rumor,
            int(confidence * 100),
            source or "",
            json.dumps(metadata or {})
        ).build_transaction({
            'from': w3.eth.accounts[0],
            'gas': 2000000,
            'gasPrice': w3.eth.gas_price,
            'nonce': w3.eth.get_transaction_count(w3.eth.accounts[0])
        })
        
        # 签名交易
        signed_txn = w3.eth.account.sign_transaction(
            transaction,
            os.getenv('PRIVATE_KEY')
        )
        
        # 发送交易
        tx_hash = w3.eth.send_raw_transaction(signed_txn.rawTransaction)
        tx_receipt = w3.eth.wait_for_transaction_receipt(tx_hash)
        
        # 获取谣言ID
        rumor_id = contract.functions.getRumorIdByTxHash(tx_hash.hex()).call()
        
        return str(rumor_id)
    except Exception as e:
        logger.error(f"记录谣言到区块链失败: {str(e)}")
        raise

def update_rumor_status(
    rumor_id: str,
    status: str,
    comment: Optional[str]
) -> None:
    """更新谣言状态"""
    try:
        # 构建交易
        transaction = contract.functions.updateRumorStatus(
            int(rumor_id),
            status,
            comment or ""
        ).build_transaction({
            'from': w3.eth.accounts[0],
            'gas': 2000000,
            'gasPrice': w3.eth.gas_price,
            'nonce': w3.eth.get_transaction_count(w3.eth.accounts[0])
        })
        
        # 签名交易
        signed_txn = w3.eth.account.sign_transaction(
            transaction,
            os.getenv('PRIVATE_KEY')
        )
        
        # 发送交易
        tx_hash = w3.eth.send_raw_transaction(signed_txn.rawTransaction)
        w3.eth.wait_for_transaction_receipt(tx_hash)
    except Exception as e:
        logger.error(f"更新谣言状态失败: {str(e)}")
        raise

def start_visualization_server():
    """启动可视化服务器"""
    visualizer = RumorVisualizer()
    visualizer.run_server(host='0.0.0.0', port=8050)

if __name__ == "__main__":
    # 启动可视化服务器
    visualization_thread = threading.Thread(target=start_visualization_server)
    visualization_thread.daemon = True
    visualization_thread.start()
    
    # 启动API服务器
    uvicorn.run(app, host="0.0.0.0", port=8000) 