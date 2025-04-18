# 谣言检测AI模型

这是一个基于机器学习的谣言检测系统，使用多种模型来识别和验证网络谣言。

## 功能特点

- 支持多种机器学习模型：
  - 随机森林
  - 梯度提升
  - 支持向量机
  - XGBoost
  - LightGBM
  - 神经网络

- 数据预处理：
  - 文本清洗和标准化
  - 特征提取
  - 数据标准化

- 模型训练和优化：
  - 自动超参数优化
  - 交叉验证
  - 模型评估和比较

- 结果分析：
  - 详细的评估指标
  - 特征重要性分析
  - 训练过程日志

## 环境要求

- Python 3.8+
- 依赖包：见 requirements.txt

## 安装步骤

1. 克隆项目：
```bash
git clone [项目地址]
cd ai-model
```

2. 创建虚拟环境：
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows
```

3. 安装依赖：
```bash
pip install -r requirements.txt
```

4. 下载NLTK数据：
```python
import nltk
nltk.download('punkt')
nltk.download('stopwords')
```

## 使用方法

### 基本训练

```bash
python train.py --model_type random_forest
```

### 带超参数优化的训练

```bash
python train.py --model_type xgboost --optimize
```

### 自定义测试集比例

```bash
python train.py --model_type neural_network --test_size 0.3
```

### 参数说明

- `--model_type`: 选择模型类型
  - 可选值：random_forest, gradient_boosting, svm, xgboost, lightgbm, neural_network
  - 默认值：random_forest

- `--optimize`: 是否进行超参数优化
  - 默认值：False

- `--test_size`: 测试集比例
  - 默认值：0.2

- `--random_state`: 随机种子
  - 默认值：42

## 项目结构

```
ai-model/
├── data/                  # 数据目录
├── models/                # 保存的模型
├── results/               # 训练结果
├── data_collector.py      # 数据收集器
├── data_preprocessor.py   # 数据预处理器
├── model_trainer.py       # 模型训练器
├── train.py              # 训练脚本
├── requirements.txt      # 项目依赖
└── README.md            # 项目说明
```

## 输出文件

- `training.log`: 训练过程日志
- `models/[model_type]_model`: 保存的模型文件
- `results/[model_type]_[timestamp].json`: 训练结果

## 注意事项

1. 确保有足够的内存和计算资源
2. 神经网络训练可能需要GPU支持
3. 数据预处理可能需要较长时间
4. 建议先使用小规模数据进行测试

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 许可证

MIT License 