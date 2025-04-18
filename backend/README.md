# 谣言溯源系统后端服务

## 项目简介

本后端服务是谣言溯源系统的核心组件，基于Spring Boot 3.x开发，集成了DeepSeek API、Elasticsearch等技术，提供谣言检测、传播追踪等核心功能。

### 核心特性
- 多模态谣言检测：支持文本、图片、视频等多种形式的内容分析
- 智能传播追踪：基于图论和机器学习算法分析传播路径
- 实时数据分析：提供多维度的数据统计和可视化
- 权限管理：基于RBAC的细粒度权限控制
- 软删除支持：所有实体支持软删除
- 乐观锁支持：所有实体支持乐观锁
- 审计日志：记录所有关键操作

## 技术架构

### 核心框架
- Spring Boot 3.x：提供快速开发框架
- Spring Security：安全认证和授权
- Spring Data JPA：数据持久化
- Spring Data Redis：缓存管理
- Spring Data Elasticsearch：全文搜索
- Spring Data REST：RESTful API支持
- Spring Data Envers：审计日志支持

### 数据存储
- MySQL 8.0：关系型数据存储
  - 用户信息
  - 谣言记录
  - 操作日志
  - 评论系统
  - 分析结果
- Redis：缓存服务
  - 会话管理
  - 热点数据缓存
- Elasticsearch：全文搜索
  - 谣言内容索引
  - 快速检索

### AI服务集成
- DeepSeek API
  - 文本分析
  - 图像识别
  - 视频分析
  - 多模态融合

### 开发工具
- Lombok：简化代码
- MapStruct：对象映射
- Maven：依赖管理
- Swagger：API文档
- JaCoCo：代码覆盖率
- Checkstyle：代码规范检查
- SpotBugs：代码缺陷检测

## 系统要求

### 开发环境
- JDK 18+
- Maven 3.8+
- Docker 20.10+
- Docker Compose 2.0+

### 运行环境
- MySQL 8.0+
- Redis 6.x+
- Elasticsearch 7.17.4+

## 快速开始

### 1. 环境准备

#### 数据库配置
```sql
CREATE DATABASE rumor_tracing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
```

#### Elasticsearch配置
```yaml
spring:
  elasticsearch:
    rest:
      uris: http://localhost:9200
      username: elastic
      password: your_password
```

### 2. 编译项目
```bash
# 清理并编译
mvn clean install

# 跳过测试
mvn clean install -DskipTests
```

### 3. 运行项目
```bash
# 开发环境
mvn spring-boot:run

# 生产环境
java -jar target/rumor-tracing-1.0.0.jar
```

## 项目结构

```
src/main/java/com/rumor/tracing/
├── config/          # 配置类
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   ├── MessageConfig.java
│   ├── LoggingConfig.java
│   ├── AIConfig.java
│   └── JpaConfig.java
├── controller/      # 控制器层
│   ├── AuthController.java
│   ├── UserController.java
│   ├── RumorController.java
│   ├── CommentController.java
│   ├── AnalyticsController.java
│   ├── LogController.java
│   ├── PermissionController.java
│   └── SystemController.java
├── service/         # 服务层
│   ├── impl/
│   │   ├── UserServiceImpl.java
│   │   ├── RumorServiceImpl.java
│   │   ├── OperationLogServiceImpl.java
│   │   └── PropagationAnalysisServiceImpl.java
│   ├── AuthService.java
│   ├── UserService.java
│   ├── RumorService.java
│   ├── CommentService.java
│   ├── AnalyticsService.java
│   ├── OperationLogService.java
│   ├── PropagationAnalysisService.java
│   └── LoggingService.java
├── repository/      # 数据访问层
├── entity/         # 实体类
│   ├── User.java
│   ├── Rumor.java
│   ├── Comment.java
│   ├── OperationLog.java
│   ├── RumorAnalysis.java
│   ├── InfluenceAnalysis.java
│   ├── MisinformationIndicator.java
│   ├── FactCheckingPoint.java
│   ├── AIAnalysis.java
│   ├── AuditLog.java
│   ├── SystemLog.java
│   ├── UserRole.java
│   └── BaseEntity.java
├── dto/            # 数据传输对象
├── exception/      # 异常处理
├── security/       # 安全相关
├── ai/            # AI服务相关
│   ├── dto/
│   └── service/
├── aspect/        # AOP切面
├── annotation/    # 自定义注解
└── util/          # 工具类
```

