import numpy as np
import pandas as pd
from typing import Dict, Any, Tuple, List
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.svm import SVC
from sklearn.neural_network import MLPClassifier
import xgboost as xgb
import lightgbm as lgb
import tensorflow as tf
from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import Dense, Dropout, BatchNormalization
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint, ReduceLROnPlateau
import joblib
import os
import json
import mlflow
import mlflow.tensorflow
import logging
from datetime import datetime
import shutil
from pathlib import Path
import optuna
from optuna.integration import TFKerasPruningCallback

class ModelTrainer:
    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.logger = logging.getLogger(__name__)
        self.model_version = None
        self.experiment_name = "rumor_detection"
        
        # 初始化MLflow
        mlflow.set_tracking_uri(config['mlflow']['tracking_uri'])
        mlflow.set_experiment(self.experiment_name)
        
        # 创建模型版本目录
        self.models_dir = Path(config['model']['models_dir'])
        self.models_dir.mkdir(parents=True, exist_ok=True)
        
        self.model_type = config['model']['type']
        self.model = None
        self.best_params = None
        self.scaler = None
        self.feature_importance = None
    
    def create_model(self, trial: optuna.Trial = None) -> tf.keras.Model:
        """创建模型架构"""
        model = Sequential()
        
        # 基础层
        model.add(Dense(256, activation='relu', input_shape=(self.config['model']['input_dim'],)))
        model.add(BatchNormalization())
        model.add(Dropout(0.3))
        
        # 可选的中间层
        if trial:
            n_layers = trial.suggest_int('n_layers', 1, 3)
            for i in range(n_layers):
                units = trial.suggest_int(f'n_units_l{i}', 128, 512)
                model.add(Dense(units, activation='relu'))
                model.add(BatchNormalization())
                model.add(Dropout(trial.suggest_float(f'dropout_l{i}', 0.2, 0.5)))
        else:
            model.add(Dense(128, activation='relu'))
            model.add(BatchNormalization())
            model.add(Dropout(0.3))
        
        # 输出层
        model.add(Dense(1, activation='sigmoid'))
        
        # 编译模型
        optimizer = tf.keras.optimizers.Adam(learning_rate=self.config['model']['learning_rate'])
        model.compile(
            optimizer=optimizer,
            loss='binary_crossentropy',
            metrics=['accuracy', tf.keras.metrics.Precision(), tf.keras.metrics.Recall()]
        )
        
        return model
    
    def train_model(self, X_train: np.ndarray, y_train: np.ndarray, 
                   X_val: np.ndarray, y_val: np.ndarray) -> Tuple[tf.keras.Model, Dict[str, Any]]:
        """训练模型"""
        with mlflow.start_run():
            # 记录参数
            mlflow.log_params(self.config['model'])
            
            # 创建模型
            model = self.create_model()
            
            # 设置回调
            callbacks = [
                ModelCheckpoint(
                    filepath=str(self.models_dir / 'best_model.h5'),
                    monitor='val_loss',
                    save_best_only=True,
                    verbose=1
                ),
                EarlyStopping(
                    monitor='val_loss',
                    patience=5,
                    restore_best_weights=True
                ),
                ReduceLROnPlateau(
                    monitor='val_loss',
                    factor=0.2,
                    patience=3,
                    min_lr=1e-6
                )
            ]
            
            # 训练模型
            history = model.fit(
                X_train, y_train,
                validation_data=(X_val, y_val),
                epochs=self.config['model']['epochs'],
                batch_size=self.config['model']['batch_size'],
                callbacks=callbacks,
                verbose=1
            )
            
            # 记录指标
            mlflow.log_metrics({
                'val_accuracy': max(history.history['val_accuracy']),
                'val_precision': max(history.history['val_precision']),
                'val_recall': max(history.history['val_recall'])
            })
            
            # 保存模型
            self._save_model(model, history)
            
            return model, history.history
    
    def optimize_hyperparameters(self, X_train: np.ndarray, y_train: np.ndarray, 
                               X_val: np.ndarray, y_val: np.ndarray) -> Dict[str, Any]:
        """使用Optuna优化超参数"""
        def objective(trial):
            model = self.create_model(trial)
            
            callbacks = [
                TFKerasPruningCallback(trial, 'val_loss'),
                EarlyStopping(patience=5)
            ]
            
            history = model.fit(
                X_train, y_train,
                validation_data=(X_val, y_val),
                epochs=self.config['model']['epochs'],
                batch_size=trial.suggest_categorical('batch_size', [32, 64, 128]),
                callbacks=callbacks,
                verbose=0
            )
            
            return max(history.history['val_accuracy'])
        
        study = optuna.create_study(direction='maximize')
        study.optimize(objective, n_trials=self.config['model']['n_trials'])
        
        return study.best_params
    
    def _save_model(self, model: tf.keras.Model, history: Dict[str, Any]):
        """保存模型和元数据"""
        # 生成版本号
        self.model_version = datetime.now().strftime('%Y%m%d_%H%M%S')
        version_dir = self.models_dir / self.model_version
        version_dir.mkdir(exist_ok=True)
        
        # 保存模型
        model.save(str(version_dir / 'model.h5'))
        
        # 保存元数据
        metadata = {
            'version': self.model_version,
            'timestamp': datetime.now().isoformat(),
            'metrics': {
                'val_accuracy': max(history['val_accuracy']),
                'val_precision': max(history['val_precision']),
                'val_recall': max(history['val_recall'])
            },
            'config': self.config['model']
        }
        
        with open(version_dir / 'metadata.json', 'w') as f:
            json.dump(metadata, f, indent=2)
        
        # 记录到MLflow
        mlflow.log_artifact(str(version_dir / 'metadata.json'))
        mlflow.tensorflow.log_model(model, "model")
    
    def load_model(self, version: str = None) -> tf.keras.Model:
        """加载指定版本的模型"""
        if version is None:
            # 获取最新版本
            versions = sorted(self.models_dir.glob('*'))
            if not versions:
                raise ValueError("没有找到任何模型版本")
            version = versions[-1].name
        
        model_path = self.models_dir / version / 'model.h5'
        if not model_path.exists():
            raise ValueError(f"找不到版本 {version} 的模型")
        
        return load_model(str(model_path))
    
    def evaluate_model(self, model: tf.keras.Model, X_test: np.ndarray, y_test: np.ndarray) -> Dict[str, float]:
        """评估模型性能"""
        y_pred = model.predict(X_test)
        y_pred_binary = (y_pred > 0.5).astype(int)
        
        metrics = {
            'accuracy': accuracy_score(y_test, y_pred_binary),
            'precision': precision_score(y_test, y_pred_binary),
            'recall': recall_score(y_test, y_pred_binary),
            'f1': f1_score(y_test, y_pred_binary)
        }
        
        return metrics
    
    def run_ab_test(self, model_a: tf.keras.Model, model_b: tf.keras.Model, 
                   X_test: np.ndarray, y_test: np.ndarray) -> Dict[str, Any]:
        """运行A/B测试"""
        metrics_a = self.evaluate_model(model_a, X_test, y_test)
        metrics_b = self.evaluate_model(model_b, X_test, y_test)
        
        results = {
            'model_a': metrics_a,
            'model_b': metrics_b,
            'comparison': {
                'accuracy_diff': metrics_b['accuracy'] - metrics_a['accuracy'],
                'precision_diff': metrics_b['precision'] - metrics_a['precision'],
                'recall_diff': metrics_b['recall'] - metrics_a['recall'],
                'f1_diff': metrics_b['f1'] - metrics_a['f1']
            }
        }
        
        # 记录A/B测试结果
        with open(self.models_dir / 'ab_test_results.json', 'w') as f:
            json.dump(results, f, indent=2)
        
        return results
    
    def create_model_sklearn(self, input_dim: int) -> Any:
        """创建指定类型的模型"""
        if self.model_type == 'random_forest':
            return RandomForestClassifier(n_estimators=100, random_state=42)
        elif self.model_type == 'gradient_boosting':
            return GradientBoostingClassifier(n_estimators=100, random_state=42)
        elif self.model_type == 'svm':
            return SVC(probability=True, random_state=42)
        elif self.model_type == 'xgboost':
            return xgb.XGBClassifier(n_estimators=100, random_state=42)
        elif self.model_type == 'lightgbm':
            return lgb.LGBMClassifier(n_estimators=100, random_state=42)
        elif self.model_type == 'neural_network':
            model = Sequential([
                Dense(128, activation='relu', input_dim=input_dim),
                BatchNormalization(),
                Dropout(0.3),
                Dense(64, activation='relu'),
                BatchNormalization(),
                Dropout(0.2),
                Dense(32, activation='relu'),
                Dense(1, activation='sigmoid')
            ])
            model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
            return model
        else:
            raise ValueError(f"不支持的模型类型: {self.model_type}")
    
    def train(self, X: np.ndarray, y: np.ndarray, 
             test_size: float = 0.2, 
             random_state: int = 42) -> Dict[str, Any]:
        """训练模型"""
        # 划分训练集和测试集
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=test_size, random_state=random_state
        )
        
        # 创建模型
        self.model = self.create_model_sklearn(X_train.shape[1])
        
        # 训练模型
        if self.model_type == 'neural_network':
            # 设置早停和模型保存
            callbacks = [
                EarlyStopping(monitor='val_loss', patience=10),
                ModelCheckpoint('models/best_model.h5', save_best_only=True)
            ]
            
            # 训练神经网络
            history = self.model.fit(
                X_train, y_train,
                validation_split=0.2,
                epochs=100,
                batch_size=32,
                callbacks=callbacks,
                verbose=1
            )
            
            # 加载最佳模型
            self.model = tf.keras.models.load_model('models/best_model.h5')
        else:
            # 训练传统机器学习模型
            self.model.fit(X_train, y_train)
        
        # 评估模型
        metrics = self.evaluate(X_test, y_test)
        
        # 获取特征重要性
        if hasattr(self.model, 'feature_importances_'):
            self.feature_importance = self.model.feature_importances_
        elif hasattr(self.model, 'coef_'):
            self.feature_importance = np.abs(self.model.coef_[0])
        
        return metrics
    
    def evaluate(self, X: np.ndarray, y: np.ndarray) -> Dict[str, float]:
        """评估模型性能"""
        if self.model_type == 'neural_network':
            y_pred = (self.model.predict(X) > 0.5).astype(int)
        else:
            y_pred = self.model.predict(X)
        
        metrics = {
            'accuracy': accuracy_score(y, y_pred),
            'precision': precision_score(y, y_pred),
            'recall': recall_score(y, y_pred),
            'f1': f1_score(y, y_pred)
        }
        
        # 计算混淆矩阵
        cm = confusion_matrix(y, y_pred)
        metrics['confusion_matrix'] = cm.tolist()
        
        return metrics
    
    def optimize_hyperparameters_sklearn(self, X: np.ndarray, y: np.ndarray,
                               param_grid: Dict[str, List[Any]],
                               cv: int = 5) -> Dict[str, Any]:
        """优化模型超参数"""
        if self.model_type == 'neural_network':
            raise ValueError("神经网络模型不支持GridSearchCV优化")
        
        # 创建基础模型
        base_model = self.create_model_sklearn(X.shape[1])
        
        # 使用GridSearchCV进行参数优化
        grid_search = GridSearchCV(
            estimator=base_model,
            param_grid=param_grid,
            cv=cv,
            scoring='f1',
            n_jobs=-1
        )
        
        # 执行网格搜索
        grid_search.fit(X, y)
        
        # 保存最佳参数和模型
        self.best_params = grid_search.best_params_
        self.model = grid_search.best_estimator_
        
        return {
            'best_params': self.best_params,
            'best_score': grid_search.best_score_
        }
    
    def save_model(self, model_path: str):
        """保存模型"""
        if not os.path.exists('models'):
            os.makedirs('models')
        
        if self.model_type == 'neural_network':
            self.model.save(model_path)
        else:
            joblib.dump(self.model, model_path)
        
        # 保存模型信息
        model_info = {
            'model_type': self.model_type,
            'best_params': self.best_params,
            'feature_importance': self.feature_importance.tolist() if self.feature_importance is not None else None
        }
        
        with open(f'{model_path}.json', 'w') as f:
            json.dump(model_info, f)
    
    def load_model_sklearn(self, model_path: str):
        """加载模型"""
        if self.model_type == 'neural_network':
            self.model = tf.keras.models.load_model(model_path)
        else:
            self.model = joblib.load(model_path)
        
        # 加载模型信息
        with open(f'{model_path}.json', 'r') as f:
            model_info = json.load(f)
            self.best_params = model_info['best_params']
            self.feature_importance = np.array(model_info['feature_importance']) if model_info['feature_importance'] else None
    
    def predict(self, X: np.ndarray) -> np.ndarray:
        """使用模型进行预测"""
        if self.model is None:
            raise ValueError("模型尚未训练")
        
        if self.model_type == 'neural_network':
            return (self.model.predict(X) > 0.5).astype(int)
        else:
            return self.model.predict(X)
    
    def predict_proba(self, X: np.ndarray) -> np.ndarray:
        """预测概率"""
        if self.model is None:
            raise ValueError("模型尚未训练")
        
        if self.model_type == 'neural_network':
            return self.model.predict(X)
        else:
            return self.model.predict_proba(X) 