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
    version INT DEFAULT 0
);
```

### 2. 用户角色表 (user_roles)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (user_id, role)
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
    version INT DEFAULT 0
);
```

### 4. 评论表 (comments)
```sql
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 5. 谣言分析表 (rumor_analysis)
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
    FOREIGN KEY (rumor_id) REFERENCES rumors(id)
);
```

### 6. 传播路径表 (propagation_paths)
```sql
CREATE TABLE propagation_paths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    source VARCHAR(255),
    target VARCHAR(255),
    path_type VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id)
);
```

### 7. 影响分析表 (influence_analysis)
```sql
CREATE TABLE influence_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    metrics JSON,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id)
);
```

### 8. 虚假信息指标表 (misinformation_indicators)
```sql
CREATE TABLE misinformation_indicators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    indicator_type VARCHAR(50) NOT NULL,
    score FLOAT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id)
);
```

### 9. 事实核查点表 (fact_checking_points)
```sql
CREATE TABLE fact_checking_points (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    point_type VARCHAR(50) NOT NULL,
    content TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id)
);
```

### 10. AI分析表 (ai_analysis)
```sql
CREATE TABLE ai_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rumor_id BIGINT NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    result TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (rumor_id) REFERENCES rumors(id)
);
```

## 日志相关表

### 11. 系统日志表 (system_logs)
```sql
CREATE TABLE system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    source VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0
);
```

### 12. 操作日志表 (operation_logs)
```sql
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT,
    details TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 13. 审计日志表 (audit_logs)
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id BIGINT,
    details TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 索引说明

1. 用户表索引：
   - 主键索引：id
   - 唯一索引：username, email
   - 普通索引：status, enabled

2. 谣言表索引：
   - 主键索引：id
   - 普通索引：status, created_at

3. 评论表索引：
   - 主键索引：id
   - 外键索引：rumor_id, user_id

4. 日志表索引：
   - 主键索引：id
   - 普通索引：created_at, level (system_logs)
   - 普通索引：user_id, operation_type (operation_logs)
   - 普通索引：user_id, action (audit_logs)

## 注意事项

1. 所有表都包含审计字段（created_at, updated_at, created_by, updated_by）
2. 外键关系都设置了级联更新和删除
3. 时间字段使用 DATETIME 类型
4. 状态字段使用 VARCHAR 类型，便于扩展
5. 大文本字段使用 TEXT 类型
6. 数值类型根据实际需求选择 INT 或 BIGINT
7. 所有表都包含 version 字段用于乐观锁
8. 软删除使用 deleted 字段标记 