## 核心功能实现

### 1. 用户认证与授权
- JWT token认证
  - 登录认证
  - 用户注册
  - 密码重置
  - Token刷新
- 基于角色的访问控制
  - ADMIN：系统管理员
  - MODERATOR：内容审核员
  - USER：普通用户
- 权限注解
  - @RequiresAdmin
  - @RequiresModerator
  - @RequiresUser

### 2. 谣言检测服务
- 文本分析
  - 关键词提取
  - 情感分析
  - 可信度评估
- 图像识别
  - 图像内容分析
  - 图像篡改检测
- 视频分析
  - 视频内容识别
  - 关键帧提取
- 多模态融合
  - 特征融合
  - 决策融合

### 3. 传播追踪服务
- 传播路径分析
  - 传播网络构建
  - 关键节点识别
  - 传播路径可视化
- 影响力评估
  - 用户影响力计算
  - 内容传播力评估
  - 传播路径分析
- 实时监控
  - 传播趋势分析
  - 异常传播检测
  - 传播路径追踪

### 4. 日志管理服务
- 操作日志
  - 用户操作记录
  - 系统操作记录
  - 审计日志
- 系统日志
  - 错误日志
  - 性能日志
  - 安全日志
- 日志分析
  - 日志聚合
  - 异常检测
  - 性能分析

### 5. AI分析服务
- 多模态分析
  - 文本分析
  - 图像识别
  - 视频分析
- 智能检测
  - 谣言识别
  - 情感分析
  - 可信度评估
- 分析结果
  - 分析报告
  - 指标统计
  - 趋势预测

## API文档

系统使用SpringDoc OpenAPI提供API文档：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI规范：`http://localhost:8080/v3/api-docs`

## 测试

### 单元测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=YourTestClass
```

### 集成测试
```bash
# 运行集成测试
mvn verify -Pintegration-test
```

### 测试覆盖率
```bash
# 生成测试覆盖率报告
mvn test jacoco:report
```

## 部署

### Docker部署
```bash
# 构建镜像
docker build -t rumor-tracing-backend .

# 运行容器
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/rumor_tracing \
  -e SPRING_REDIS_HOST=redis \
  -e SPRING_ELASTICSEARCH_REST_URIS=http://elasticsearch:9200 \
  rumor-tracing-backend
```

### Kubernetes部署
```bash
# 部署应用
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# 查看部署状态
kubectl get pods
kubectl get services
```

## 监控与日志

### 日志配置
```yaml
logging:
  level:
    root: INFO
    com.rumor.tracing: DEBUG
  file:
    name: logs/application.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 监控指标
- JVM监控
- 数据库监控
- API性能监控
- 系统资源监控

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 使用MapStruct进行对象映射
- 提交信息遵循Angular提交规范

### 分支管理
- main：主分支
- develop：开发分支
- feature/*：功能分支
- hotfix/*：修复分支

### 版本管理
- 遵循语义化版本
- 主版本号：重大更新
- 次版本号：功能更新
- 修订号：问题修复

## 常见问题

### 数据库连接问题
1. 检查MySQL服务状态
2. 验证数据库连接信息
3. 检查网络连接

### Redis连接问题
1. 检查Redis服务状态
2. 验证Redis配置
3. 检查防火墙设置

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 发起Pull Request

## 许可证

MIT License 