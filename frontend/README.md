# 谣言溯源系统前端

基于 Vue.js 构建的谣言溯源系统前端应用。

## 项目概述

前端使用 Vue.js 框架构建，具有以下主要特性：
- Vue 3 + Composition API
- Vue Router 用于导航
- Pinia 用于状态管理
- Axios 用于 API 请求
- Element Plus 用于 UI 组件
- Vite 用于构建工具

## 目录结构

```
frontend/
├── src/                    # 源代码文件
│   ├── api/               # API 服务模块
│   │   ├── auth.js        # 认证相关 API
│   │   ├── user.js        # 用户管理 API
│   │   ├── rumor.js       # 谣言管理 API
│   │   ├── comment.js     # 评论管理 API
│   │   ├── blockchain.js  # 区块链存证 API
│   │   ├── system.js      # 系统管理 API
│   │   ├── permission.js  # 权限管理 API
│   │   ├── log.js         # 日志管理 API
│   │   ├── propagation.js # 传播分析 API
│   │   └── ai.js          # AI分析 API
│   ├── assets/            # 静态资源（图片、样式等）
│   ├── components/        # 公共组件
│   ├── layouts/           # 布局组件
│   ├── router/            # Vue Router 配置
│   ├── stores/            # Pinia 状态管理
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   │   ├── admin/         # 管理后台页面
│   │   │   ├── Dashboard.vue
│   │   │   ├── UserManagement.vue
│   │   │   ├── PermissionManagement.vue
│   │   │   ├── SystemSettings.vue
│   │   │   ├── Analytics.vue
│   │   │   ├── LogManagement.vue
│   │   │   └── AIManagement.vue
│   │   ├── blockchain/    # 区块链相关页面
│   │   │   ├── Evidence.vue
│   │   │   └── Transaction.vue
│   │   ├── rumor/         # 谣言相关页面
│   │   │   ├── List.vue
│   │   │   ├── Detail.vue
│   │   │   ├── Create.vue
│   │   │   └── Propagation.vue
│   │   └── user/          # 用户相关页面
│   │       ├── Login.vue
│   │       ├── Register.vue
│   │       └── Profile.vue
│   ├── App.vue            # 根组件
│   └── main.js            # 应用入口文件
├── public/                # 公共静态文件
├── tests/                 # 测试文件
├── .env                   # 环境变量
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── vite.config.js         # Vite 配置
├── package.json           # 项目依赖
└── README.md              # 项目文档
```

## 主要功能模块

### 认证模块
- 用户登录/注册
- JWT 认证
- 令牌刷新
- 权限控制

### 用户管理
- 用户信息管理
- 用户角色分配
- 用户状态管理

### 谣言管理
- 谣言创建和编辑
- 谣言列表和详情
- 谣言状态管理
- 谣言分析结果展示

### 评论系统
- 评论发布
- 评论列表
- 评论管理

### 区块链存证
- 存证信息展示
- 存证验证
- 交易记录查询
- 存证历史追踪

### 系统管理
- 系统配置
- 系统监控
- 日志管理
- 告警管理

### 权限管理
- 权限列表管理
- 权限创建和编辑
- 权限状态管理
- 角色管理
- 角色权限分配
- 用户角色管理

### 日志管理
- 操作日志查看
- 系统日志查看
- 日志分析
- 日志导出

### 传播分析
- 传播路径可视化
- 影响力分析
- 传播趋势分析
- 关键节点识别

### AI分析
- 文本分析
- 图像分析
- 视频分析
- 分析结果展示
- 多模态融合分析

## 页面关系说明

### 1. 基础页面
位于 `/views` 目录下，面向所有用户：
- `Login.vue` - 登录页面
- `Register.vue` - 注册页面
- `Profile.vue` - 用户个人资料页面
- `Dashboard.vue` - 用户仪表盘
- `Rumors.vue` - 谣言列表页面
- `RumorDetail.vue` - 谣言详情页面
- `NotFound.vue` - 404页面

### 2. 管理员页面
位于 `/views/admin` 目录下，仅管理员可访问：
- `AdminLayout.vue` - 管理员布局组件
- `Dashboard.vue` - 管理员仪表盘（包含更多管理功能）
- `UserManagement.vue` - 用户管理
- `PermissionManagement.vue` - 权限管理
- `SystemSettings.vue` - 系统设置
- `AdminProfile.vue` - 管理员个人资料

### 3. 功能模块页面
按功能分类的专门页面：

#### 区块链相关
- `Blockchain.vue` - 区块链功能页面
- `SyncManagement.vue` - 同步管理

#### 分析相关
- `MultimodalAnalysis.vue` - 多模态分析
- `Detection.vue` - 检测功能
- `Trace.vue` - 追踪功能
- `LogVisualization.vue` - 日志可视化
- `Analytics.vue` - 分析功能

#### 日志相关
- `LogManagement.vue` - 日志管理
- `OperationLog.vue` - 操作日志
- `SystemLog.vue` - 系统日志
- `AuditLog.vue` - 审计日志

#### AI和传播分析
- `AIManagement.vue` - AI分析管理
- `Propagation.vue` - 传播分析

### 页面交互关系

1. **权限层级**
   - 普通用户只能访问基础页面
   - 管理员可以访问所有页面，包括管理功能

2. **功能关联**
   - 用户可以通过 `Rumors.vue` 查看谣言列表
   - 点击进入 `RumorDetail.vue` 查看详情
   - 在详情页可以进行 `Detection.vue` 检测和 `Trace.vue` 追踪
   - 管理员可以通过 `AIManagement.vue` 进行AI分析
   - 分析结果可以在 `Propagation.vue` 中查看传播情况

3. **数据流向**
   - 用户操作 -> 记录到 `OperationLog.vue`
   - 系统运行 -> 记录到 `SystemLog.vue`
   - 重要操作 -> 记录到 `AuditLog.vue`
   - 所有日志可以通过 `LogVisualization.vue` 可视化展示

4. **管理流程**
   - 管理员通过 `UserManagement.vue` 管理用户
   - 通过 `PermissionManagement.vue` 设置权限
   - 通过 `SystemSettings.vue` 配置系统
   - 通过 `AIManagement.vue` 进行AI分析
   - 通过 `Blockchain.vue` 和 `SyncManagement.vue` 管理区块链相关功能

## 开发环境设置

1. 安装依赖：
```bash
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 构建生产版本：
```bash
npm run build
```

## 环境配置

项目使用环境变量进行配置：
- `.env` - 基础环境变量
- `.env.development` - 开发环境特定变量
- `.env.production` - 生产环境特定变量

主要配置项：
- `VITE_API_BASE_URL` - API 基础 URL
- `VITE_APP_TITLE` - 应用标题
- `VITE_APP_DESCRIPTION` - 应用描述

## 测试

项目使用 Vitest 进行测试：
```bash
npm run test
```

## 部署

1. 构建生产版本：
```bash
npm run build
```

2. 部署构建后的文件到 Web 服务器

## 注意事项

1. 确保后端 API 服务已启动并正常运行
2. 检查环境变量配置是否正确
3. 确保有正确的权限访问 API 服务
4. 遵循项目的代码规范和提交规范 