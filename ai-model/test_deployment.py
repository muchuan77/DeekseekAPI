import requests
import json
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# API基础URL
BASE_URL = "http://localhost:8000"

def test_detect_rumor():
    """测试谣言检测接口"""
    try:
        # 准备测试数据
        test_data = {
            "content": "某知名公司即将破产，员工集体离职",
            "source": "社交媒体",
            "metadata": {
                "platform": "微博",
                "author": "匿名用户",
                "timestamp": "2023-01-01T12:00:00Z"
            }
        }
        
        # 发送请求
        response = requests.post(
            f"{BASE_URL}/detect",
            json=test_data
        )
        
        # 检查响应
        if response.status_code == 200:
            result = response.json()
            logger.info("谣言检测成功")
            logger.info(f"检测结果: {json.dumps(result, indent=2, ensure_ascii=False)}")
        else:
            logger.error(f"谣言检测失败: {response.status_code} - {response.text}")
    except Exception as e:
        logger.error(f"测试谣言检测接口时发生错误: {str(e)}")

def test_verify_rumor():
    """测试谣言验证接口"""
    try:
        # 准备测试数据
        test_data = {
            "rumor_id": "1",
            "evidence": [
                "公司官方声明：经营状况良好",
                "员工在职证明",
                "财务报表"
            ],
            "comment": "经过多方验证，该消息不实"
        }
        
        # 发送请求
        response = requests.post(
            f"{BASE_URL}/verify",
            json=test_data
        )
        
        # 检查响应
        if response.status_code == 200:
            result = response.json()
            logger.info("谣言验证成功")
            logger.info(f"验证结果: {json.dumps(result, indent=2, ensure_ascii=False)}")
        else:
            logger.error(f"谣言验证失败: {response.status_code} - {response.text}")
    except Exception as e:
        logger.error(f"测试谣言验证接口时发生错误: {str(e)}")

def test_trace_rumor():
    """测试谣言追踪接口"""
    try:
        # 发送请求
        response = requests.get(f"{BASE_URL}/trace/1")
        
        # 检查响应
        if response.status_code == 200:
            result = response.json()
            logger.info("谣言追踪成功")
            logger.info(f"追踪结果: {json.dumps(result, indent=2, ensure_ascii=False)}")
        else:
            logger.error(f"谣言追踪失败: {response.status_code} - {response.text}")
    except Exception as e:
        logger.error(f"测试谣言追踪接口时发生错误: {str(e)}")

def test_get_rumor_history():
    """测试获取谣言历史接口"""
    try:
        # 发送请求
        response = requests.get(f"{BASE_URL}/history/1")
        
        # 检查响应
        if response.status_code == 200:
            result = response.json()
            logger.info("获取谣言历史成功")
            logger.info(f"历史记录: {json.dumps(result, indent=2, ensure_ascii=False)}")
        else:
            logger.error(f"获取谣言历史失败: {response.status_code} - {response.text}")
    except Exception as e:
        logger.error(f"测试获取谣言历史接口时发生错误: {str(e)}")

def run_all_tests():
    """运行所有测试"""
    logger.info("开始测试部署接口...")
    
    # 运行测试
    test_detect_rumor()
    test_verify_rumor()
    test_trace_rumor()
    test_get_rumor_history()
    
    logger.info("测试完成")

if __name__ == "__main__":
    run_all_tests() 