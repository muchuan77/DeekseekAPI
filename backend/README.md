# 谣言溯源系统后端服务

## 项目简介

本后端服务是谣言溯源系统的核心组件，基于Spring Boot 2.7.12开发，集成了DeepSeek API、Elasticsearch、区块链等技术，提供谣言检测、传播追踪等核心功能。

### 🚀 核心特性
- **多模态谣言检测**: 支持文本、图片、视频等多种形式的内容分析
- **智能传播追踪**: 基于图论和机器学习算法分析传播路径
- **区块链存证**: 关键数据上链，确保数据不可篡改
- **实时数据分析**: 提供多维度的数据统计和可视化
- **权限管理**: 基于RBAC的细粒度权限控制
- **软删除支持**: 所有实体支持软删除
- **乐观锁支持**: 所有实体支持乐观锁
- **审计日志**: 记录所有关键操作
- **分布式缓存**: 使用Redis进行缓存管理
- **全文搜索**: 使用Elasticsearch进行高效检索
- **多环境配置**: 支持开发、测试、生产环境配置
- **Excel导出**: 支持数据导出为Excel格式
- **监控指标**: Spring Boot Actuator集成Prometheus监控
- **自动化测试**: 完整的单元测试和集成测试
- **代码质量**: SonarCloud质量分析和JaCoCo覆盖率报告
- **CI/CD集成**: GitHub Actions自动化构建和部署

## 技术架构

### 核心框架
- Spring Boot 2.7.12：提供快速开发框架
- Spring Security：安全认证和授权
- Spring Data JPA：数据持久化
- Spring Data Redis：缓存管理
- Spring Data Elasticsearch：全文搜索
- Spring Cloud OpenFeign：服务间通信
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
  - 分布式锁
- Elasticsearch 7.17.4：全文搜索
  - 谣言内容索引
  - 快速检索
  - 聚合分析
- 区块链：数据存证
  - 关键数据上链
  - 数据不可篡改
  - 时间戳服务

### AI服务集成
- DeepSeek API
  - 文本分析
  - 图像识别
  - 视频分析
  - 多模态融合
  - 情感分析
  - 可信度评估

### 开发工具与监控
- **代码工具**
  - Lombok：简化代码
  - MapStruct 1.5.3：对象映射
  - Maven：依赖管理
  - SpringDoc OpenAPI 1.7.0：API文档
  - Apache POI 5.2.3：Excel处理
- **监控与测试**
  - Spring Boot Actuator：应用监控指标
  - Micrometer：指标收集框架
  - JaCoCo：代码覆盖率分析
  - JUnit 5：单元测试框架
  - Testcontainers：集成测试
- **CI/CD工具**
  - GitHub Actions：自动化CI/CD
  - SonarCloud：代码质量分析
  - Docker：容器化部署
  - Maven Surefire：测试报告

## 系统要求

### 开发环境
- JDK 18+
- Maven 3.8+
- Git 2.30+
- IDE (推荐使用IntelliJ IDEA)

### 运行环境
- MySQL 8.0+
- Redis 6.x+
- Elasticsearch 7.17.4+
- 区块链节点
- 操作系统：Linux/Windows/macOS

## 🚀 快速开始

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
    database: 0
    timeout: 10000
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1
```

#### Elasticsearch配置
```yaml
spring:
  elasticsearch:
    rest:
      uris: http://localhost:9200
      username: elastic
      password: your_password
    connection-timeout: 5000
    socket-timeout: 5000
```

### 2. 编译与测试
```bash
# 清理并编译
mvn clean install

# 运行单元测试
mvn test

# 生成代码覆盖率报告
mvn clean test jacoco:report

# 跳过测试快速构建
mvn clean install -DskipTests
```

### 3. 运行项目
```bash
# 开发环境
mvn spring-boot:run

# 指定配置文件
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境
java -jar target/rumor-tracing-1.0.0.jar

# 启用监控端点
java -jar target/rumor-tracing-1.0.0.jar --management.endpoints.web.exposure.include=*
```

### 4. 监控检查
```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 查看应用信息
curl http://localhost:8080/actuator/info

# 查看指标
curl http://localhost:8080/actuator/metrics

