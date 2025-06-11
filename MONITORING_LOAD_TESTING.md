# 监控和负载测试指南

## 概述

本项目集成了完整的监控和负载测试解决方案：
- **Prometheus**: 指标收集
- **Grafana**: 可视化仪表板
- **K6**: 负载测试工具
- **Spring Boot Actuator**: 应用指标暴露

## 快速开始

### 1. 启动监控系统

```bash
# 启动所有服务（包括监控）
docker-compose up -d

# 或者单独启动监控组件
docker-compose up -d prometheus grafana
```

### 2. 访问监控界面

- **Grafana**: http://localhost:3001
  - 用户名: admin
  - 密码: admin123

- **Prometheus**: http://localhost:9090

- **应用健康检查**: http://localhost:8080/actuator/health

- **应用指标**: http://localhost:8080/actuator/prometheus

### 3. 运行负载测试

#### 方法一：本地运行

```bash
# 安装K6
# Windows: choco install k6
# macOS: brew install k6  
# Linux: sudo apt-get install k6

# 运行测试
cd load-testing
chmod +x run-load-test.sh
./run-load-test.sh
```

#### 方法二：GitHub Actions

1. 在GitHub仓库中，进入 "Actions" 标签
2. 选择 "Load Testing" 工作流
3. 点击 "Run workflow" 手动触发测试

## 监控指标说明

### 应用指标

- **HTTP请求**: 请求数量、响应时间、状态码分布
- **JVM指标**: 内存使用、垃圾回收、线程数
- **数据库连接池**: 活跃连接数、等待连接数
- **自定义业务指标**: 用户注册数、谣言分析数等

### 系统指标

- **CPU使用率**: 系统和应用CPU占用
- **内存使用**: 已用内存、可用内存
- **磁盘I/O**: 读写速度、IOPS
- **网络流量**: 入站/出站流量

## 负载测试场景

### 测试阶段
1. **预热阶段**: 2分钟内增加到20个并发用户
2. **稳定负载**: 保持20个用户5分钟
3. **压力测试**: 2分钟内增加到50个用户  
4. **峰值负载**: 保持50个用户5分钟
5. **缓降阶段**: 2分钟内减少到0个用户

### 性能阈值
- 95%的请求响应时间 < 500ms
- 错误率 < 10%
- 健康检查响应时间 < 200ms
- API响应时间 < 1000ms

### 测试端点
- `/actuator/health` - 健康检查
- `/api/rumors` - 主要业务API
- `/actuator/prometheus` - 指标端点

## 性能基准

### 预期性能指标
- **吞吐量**: >100 requests/second
- **并发用户**: 50个用户同时访问
- **响应时间**: 
  - P50 < 200ms
  - P95 < 500ms
  - P99 < 1000ms
- **可用性**: >99.9%

### 资源使用限制
- **CPU**: <80%
- **内存**: <2GB
- **数据库连接**: <50个活跃连接

## 故障排查

### 常见问题

1. **Prometheus无法采集指标**
   - 检查后端服务是否启动: `curl http://localhost:8080/actuator/health`
   - 检查Prometheus配置: `docker-compose logs prometheus`

2. **Grafana无法连接Prometheus**
   - 检查容器网络: `docker network ls`
   - 检查数据源配置: Grafana -> Configuration -> Data Sources

3. **负载测试失败**
   - 确保后端服务正在运行
   - 检查K6是否正确安装: `k6 version`

### 日志查看

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs prometheus
docker-compose logs grafana
```

## 自定义扩展

### 添加自定义指标

在Spring Boot应用中添加自定义指标：

```java
@Component
public class CustomMetrics {
    private final Counter userRegistrations;
    private final Timer rumorAnalysisTime;
    
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.userRegistrations = Counter.builder("user_registrations_total")
            .description("Total user registrations")
            .register(meterRegistry);
            
        this.rumorAnalysisTime = Timer.builder("rumor_analysis_duration")
            .description("Time taken to analyze rumors")
            .register(meterRegistry);
    }
}
```

### 自定义Grafana仪表板

1. 在Grafana中创建新仪表板
2. 导出JSON配置
3. 保存到 `monitoring/grafana/dashboards/` 目录

### 扩展负载测试

修改 `load-testing/k6-load-test.js` 添加新的测试场景：

```javascript
// 添加登录测试
let loginResponse = http.post(`${BASE_URL}/api/auth/login`, {
  username: 'testuser',
  password: 'testpass'
});
```

## 部署到生产环境

### 环境变量配置

```bash
# .env 文件
GRAFANA_ADMIN_PASSWORD=your_secure_password
PROMETHEUS_RETENTION=30d
```

### 安全配置

- 修改Grafana默认密码
- 配置Prometheus访问控制
- 使用HTTPS代理监控端点

### 数据持久化

确保Prometheus和Grafana数据持久化：
- 定期备份监控数据
- 配置数据保留策略
- 监控存储空间使用