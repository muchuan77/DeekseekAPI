import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// 自定义指标
export let errorRate = new Rate('errors');

// 测试配置
export let options = {
  // 测试阶段
  stages: [
    { duration: '2m', target: 20 }, // 2分钟内增加到20个用户
    { duration: '5m', target: 20 }, // 保持20个用户5分钟
    { duration: '2m', target: 50 }, // 2分钟内增加到50个用户
    { duration: '5m', target: 50 }, // 保持50个用户5分钟
    { duration: '2m', target: 0 },  // 2分钟内减少到0个用户
  ],
  
  // 性能阈值
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%的请求应在500ms内完成
    http_req_failed: ['rate<0.1'],    // 错误率应小于10%
    errors: ['rate<0.1'],             // 自定义错误率小于10%
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  // 测试健康检查端点
  let healthResponse = http.get(`${BASE_URL}/actuator/health`);
  check(healthResponse, {
    '健康检查状态为200': (r) => r.status === 200,
    '健康检查响应时间 < 200ms': (r) => r.timings.duration < 200,
  }) || errorRate.add(1);

  sleep(1);

  // 测试API端点（根据您的实际API调整）
  let apiResponse = http.get(`${BASE_URL}/api/rumors`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
  
  check(apiResponse, {
    'API状态为200或401': (r) => r.status === 200 || r.status === 401, // 401是未认证，也是正常的
    'API响应时间 < 1000ms': (r) => r.timings.duration < 1000,
  }) || errorRate.add(1);

  sleep(2);

  // 测试Prometheus指标端点
  let metricsResponse = http.get(`${BASE_URL}/actuator/prometheus`);
  check(metricsResponse, {
    'Prometheus指标状态为200': (r) => r.status === 200,
    'Prometheus指标响应时间 < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);

  sleep(1);
} 