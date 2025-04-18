import os
import json
import requests
from typing import Dict, Any, List, Optional
import logging
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

logger = logging.getLogger(__name__)

class DeepSeekAPI:
    """DeepSeek API集成类"""
    
    def __init__(self):
        self.api_key = os.getenv('DEEPSEEK_API_KEY')
        self.base_url = os.getenv('DEEPSEEK_API_BASE_URL', 'https://api.deepseek.com/v1')
        self.headers = {
            'Authorization': f'Bearer {self.api_key}',
            'Content-Type': 'application/json'
        }
    
    def analyze_text(self, text: str) -> Dict[str, Any]:
        """分析文本内容"""
        try:
            response = requests.post(
                f'{self.base_url}/analyze',
                headers=self.headers,
                json={
                    'text': text,
                    'analysis_type': 'rumor_detection'
                }
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"DeepSeek API调用失败: {str(e)}")
            raise
    
    def get_rumor_features(self, text: str) -> Dict[str, float]:
        """获取谣言特征"""
        try:
            response = requests.post(
                f'{self.base_url}/features',
                headers=self.headers,
                json={
                    'text': text,
                    'feature_types': [
                        'credibility',
                        'emotion',
                        'consistency',
                        'source_reliability'
                    ]
                }
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"获取谣言特征失败: {str(e)}")
            raise
    
    def verify_rumor(self, rumor_id: str, evidence: List[str]) -> Dict[str, Any]:
        """验证谣言"""
        try:
            response = requests.post(
                f'{self.base_url}/verify',
                headers=self.headers,
                json={
                    'rumor_id': rumor_id,
                    'evidence': evidence
                }
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"谣言验证失败: {str(e)}")
            raise
    
    def trace_rumor(self, rumor_id: str) -> Dict[str, Any]:
        """追踪谣言来源"""
        try:
            response = requests.get(
                f'{self.base_url}/trace/{rumor_id}',
                headers=self.headers
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"谣言追踪失败: {str(e)}")
            raise
    
    def get_rumor_history(self, rumor_id: str) -> List[Dict[str, Any]]:
        """获取谣言历史记录"""
        try:
            response = requests.get(
                f'{self.base_url}/history/{rumor_id}',
                headers=self.headers
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"获取谣言历史失败: {str(e)}")
            raise
    
    def update_rumor_status(self, rumor_id: str, status: str, 
                          comment: Optional[str] = None) -> Dict[str, Any]:
        """更新谣言状态"""
        try:
            response = requests.put(
                f'{self.base_url}/status/{rumor_id}',
                headers=self.headers,
                json={
                    'status': status,
                    'comment': comment
                }
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"更新谣言状态失败: {str(e)}")
            raise
    
    def get_rumor_statistics(self, time_range: str = '7d') -> Dict[str, Any]:
        """获取谣言统计信息"""
        try:
            response = requests.get(
                f'{self.base_url}/statistics',
                headers=self.headers,
                params={'time_range': time_range}
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            logger.error(f"获取谣言统计信息失败: {str(e)}")
            raise 