import os
import json
import numpy as np
import pandas as pd
from data_collector import DataCollector
from data_preprocessor import DataPreprocessor
from model_trainer import ModelTrainer
from typing import Dict, Any, List
import argparse
import logging
from datetime import datetime

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('training.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def parse_args():
    """解析命令行参数"""
    parser = argparse.ArgumentParser(description='谣言检测模型训练脚本')
    parser.add_argument('--model_type', type=str, default='random_forest',
                      choices=['random_forest', 'gradient_boosting', 'svm', 
                              'xgboost', 'lightgbm', 'neural_network'],
                      help='要训练的模型类型')
    parser.add_argument('--optimize', action='store_true',
                      help='是否进行超参数优化')
    parser.add_argument('--test_size', type=float, default=0.2,
                      help='测试集比例')
    parser.add_argument('--random_state', type=int, default=42,
                      help='随机种子')
    return parser.parse_args()

def get_param_grid(model_type: str) -> Dict[str, List[Any]]:
    """获取不同模型类型的参数网格"""
    if model_type == 'random_forest':
        return {
            'n_estimators': [50, 100, 200],
            'max_depth': [None, 10, 20, 30],
            'min_samples_split': [2, 5, 10],
            'min_samples_leaf': [1, 2, 4]
        }
    elif model_type == 'gradient_boosting':
        return {
            'n_estimators': [50, 100, 200],
            'learning_rate': [0.01, 0.1, 0.2],
            'max_depth': [3, 5, 7],
            'min_samples_split': [2, 5, 10]
        }
    elif model_type == 'svm':
        return {
            'C': [0.1, 1, 10],
            'kernel': ['linear', 'rbf'],
            'gamma': ['scale', 'auto']
        }
    elif model_type == 'xgboost':
        return {
            'n_estimators': [50, 100, 200],
            'learning_rate': [0.01, 0.1, 0.2],
            'max_depth': [3, 5, 7],
            'subsample': [0.8, 0.9, 1.0]
        }
    elif model_type == 'lightgbm':
        return {
            'n_estimators': [50, 100, 200],
            'learning_rate': [0.01, 0.1, 0.2],
            'max_depth': [3, 5, 7],
            'num_leaves': [31, 63, 127]
        }
    else:
        return {}

def save_results(results: Dict[str, Any], model_type: str):
    """保存训练结果"""
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    results_dir = 'results'
    if not os.path.exists(results_dir):
        os.makedirs(results_dir)
    
    results_file = os.path.join(results_dir, f'{model_type}_{timestamp}.json')
    with open(results_file, 'w', encoding='utf-8') as f:
        json.dump(results, f, ensure_ascii=False, indent=4)
    
    logger.info(f"训练结果已保存到: {results_file}")

def main():
    # 解析命令行参数
    args = parse_args()
    
    try:
        # 收集数据
        logger.info("开始收集数据...")
        collector = DataCollector()
        collector.collect_all_data()
        
        # 加载数据
        rumors_df = pd.read_csv('data/rumors.csv')
        verifications_df = pd.read_csv('data/verifications.csv')
        users_df = pd.read_csv('data/users.csv')
        
        # 预处理数据
        logger.info("开始预处理数据...")
        preprocessor = DataPreprocessor()
        X, y = preprocessor.prepare_training_data(rumors_df, verifications_df, users_df)
        
        # 训练模型
        logger.info(f"开始训练{args.model_type}模型...")
        trainer = ModelTrainer(model_type=args.model_type)
        
        if args.optimize:
            logger.info("开始超参数优化...")
            param_grid = get_param_grid(args.model_type)
            optimization_results = trainer.optimize_hyperparameters(
                X, y, param_grid=param_grid
            )
            logger.info(f"最佳参数: {optimization_results['best_params']}")
            logger.info(f"最佳分数: {optimization_results['best_score']}")
        
        # 训练模型
        metrics = trainer.train(
            X, y,
            test_size=args.test_size,
            random_state=args.random_state
        )
        
        # 保存模型
        model_path = f'models/{args.model_type}_model'
        trainer.save_model(model_path)
        logger.info(f"模型已保存到: {model_path}")
        
        # 保存结果
        results = {
            'model_type': args.model_type,
            'metrics': metrics,
            'optimization': optimization_results if args.optimize else None,
            'feature_importance': trainer.feature_importance.tolist() if trainer.feature_importance is not None else None
        }
        save_results(results, args.model_type)
        
        # 打印结果
        logger.info("\n模型评估结果:")
        logger.info(f"准确率: {metrics['accuracy']:.4f}")
        logger.info(f"精确率: {metrics['precision']:.4f}")
        logger.info(f"召回率: {metrics['recall']:.4f}")
        logger.info(f"F1分数: {metrics['f1']:.4f}")
        
    except Exception as e:
        logger.error(f"训练过程中发生错误: {str(e)}")
        raise

if __name__ == '__main__':
    main() 