# Prometheus指标
curl http://localhost:8080/actuator/prometheus
```

## 项目结构

```
src/main/java/com/rumor/tracing/
├── config/          # 配置类
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   ├── MessageConfig.java
│   ├── LogConfig.java
│   ├── LogSecurityConfig.java
│   ├── LogElasticsearchConfig.java
│   ├── JpaConfig.java
│   ├── RestTemplateConfig.java
│   └── BlockchainConfig.java
├── controller/      # 控制器层
│   ├── AuthController.java
│   ├── UserController.java
│   ├── RumorController.java
│   ├── CommentController.java
│   ├── DeepSeekAnalysisController.java
│   ├── LogController.java
│   ├── PermissionController.java
│   ├── PropagationController.java
│   ├── BlockchainController.java
│   └── SystemController.java
├── service/         # 服务层
│   ├── impl/
│   │   ├── UserServiceImpl.java
│   │   ├── RumorServiceImpl.java
│   │   ├── OperationLogServiceImpl.java
│   │   ├── PropagationAnalysisServiceImpl.java
│   │   ├── DeepSeekAIServiceImpl.java
│   │   └── BlockchainServiceImpl.java
│   ├── AuthService.java
│   ├── UserService.java
│   ├── RumorService.java
│   ├── CommentService.java
│   ├── DeepSeekAIService.java
│   ├── OperationLogService.java
│   ├── PropagationAnalysisService.java
│   ├── LoggingService.java
│   ├── PermissionService.java
│   ├── BlockchainService.java
│   └── SystemService.java
├── repository/      # 数据访问层
├── entity/         # 实体类
│   ├── User.java
│   ├── Rumor.java
│   ├── Comment.java
│   ├── LogOperation.java
│   ├── LogAudit.java
│   ├── LogSystem.java
│   ├── RumorAnalysis.java
│   ├── DeepSeekAnalysis.java
│   ├── PropagationPath.java
│   ├── InfluenceAnalysis.java
│   ├── BlockchainRecord.java
│   └── BaseEntity.java
├── dto/            # 数据传输对象
├── exception/      # 异常处理
├── security/       # 安全相关
├── model/          # 数据模型
└── RumorTracingApplication.java
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
  - @RequiresPermission

### 2. 谣言检测服务
- 文本分析
  - 关键词提取
  - 情感分析
  - 可信度评估
  - 语义分析
  - 实体识别
- 图像识别
  - 图像内容分析
  - 图像篡改检测
  - OCR文字识别
  - 图像相似度分析
- 视频分析
  - 视频内容识别
  - 关键帧提取
  - 视频摘要生成
  - 视频相似度分析
- 多模态融合
  - 特征融合
  - 决策融合
  - 跨模态检索
  - 多模态分类

### 3. 传播追踪服务
- 传播路径分析
  - 传播网络构建
  - 关键节点识别
  - 传播路径可视化
  - 传播速度分析
  - 传播范围分析
- 影响力评估
  - 用户影响力计算
  - 内容传播力评估
  - 传播路径分析
  - 影响力预测
- 实时监控
  - 传播趋势分析
  - 异常传播检测
  - 传播路径追踪
  - 实时告警

### 4. 区块链存证服务
- 数据上链
  - 关键数据哈希
  - 时间戳服务
  - 数据验证
  - 存证查询
- 智能合约
  - 存证合约
  - 验证合约
  - 查询合约
- 区块链集成
  - 节点管理
  - 交易管理
  - 区块查询
  - 事件监听

### 5. 日志管理服务
- 操作日志
  - 用户操作记录
  - 系统操作记录
  - 审计日志
  - 性能日志
- 系统日志
  - 错误日志
  - 性能日志
  - 安全日志
  - 访问日志
- 日志分析
  - 日志聚合
  - 异常检测
  - 性能分析
  - 安全分析

## API文档

系统使用SpringDoc OpenAPI提供API文档：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI规范：`http://localhost:8080/v3/api-docs`

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 使用MapStruct进行对象映射
- 提交信息遵循Angular提交规范
- 代码注释规范
- 命名规范
- 异常处理规范

### 分支管理
- main：主分支
- develop：开发分支
- feature/*：功能分支
- hotfix/*：修复分支
- release/*：发布分支

### 版本管理
- 遵循语义化版本
- 主版本号：重大更新
- 次版本号：功能更新
- 修订号：问题修复
- 预发布版本：alpha/beta/rc

## 常见问题

### 数据库连接问题
1. 检查MySQL服务状态
2. 验证数据库连接信息
3. 检查网络连接
4. 检查连接池配置
5. 检查数据库权限

### Redis连接问题
1. 检查Redis服务状态
2. 验证Redis配置
3. 检查防火墙设置
4. 检查网络连接
5. 检查Redis版本兼容性

### Elasticsearch连接问题
1. 检查Elasticsearch服务状态
2. 验证Elasticsearch配置
3. 检查网络连接
4. 检查索引状态
5. 检查集群健康状态

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 发起Pull Request
5. 等待审核
6. 合并代码

## 许可证

MIT License 