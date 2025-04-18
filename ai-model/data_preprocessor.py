import pandas as pd
import numpy as np
from typing import Tuple, Dict, Any
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import StandardScaler
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
import nltk
import re

class DataPreprocessor:
    def __init__(self):
        # 下载必要的NLTK数据
        nltk.download('punkt')
        nltk.download('stopwords')
        self.stop_words = set(stopwords.words('english'))
        self.tfidf_vectorizer = TfidfVectorizer(max_features=1000)
        self.scaler = StandardScaler()
        
    def preprocess_text(self, text: str) -> str:
        """预处理文本数据"""
        if not isinstance(text, str):
            return ""
            
        # 转换为小写
        text = text.lower()
        
        # 移除特殊字符
        text = re.sub(r'[^\w\s]', '', text)
        
        # 分词
        tokens = word_tokenize(text)
        
        # 移除停用词
        tokens = [token for token in tokens if token not in self.stop_words]
        
        return ' '.join(tokens)
    
    def preprocess_rumor_data(self, rumors_df: pd.DataFrame) -> pd.DataFrame:
        """预处理谣言数据"""
        # 复制数据框
        df = rumors_df.copy()
        
        # 预处理文本内容
        df['processed_content'] = df['content'].apply(self.preprocess_text)
        
        # 转换时间特征
        df['create_time'] = pd.to_datetime(df['create_time'])
        df['verify_time'] = pd.to_datetime(df['verify_time'])
        
        # 计算时间差
        df['verification_delay'] = (df['verify_time'] - df['create_time']).dt.total_seconds()
        
        # 提取文本特征
        content_features = self.tfidf_vectorizer.fit_transform(df['processed_content'])
        content_features_df = pd.DataFrame(content_features.toarray(), 
                                         columns=[f'content_feature_{i}' for i in range(content_features.shape[1])])
        
        # 合并特征
        df = pd.concat([df, content_features_df], axis=1)
        
        return df
    
    def preprocess_verification_data(self, verifications_df: pd.DataFrame) -> pd.DataFrame:
        """预处理验证数据"""
        # 复制数据框
        df = verifications_df.copy()
        
        # 预处理评论文本
        df['processed_comment'] = df['comment'].apply(self.preprocess_text)
        
        # 提取文本特征
        comment_features = self.tfidf_vectorizer.fit_transform(df['processed_comment'])
        comment_features_df = pd.DataFrame(comment_features.toarray(), 
                                         columns=[f'comment_feature_{i}' for i in range(comment_features.shape[1])])
        
        # 合并特征
        df = pd.concat([df, comment_features_df], axis=1)
        
        return df
    
    def preprocess_user_data(self, users_df: pd.DataFrame) -> pd.DataFrame:
        """预处理用户数据"""
        # 复制数据框
        df = users_df.copy()
        
        # 标准化数值特征
        numeric_features = ['reputation_score', 'verification_count', 'success_rate']
        df[numeric_features] = self.scaler.fit_transform(df[numeric_features])
        
        # 对角色进行独热编码
        role_dummies = pd.get_dummies(df['role'], prefix='role')
        df = pd.concat([df, role_dummies], axis=1)
        
        return df
    
    def prepare_training_data(self, rumors_df: pd.DataFrame, 
                            verifications_df: pd.DataFrame,
                            users_df: pd.DataFrame) -> Tuple[np.ndarray, np.ndarray]:
        """准备训练数据"""
        # 预处理数据
        processed_rumors = self.preprocess_rumor_data(rumors_df)
        processed_verifications = self.preprocess_verification_data(verifications_df)
        processed_users = self.preprocess_user_data(users_df)
        
        # 合并特征
        features = []
        
        # 添加谣言特征
        rumor_features = processed_rumors.filter(regex='content_feature_|verification_delay')
        features.append(rumor_features)
        
        # 添加验证特征
        verification_features = processed_verifications.filter(regex='comment_feature_|sentiment|confidence')
        features.append(verification_features)
        
        # 添加用户特征
        user_features = processed_users.filter(regex='reputation_score|verification_count|success_rate|role_')
        features.append(user_features)
        
        # 合并所有特征
        X = pd.concat(features, axis=1).values
        
        # 获取标签
        y = processed_rumors['is_verified'].values
        
        return X, y
    
    def save_preprocessed_data(self, X: np.ndarray, y: np.ndarray, filename: str):
        """保存预处理后的数据"""
        np.savez(f'data/{filename}', X=X, y=y)
        
    def load_preprocessed_data(self, filename: str) -> Tuple[np.ndarray, np.ndarray]:
        """加载预处理后的数据"""
        data = np.load(f'data/{filename}')
        return data['X'], data['y'] 