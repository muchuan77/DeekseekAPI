# 数据库表结构文档

## 基础实体 (BaseEntity)
所有实体类都继承自 `BaseEntity`，包含以下基础字段：
- `id`: 主键
- `createdAt`: 创建时间
- `updatedAt`: 更新时间
- `createdBy`: 创建人
- `updatedBy`: 更新人
- `version`: 版本号（用于乐观锁）

## 用户相关表

### 1. 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    phone_number VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    deleted BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ENABLED',
    last_login DATETIME,
    login_attempts INT DEFAULT 0,
    account_locked_until DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_enabled (enabled)
);
```

### 2. 用户角色表 (user_roles)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role),
    INDEX idx_role (role)
);
```

## 谣言相关表

### 3. 谣言表 (rumors)
```sql
CREATE TABLE rumors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    source VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    verification_result TEXT,
    verified BOOLEAN DEFAULT FALSE,
    verified_by BIGINT,
    verified_at DATETIME,
    deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_verified (verified),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (verified_by) REFERENCES users(id)
);
```

### 4. 评论表 (comments)
```sql
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_rumor_id (rumor_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
);
```

### 5. DeepSeek分析表 (deepseek_analysis)
```sql
CREATE TABLE deepseek_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    result TEXT,
    confidence DOUBLE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id) ON DELETE CASCADE,
    INDEX idx_rumor_id (rumor_id),
    INDEX idx_analysis_type (analysis_type)
);
```

### 6. 传播路径表 (propagation_paths)
```sql
CREATE TABLE propagation_paths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    source_node VARCHAR(255) NOT NULL,
    target_node VARCHAR(255) NOT NULL,
    propagation_time BIGINT NOT NULL,
    path_length INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id) ON DELETE CASCADE,
    INDEX idx_rumor_id (rumor_id),
    INDEX idx_source_node (source_node),
    INDEX idx_target_node (target_node),
    INDEX idx_type (type)
);
```

### 7. 影响力分析表 (influence_analysis)
```sql
CREATE TABLE influence_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    node_id VARCHAR(255) NOT NULL,
    influence_score DOUBLE NOT NULL,
    path_count INT NOT NULL,
    is_key_node BOOLEAN NOT NULL,
    analysis_time BIGINT NOT NULL,
    user_count INT NOT NULL,
    content_count INT NOT NULL,
    analysis_time_original DATETIME NOT NULL,
    analysis_status VARCHAR(50) NOT NULL,
    analysis_result TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id) ON DELETE CASCADE,
    INDEX idx_rumor_id (rumor_id),
    INDEX idx_node_id (node_id),
    INDEX idx_analysis_time (analysis_time),
    INDEX idx_is_key_node (is_key_node)
);

CREATE TABLE influence_analysis_user_influence (
    analysis_id BIGINT NOT NULL,
    user_influence VARCHAR(255),
    FOREIGN KEY (analysis_id) REFERENCES influence_analysis(id) ON DELETE CASCADE,
    INDEX idx_analysis_id (analysis_id)
);

CREATE TABLE influence_analysis_content_influence (
    analysis_id BIGINT NOT NULL,
    content_influence VARCHAR(255),
    FOREIGN KEY (analysis_id) REFERENCES influence_analysis(id) ON DELETE CASCADE,
    INDEX idx_analysis_id (analysis_id)
);
```

### 8. 谣言分析表 (rumor_analysis)
```sql
CREATE TABLE rumor_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    result TEXT,
    confidence FLOAT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id) ON DELETE CASCADE,
    INDEX idx_rumor_id (rumor_id),
    INDEX idx_analysis_type (analysis_type)
);
```

## 日志相关表

### 9. 系统日志表 (system_logs)
```sql
CREATE TABLE system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    source VARCHAR(255),
    application_name VARCHAR(100),
    stack_trace TEXT,
    response_time BIGINT,
    log_time DATETIME NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    INDEX idx_level (level),
    INDEX idx_log_time (log_time),
    INDEX idx_application_name (application_name)
);
```

### 10. 操作日志表 (operation_logs)
```sql
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT,
    details TEXT,
    execution_time DATETIME NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_target_type (target_type),
    INDEX idx_target_id (target_id)
);
```

### 11. 审计日志表 (audit_logs)
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT,
    details TEXT,
    audit_time DATETIME NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_target_type (target_type),
    INDEX idx_target_id (target_id)
);
```

## 索引说明

1. 用户表索引：
   - 主键索引：id
   - 唯一索引：username, email
   - 普通索引：status, enabled

2. 谣言表索引：
   - 主键索引：id
   - 普通索引：status, created_at, verified, deleted
   - 外键索引：verified_by

3. 评论表索引：
   - 主键索引：id
   - 外键索引：rumor_id, user_id, parent_id
   - 普通索引：created_at

4. DeepSeek分析表索引：
   - 主键索引：id
   - 外键索引：rumor_id
   - 普通索引：analysis_type

5. 传播路径表索引：
   - 主键索引：id
   - 外键索引：rumor_id
   - 普通索引：source_node, target_node, type

6. 影响力分析表索引：
   - 主键索引：id
   - 外键索引：rumor_id
   - 普通索引：node_id, analysis_time, is_key_node
   - 关联表索引：analysis_id

7. 日志表索引：
   - 主键索引：id
   - 普通索引：created_at, level (system_logs)
   - 普通索引：user_id, operation_type (operation_logs)
   - 普通索引：user_id, action (audit_logs)
   - 普通索引：application_name, log_time (system_logs)
   - 普通索引：target_type, target_id (operation_logs, audit_logs)

## 注意事项

1. 所有表都包含审计字段（created_at, updated_at, created_by, updated_by）
2. 外键关系都设置了级联更新和删除
3. 时间字段使用 DATETIME 类型
4. 状态字段使用 VARCHAR 类型，便于扩展
5. 大文本字段使用 TEXT 类型
6. 数值类型根据实际需求选择 INT 或 BIGINT
7. 所有表都包含 version 字段用于乐观锁
8. 软删除使用 deleted 字段标记
9. 所有表都添加了适当的索引以提高查询性能
10. JSON 类型字段用于存储复杂的数据结构
11. 所有外键都添加了索引以提高关联查询性能
12. 时间戳字段都添加了索引以支持时间范围查询
13. 影响力分析表使用关联表存储多值字段
14. 传播路径表使用 BIGINT 存储时间戳
15. 分析结果使用 TEXT 类型存储，支持大文本内容 