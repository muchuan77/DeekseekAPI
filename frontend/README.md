# 谣言溯源系统前端

基于 Vue.js 3 构建的谣言溯源系统前端应用。

## 项目概述

前端使用 Vue.js 3 框架构建，具有以下主要特性：
- Vue 3.3.11 + Composition API
- Vue Router 4.2.5 用于导航
- Pinia 2.1.7 用于状态管理
- Axios 1.6.2 用于 API 请求
- Element Plus 2.4.4 用于 UI 组件
- Vite 6.3.0 用于构建工具
- ECharts 5.6.0 用于数据可视化
- SASS 1.69.5 用于样式管理

## 目录结构

```
frontend/
├── src/                    # 源代码文件
│   ├── api/               # API 服务模块
│   │   ├── auth.js        # 认证相关 API
│   │   ├── user.js        # 用户管理 API
│   │   ├── rumor.js       # 谣言管理 API
│   │   ├── comment.js     # 评论管理 API
│   │   ├── settings.js    # 系统设置 API
│   │   ├── system.js      # 系统管理 API
│   │   ├── permission.js  # 权限管理 API
│   │   ├── log.js         # 日志管理 API
│   │   ├── propagation.js # 传播分析 API
│   │   └── ai.js          # AI分析 API
│   ├── assets/            # 静态资源（图片、样式等）
│   ├── layouts/           # 布局组件
│   ├── router/            # Vue Router 配置
│   ├── stores/            # Pinia 状态管理
│   │   ├── app.js         # 应用状态
│   │   ├── auth.js        # 认证状态
│   │   ├── user.js        # 用户状态
│   │   ├── rumor.js       # 谣言状态
│   │   ├── comment.js     # 评论状态
│   │   ├── permission.js  # 权限状态
│   │   ├── system.js      # 系统状态
│   │   ├── settings.js    # 设置状态
│   │   ├── log.js         # 日志状态
│   │   ├── propagation.js # 传播分析状态
│   │   └── ai.js          # AI分析状态
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   │   ├── admin/         # 管理后台页面
│   │   │   ├── AdminLayout.vue
│   │   │   ├── Dashboard.vue
│   │   │   ├── UserManagement.vue
│   │   │   ├── PermissionManagement.vue
│   │   │   ├── SystemSettings.vue
│   │   │   ├── Analytics.vue
│   │   │   ├── LogManagement.vue
│   │   │   ├── AIManagement.vue
│   │   │   ├── CommentManagement.vue
│   │   │   ├── SystemLog.vue
│   │   │   ├── OperationLog.vue
│   │   │   ├── AuditLog.vue
│   │   │   └── Profile.vue
│   │   ├── error/         # 错误页面
│   │   ├── Login.vue      # 登录页面
│   │   ├── Register.vue   # 注册页面
│   │   ├── Profile.vue    # 用户资料页面
│   │   ├── Dashboard.vue  # 用户仪表盘
│   │   ├── Rumors.vue     # 谣言列表页面
│   │   ├── RumorDetail.vue # 谣言详情页面
│   │   ├── Propagation.vue # 传播分析页面
│   │   ├── Trace.vue      # 追踪页面
│   │   ├── AI.vue         # AI分析页面
│   │   ├── LogVisualization.vue # 日志可视化页面
│   │   ├── SyncManagement.vue   # 同步管理页面
│   │   ├── FlowManager.vue      # 流程管理页面
│   │   └── AdminProfile.vue     # 管理员资料页面
│   ├── App.vue            # 根组件
│   └── main.js            # 应用入口文件
├── public/                # 公共静态文件
├── .env                   # 环境变量
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── vite.config.js         # Vite 配置
├── package.json           # 项目依赖
├── .eslintrc.js          # ESLint 配置
├── jest.config.js         # Jest 测试配置
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

### 区块链存证
- 数据上链管理
- 存证查询
- 区块链状态监控
- 智能合约交互

## 页面关系说明

### 1. 基础页面
位于 `/views` 目录下，面向所有用户：
- `Login.vue` - 登录页面
- `Register.vue` - 注册页面
- `Profile.vue` - 用户个人资料页面
- `Dashboard.vue` - 用户仪表盘
- `Rumors.vue` - 谣言列表页面
- `RumorDetail.vue` - 谣言详情页面
- `Propagation.vue` - 传播分析页面
- `Trace.vue` - 追踪页面
- `AI.vue` - AI分析页面
- `LogVisualization.vue` - 日志可视化页面
- `SyncManagement.vue` - 同步管理页面

### 2. 管理员页面
位于 `/views/admin` 目录下，仅管理员可访问：
- `AdminLayout.vue` - 管理员布局组件
- `Dashboard.vue` - 管理员仪表盘
- `UserManagement.vue` - 用户管理
- `PermissionManagement.vue` - 权限管理
- `SystemSettings.vue` - 系统设置
- `Analytics.vue` - 分析统计
- `LogManagement.vue` - 日志管理
- `AIManagement.vue` - AI分析管理
- `CommentManagement.vue` - 评论管理
- `SystemLog.vue` - 系统日志
- `OperationLog.vue` - 操作日志
- `AuditLog.vue` - 审计日志
- `Profile.vue` - 管理员个人资料

### 页面交互关系

1. **权限层级**
   - 普通用户只能访问基础页面
   - 管理员可以访问所有页面，包括管理功能

2. **功能关联**
   - 用户可以通过 `Rumors.vue` 查看谣言列表
   - 点击进入 `RumorDetail.vue` 查看详情
   - 在详情页可以进行 `Trace.vue` 追踪
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
   - 通过 `LogManagement.vue` 监控系统运行状态

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

4. 预览生产版本：
```bash
npm run preview
```

5. 代码检查：
```bash
npm run lint
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
- `VITE_APP_PORT` - 开发服务器端口
- `VITE_APP_PROXY_TARGET` - API 代理目标

## 构建配置

项目使用 Vite 6.3.0 进行构建，主要配置：
- 开发服务器端口：3000
- API 代理：/api -> http://localhost:8080
- 输出目录：dist
- 资源目录：assets
- 源码映射：启用

## 部署

1. 构建生产版本：
```bash
npm run build
```

2. 部署构建后的文件到 Web 服务器：
- 将 `dist` 目录下的文件复制到 Web 服务器
- 配置 Web 服务器路由，确保所有请求都指向 `index.html`
- 配置 API 代理，确保 API 请求正确转发

## 测试

项目使用 Jest 进行单元测试：
```bash
npm test
```

## 注意事项

1. 确保后端 API 服务已启动并正常运行
2. 检查环境变量配置是否正确
3. 确保有正确的权限访问 API 服务
4. 遵循项目的代码规范和提交规范
5. 开发时注意跨域问题，已配置代理解决
6. 生产环境部署时注意配置正确的 API 地址
7. 使用 ESLint 进行代码规范检查
8. 使用 SASS 进行样式管理，确保样式模块化