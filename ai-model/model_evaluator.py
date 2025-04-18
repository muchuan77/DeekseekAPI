import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import (
    accuracy_score, precision_score, recall_score, f1_score,
    roc_auc_score, confusion_matrix, classification_report
)
from typing import Dict, List, Tuple, Any
import logging
import json
import os
from datetime import datetime

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class ModelEvaluator:
    def __init__(self, models: Dict[str, Any], X_test: np.ndarray, y_test: np.ndarray):
        """
        初始化模型评估器
        
        Args:
            models: 要评估的模型字典，格式为 {模型名称: 模型实例}
            X_test: 测试集特征
            y_test: 测试集标签
        """
        self.models = models
        self.X_test = X_test
        self.y_test = y_test
        self.results = {}
        self.metrics = [
            'accuracy', 'precision', 'recall', 'f1',
            'roc_auc', 'confusion_matrix', 'classification_report'
        ]
        
    def evaluate_models(self) -> Dict[str, Dict[str, Any]]:
        """评估所有模型"""
        for model_name, model in self.models.items():
            logger.info(f"正在评估模型: {model_name}")
            self.results[model_name] = self._evaluate_single_model(model)
        
        return self.results
    
    def _evaluate_single_model(self, model: Any) -> Dict[str, Any]:
        """评估单个模型"""
        # 预测
        y_pred = model.predict(self.X_test)
        y_pred_proba = model.predict_proba(self.X_test)[:, 1] if hasattr(model, 'predict_proba') else None
        
        # 计算指标
        metrics = {
            'accuracy': accuracy_score(self.y_test, y_pred),
            'precision': precision_score(self.y_test, y_pred),
            'recall': recall_score(self.y_test, y_pred),
            'f1': f1_score(self.y_test, y_pred),
            'confusion_matrix': confusion_matrix(self.y_test, y_pred).tolist(),
            'classification_report': classification_report(self.y_test, y_pred, output_dict=True)
        }
        
        if y_pred_proba is not None:
            metrics['roc_auc'] = roc_auc_score(self.y_test, y_pred_proba)
        
        return metrics
    
    def plot_metrics_comparison(self, save_path: str = None):
        """绘制模型指标比较图"""
        metrics_df = pd.DataFrame()
        
        for model_name, result in self.results.items():
            model_metrics = {
                'Model': model_name,
                'Accuracy': result['accuracy'],
                'Precision': result['precision'],
                'Recall': result['recall'],
                'F1 Score': result['f1']
            }
            if 'roc_auc' in result:
                model_metrics['ROC AUC'] = result['roc_auc']
            
            metrics_df = pd.concat([metrics_df, pd.DataFrame([model_metrics])])
        
        # 设置绘图风格
        plt.style.use('seaborn')
        fig, axes = plt.subplots(2, 2, figsize=(15, 12))
        fig.suptitle('模型性能比较', fontsize=16)
        
        # 绘制各个指标的条形图
        metrics = ['Accuracy', 'Precision', 'Recall', 'F1 Score']
        for i, metric in enumerate(metrics):
            ax = axes[i//2, i%2]
            sns.barplot(data=metrics_df, x='Model', y=metric, ax=ax)
            ax.set_title(f'{metric} 比较')
            ax.set_xticklabels(ax.get_xticklabels(), rotation=45)
        
        plt.tight_layout()
        
        if save_path:
            plt.savefig(save_path)
            logger.info(f"已保存比较图到: {save_path}")
        else:
            plt.show()
    
    def plot_confusion_matrices(self, save_path: str = None):
        """绘制混淆矩阵"""
        n_models = len(self.models)
        fig, axes = plt.subplots(1, n_models, figsize=(5*n_models, 5))
        if n_models == 1:
            axes = [axes]
        
        for i, (model_name, result) in enumerate(self.results.items()):
            cm = np.array(result['confusion_matrix'])
            sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', ax=axes[i])
            axes[i].set_title(f'{model_name} 混淆矩阵')
            axes[i].set_xlabel('预测标签')
            axes[i].set_ylabel('真实标签')
        
        plt.tight_layout()
        
        if save_path:
            plt.savefig(save_path)
            logger.info(f"已保存混淆矩阵图到: {save_path}")
        else:
            plt.show()
    
    def save_results(self, output_dir: str = 'evaluation_results'):
        """保存评估结果"""
        # 创建输出目录
        os.makedirs(output_dir, exist_ok=True)
        
        # 生成时间戳
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        
        # 保存评估结果
        results_file = os.path.join(output_dir, f'evaluation_results_{timestamp}.json')
        with open(results_file, 'w', encoding='utf-8') as f:
            json.dump(self.results, f, ensure_ascii=False, indent=4)
        
        # 保存图表
        metrics_plot = os.path.join(output_dir, f'metrics_comparison_{timestamp}.png')
        confusion_plot = os.path.join(output_dir, f'confusion_matrices_{timestamp}.png')
        
        self.plot_metrics_comparison(metrics_plot)
        self.plot_confusion_matrices(confusion_plot)
        
        logger.info(f"评估结果已保存到: {output_dir}")
    
    def get_best_model(self, metric: str = 'f1') -> Tuple[str, float]:
        """获取最佳模型"""
        if not self.results:
            raise ValueError("请先运行 evaluate_models() 方法")
        
        best_model = None
        best_score = -1
        
        for model_name, result in self.results.items():
            if result[metric] > best_score:
                best_score = result[metric]
                best_model = model_name
        
        return best_model, best_score

def main():
    """示例用法"""
    from sklearn.datasets import make_classification
    from sklearn.model_selection import train_test_split
    from sklearn.ensemble import RandomForestClassifier
    from sklearn.svm import SVC
    from sklearn.linear_model import LogisticRegression
    
    # 生成示例数据
    X, y = make_classification(n_samples=1000, n_features=20, random_state=42)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # 训练多个模型
    models = {
        'Random Forest': RandomForestClassifier(random_state=42),
        'SVM': SVC(probability=True, random_state=42),
        'Logistic Regression': LogisticRegression(random_state=42)
    }
    
    for model_name, model in models.items():
        logger.info(f"训练模型: {model_name}")
        model.fit(X_train, y_train)
    
    # 评估模型
    evaluator = ModelEvaluator(models, X_test, y_test)
    results = evaluator.evaluate_models()
    
    # 保存结果
    evaluator.save_results()
    
    # 获取最佳模型
    best_model, best_score = evaluator.get_best_model()
    logger.info(f"最佳模型: {best_model}, F1分数: {best_score:.4f}")

if __name__ == "__main__":
    main() 