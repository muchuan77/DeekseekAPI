# 前端 API 文档

## 概述

本文档详细描述了谣言溯源系统前端的 API 接口调用方式。所有 API 请求都通过 `@/utils/request` 进行封装，并统一处理错误。

## API 结构

API 层根据功能分为不同的模块：

### 认证模块 (`auth.js`)
处理用户认证和授权：

#### 登录
```javascript
login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: {
      username: string,
      password: string
    }
  })
}
```
- 响应数据：
```typescript
{
  token: string;
  refreshToken: string;
  user: {
    id: number;
    username: string;
    role: string;
    status: string;
  }
}
```

#### 注册
```javascript
register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data: {
      username: string;
      password: string;
      email: string;
      fullName: string;
    }
  })
}
```
- 响应数据：同登录接口

#### 登出
```javascript
logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}
```

#### 刷新令牌
```javascript
refreshToken(refreshToken) {
  return request({
    url: '/api/auth/refresh',
    method: 'post',
    data: { refreshToken }
  })
}
```
- 响应数据：
```typescript
{
  token: string;
  refreshToken: string;
}
```

### 用户管理模块 (`user.js`)
用户相关操作：

#### 获取当前用户信息
```javascript
getCurrentUser() {
  return request({
    url: '/api/users/me',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  id: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
  status: string;
  createdAt: string;
  lastLogin: string;
  loginAttempts: number;
  accountLockedUntil: string;
}
```

#### 获取用户列表
```javascript
getUsers(params) {
  return request({
    url: '/api/users',
    method: 'get',
    params: {
      keyword?: string;
      status?: string;
      page: number;
      size: number;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: number;
    username: string;
    email: string;
    fullName: string;
    roles: string[];
    status: string;
    createdAt: string;
    lastLogin: string;
  }>;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
```

#### 获取用户详情
```javascript
getUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}
```
- 响应数据：同当前用户信息

#### 创建用户
```javascript
createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data: {
      username: string;
      password: string;
      email: string;
      fullName: string;
      roles: string[];
    }
  })
}
```

#### 更新用户
```javascript
updateUser(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data: {
      email: string;
      fullName: string;
      roles: string[];
    }
  })
}
```

#### 删除用户
```javascript
deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}
```

#### 更新用户状态
```javascript
updateUserStatus(id, status) {
  return request({
    url: `/api/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}
```

#### 更新用户角色
```javascript
updateUserRoles(id, roles) {
  return request({
    url: `/api/users/${id}/roles`,
    method: 'put',
    data: roles
  })
}
```

#### 重置密码
```javascript
resetPassword(id) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'post'
  })
}
```

#### 修改密码
```javascript
changePassword(id, data) {
  return request({
    url: `/api/users/${id}/change-password`,
    method: 'post',
    data: {
      oldPassword: string;
      newPassword: string;
    }
  })
}
```

#### 批量更新状态
```javascript
batchUpdateStatus(ids, status) {
  return request({
    url: '/api/users/batch/status',
    method: 'post',
    data: { ids, status }
  })
}
```

#### 批量删除
```javascript
batchDelete(ids) {
  return request({
    url: '/api/users/batch/delete',
    method: 'post',
    data: ids
  })
}
```

#### 导入用户
```javascript
importUsers(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/users/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
```

#### 导出用户
```javascript
exportUsers(params) {
  return request({
    url: '/api/users/export',
    method: 'get',
    params: {
      keyword?: string;
      status?: string;
    },
    responseType: 'blob'
  })
}
```

#### 锁定用户
```javascript
lockUser(id) {
  return request({
    url: `/api/users/${id}/lock`,
    method: 'post'
  })
}
```

#### 解锁用户
```javascript
unlockUser(id) {
  return request({
    url: `/api/users/${id}/unlock`,
    method: 'post'
  })
}
```

### AI 分析模块 (`ai.js`)
AI 分析相关操作：

#### 文本分析
```javascript
analyzeText(data) {
  return request({
    url: '/api/deepseek/analyze/text',
    method: 'post',
    timeout: 300000,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: {
      content: string,
      type: 'TEXT'
    }
  })
}
```
- 响应数据：
```typescript
{
  sentiment: string;
  keywords: string[];
  entities: string[];
  confidence: number;
}
```

#### 图像分析
```javascript
analyzeImage(data) {
  return request({
    url: '/api/deepseek/analyze/image',
    method: 'post',
    timeout: 300000,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: {
      content: string,
      type: 'IMAGE'
    }
  })
}
```
- 响应数据：
```typescript
{
  objects: string[];
  text: string;
  confidence: number;
}
```

#### 视频分析
```javascript
analyzeVideo(data) {
  return request({
    url: '/api/deepseek/analyze/video',
    method: 'post',
    timeout: 300000,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: {
      content: string,
      type: 'VIDEO'
    }
  })
}
```
- 响应数据：
```typescript
{
  scenes: string[];
  objects: string[];
  confidence: number;
}
```

#### 多模态分析
```javascript
analyzeMultiModal(data) {
  return request({
    url: '/api/deepseek/analyze/multimodal',
    method: 'post',
    timeout: 300000,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: {
      content: string,
      type: 'MULTIMODAL'
    }
  })
}
```
- 响应数据：
```typescript
{
  analysis: string;
  confidence: number;
  details: object;
}
```

### 传播分析模块 (`propagation.js`)
传播分析相关操作：

#### 获取传播路径
```javascript
getPropagationPaths(rumorId, params) {
  return request({
    url: `/api/propagation/paths/${rumorId}`,
    method: 'get',
    params: {
      startDate: string;
      endDate: string;
      page: number;
      size: number;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: number;
    rumorId: number;
    path: string[];
    pathLength: number;
    createdAt: string;
  }>;
  totalElements: number;
  totalPages: number;
}
```

#### 获取影响力分析
```javascript
getInfluenceAnalysis(rumorId) {
  return request({
    url: `/api/propagation/influence/${rumorId}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  analysis: {
    influenceScore: number;
    reach: number;
    engagement: number;
    virality: number;
  }
}
```

#### 获取传播趋势
```javascript
getPropagationTrends(rumorId) {
  return request({
    url: `/api/propagation/trends/${rumorId}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  data: Array<{
    timestamp: string;
    count: number;
    type: string;
  }>;
}
```

#### 获取关键节点
```javascript
getKeyNodes(rumorId) {
  return request({
    url: `/api/propagation/key-nodes/${rumorId}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  keyNodes: Array<{
    nodeId: string;
    influence: number;
    pathCount: number;
  }>;
}
```

#### 获取传播网络
```javascript
getPropagationNetwork(rumorId) {
  return request({
    url: `/api/propagation/network/${rumorId}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  nodes: string[];
  edges: string[];
  statistics: object;
}
```

#### 获取传播统计
```javascript
getPropagationStatistics(rumorId) {
  return request({
    url: `/api/propagation/statistics/${rumorId}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  totalPaths: number;
  averagePathLength: number;
  maxPathLength: number;
}
```

### 统计分析模块 (`analytics.js`)
统计分析相关操作：

#### 获取统计数据
```javascript
getStatistics(params) {
  return request({
    url: '/api/analytics/statistics',
    method: 'get',
    params
  })
}
```
- 响应数据：
```typescript
{
  totalRumors: number;
  totalUsers: number;
  totalComments: number;
  activeUsers: number;
  analysisCount: number;
  propagationCount: number;
}
```

#### 获取趋势数据
```javascript
getTrends(params) {
  return request({
    url: '/api/analytics/trends',
    method: 'get',
    params
  })
}
```
- 响应数据：
```typescript
{
  data: Array<{
    date: string;
    count: number;
    type: string;
  }>;
}
```

#### 获取传播分析
```javascript
getPropagationAnalysis(rumorId) {
  return request({
    url: `/analytics/propagation/${rumorId}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  paths: Array<{
    id: number;
    source: string;
    target: string;
    timestamp: string;
  }>;
  statistics: {
    totalPaths: number;
    averagePathLength: number;
    maxPathLength: number;
  };
}
```

### 评论模块 (`comment.js`)
评论相关操作：

#### 创建评论
```javascript
createComment(data) {
  return request({
    url: '/api/comments',
    method: 'post',
    data: {
      rumorId: string;
      content: string;
    }
  })
}
```
- 响应数据：
```typescript
{
  id: string;
  rumorId: string;
  content: string;
  userId: number;
  username: string;
  createdAt: string;
}
```

#### 获取评论列表
```javascript
getComments(params) {
  return request({
    url: '/api/comments',
    method: 'get',
    params: {
      rumorId: string;
      page: number;
      size: number;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: string;
    rumorId: string;
    content: string;
    userId: number;
    username: string;
    createdAt: string;
  }>;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
```

#### 删除评论
```javascript
deleteComment(id) {
  return request({
    url: `/api/comments/${id}`,
    method: 'delete'
  })
}
```

### 日志模块 (`log.js`)
日志相关操作：

#### 获取系统日志
```javascript
getSystemLogs(params) {
  return request({
    url: '/api/logs/system',
    method: 'get',
    params: {
      level?: string;
      startTime?: string;
      endTime?: string;
      page: number;
      size: number;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: string;
    level: string;
    message: string;
    source: string;
    applicationName: string;
    stackTrace: string;
    responseTime: number;
    logTime: string;
  }>;
  totalElements: number;
  totalPages: number;
}
```

#### 获取操作日志
```javascript
getOperationLogs(params) {
  return request({
    url: '/api/logs/operation',
    method: 'get',
    params: {
      username?: string;
      operationType?: string;
      startTime?: string;
      endTime?: string;
      page: number;
      size: number;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: string;
    userId: number;
    username: string;
    operationType: string;
    targetType: string;
    targetId: number;
    details: string;
    executionTime: string;
  }>;
  totalElements: number;
  totalPages: number;
}
```

#### 获取审计日志
```javascript
getAuditLogs(params) {
  return request({
    url: '/api/logs/audit',
    method: 'get',
    params: {
      username?: string;
      targetType?: string;
      startTime?: string;
      endTime?: string;
      page: number;
      size: number;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: string;
    userId: number;
    username: string;
    action: string;
    targetType: string;
    targetId: number;
    details: string;
    auditTime: string;
  }>;
  totalElements: number;
  totalPages: number;
}
```

#### 获取日志统计
```javascript
getLogStatistics(params) {
  return request({
    url: '/api/logs/statistics',
    method: 'get',
    params: {
      startTime?: string;
      endTime?: string;
    }
  })
}
```
- 响应数据：
```typescript
{
  totalLogs: number;
  errorLogs: number;
  warningLogs: number;
  infoLogs: number;
  operationTypeCounts: Record<string, number>;
  userActivityCounts: Record<string, number>;
}
```

#### 获取日志趋势
```javascript
getLogTrends(params) {
  return request({
    url: '/api/logs/trends',
    method: 'get',
    params: {
      startTime?: string;
      endTime?: string;
      interval: string;
    }
  })
}
```
- 响应数据：
```typescript
{
  data: Array<{
    timestamp: string;
    count: number;
    type: string;
  }>;
}
```

### 权限模块 (`permission.js`)
权限相关操作：

#### 获取权限列表
```javascript
getPermissions() {
  return request({
    url: '/api/permission/permissions',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
Array<{
  id: string;
  name: string;
  description: string;
}>
```

#### 创建权限
```javascript
createPermission(data) {
  return request({
    url: '/api/permission/permissions',
    method: 'post',
    data: {
      name: string;
      description: string;
    }
  })
}
```

#### 更新权限
```javascript
updatePermission(id, data) {
  return request({
    url: `/api/permission/permissions/${id}`,
    method: 'put',
    data: {
      name: string;
      description: string;
    }
  })
}
```

#### 删除权限
```javascript
deletePermission(id) {
  return request({
    url: `/api/permission/permissions/${id}`,
    method: 'delete'
  })
}
```

#### 获取角色列表
```javascript
getRoles(params) {
  return request({
    url: '/api/permission/roles',
    method: 'get',
    params
  })
}
```
- 响应数据：
```typescript
Array<{
  id: string;
  name: string;
  description: string;
}>
```

#### 创建角色
```javascript
createRole(data) {
  return request({
    url: '/api/permission/roles',
    method: 'post',
    data: {
      name: string;
      description: string;
    }
  })
}
```

#### 更新角色
```javascript
updateRole(id, data) {
  return request({
    url: `/api/permission/roles/${id}`,
    method: 'put',
    data: {
      name: string;
      description: string;
    }
  })
}
```

#### 删除角色
```javascript
deleteRole(id) {
  return request({
    url: `/api/permission/roles/${id}`,
    method: 'delete'
  })
}
```

#### 分配权限
```javascript
assignPermissions(data) {
  return request({
    url: '/api/permission/assign',
    method: 'post',
    data: {
      roleId: string;
      permissionId: string;
    }
  })
}
```

#### 获取用户角色
```javascript
getUserRoles(userId) {
  return request({
    url: `/api/permission/users/${userId}/roles`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
Array<{
  id: string;
  name: string;
  description: string;
}>
```

#### 更新用户角色
```javascript
updateUserRoles(userId, data) {
  return request({
    url: `/api/permission/users/${userId}/roles`,
    method: 'put',
    data: string[]
  })
}
```

#### 获取角色权限
```javascript
getRolePermissions(roleId) {
  return request({
    url: `/api/permission/roles/${roleId}/permissions`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
Array<{
  id: string;
  name: string;
  description: string;
}>
```

#### 更新角色权限
```javascript
updateRolePermissions(roleId, data) {
  return request({
    url: `/api/permission/roles/${roleId}/permissions`,
    method: 'put',
    data: string[]
  })
}
```

### 谣言模块 (`rumor.js`)
谣言相关操作：

#### 获取谣言列表
```javascript
getRumors(params) {
  return request({
    url: '/api/rumors/search',
    method: 'get',
    params: {
      page: number;
      size: number;
      keyword?: string;
      status?: string;
    }
  })
}
```
- 响应数据：
```typescript
{
  content: Array<{
    id: string;
    title: string;
    content: string;
    source: string;
    status: string;
    createdAt: string;
  }>;
  totalElements: number;
  totalPages: number;
}
```

#### 获取谣言详情
```javascript
getRumor(id) {
  return request({
    url: `/api/rumors/${id}`,
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  id: string;
  title: string;
  content: string;
  source: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}
```

#### 创建谣言
```javascript
createRumor(data) {
  return request({
    url: '/api/rumors',
    method: 'post',
    data: {
      title: string;
      content: string;
      source: string;
      status: string;
    }
  })
}
```

#### 更新谣言
```javascript
updateRumor(id, data) {
  return request({
    url: `/api/rumors/${id}`,
    method: 'put',
    data: {
      title: string;
      content: string;
      source: string;
    }
  })
}
```

#### 更新谣言状态
```javascript
updateRumorStatus(id, status) {
  return request({
    url: `/api/rumors/${id}/status`,
    method: 'put',
    params: { status }
  })
}
```

#### 删除谣言
```javascript
deleteRumor(id) {
  return request({
    url: `/api/rumors/${id}`,
    method: 'delete'
  })
}
```

### 系统设置模块 (`settings.js`)
系统设置相关操作：

#### 获取系统设置
```javascript
getSettings() {
  return request({
    url: '/api/settings',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  key: string;
  value: string;
}
```

#### 更新系统设置
```javascript
updateSettings(data) {
  return request({
    url: '/api/settings',
    method: 'put',
    data: {
      key: string;
      value: string;
    }
  })
}
```

#### 获取系统状态
```javascript
getSystemStatus() {
  return request({
    url: '/api/settings/status',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  status: string;
  version: string;
  lastUpdate: string;
}
```

#### 重置系统设置
```javascript
resetSettings() {
  return request({
    url: '/api/settings/reset',
    method: 'post'
  })
}
```

### 系统管理模块 (`system.js`)
系统管理相关操作：

#### 获取系统配置
```javascript
getSystemConfig() {
  return request({
    url: '/api/system/config',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  key: string;
  value: string;
}
```

#### 更新系统配置
```javascript
updateSystemConfig(data) {
  return request({
    url: '/api/system/config',
    method: 'put',
    data: {
      key: string;
      value: string;
    }
  })
}
```

#### 获取系统日志
```javascript
getSystemLogs(params) {
  return request({
    url: '/api/system/logs',
    method: 'get',
    params
  })
}
```
- 响应数据：
```typescript
{
  content: string[];
}
```

#### 获取系统指标
```javascript
getSystemMetrics() {
  return request({
    url: '/api/system/metrics',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
  uptime: string;
}
```

#### 获取系统状态
```javascript
getSystemStatus() {
  return request({
    url: '/api/system/status',
    method: 'get'
  })
}
```
- 响应数据：
```typescript
{
  status: string;
  version: string;
  lastUpdate: string;
}
```

## 错误处理

所有 API 调用都包含统一的错误处理机制：

```javascript
const handleApiError = (error, serviceType) => {
  console.error(`${serviceType}失败:`, error)
  
  let errorMessage = '分析请求失败'
  
  // 处理超时错误
  if (error.code === 'ECONNABORTED') {
    errorMessage = '请求超时，请稍后再试'
  } 
  // 处理网络错误
  else if (error.message === 'Network Error') {
    errorMessage = '网络连接失败，请检查网络设置'
  }
  // 处理服务器错误
  else if (error.response) {
    const status = error.response.status
    
    if (status === 400) {
      errorMessage = '请求参数错误，请检查输入内容'
    } else if (status === 401 || status === 403) {
      errorMessage = '授权失败，请重新登录'
    } else if (status === 500) {
      errorMessage = '服务器内部错误'
    } else {
      errorMessage = `服务器返回错误: ${status}`
    }
    
    // 尝试从响应中获取更具体的错误信息
    if (error.response.data && error.response.data.message) {
      errorMessage = error.response.data.message
    }
  }
  
  // 显示错误消息
  ElMessage.error(errorMessage)
  
  // 抛出更友好的错误对象
  throw {
    ...error,
    friendlyMessage: errorMessage
  }
}
```

## 注意事项

1. 所有 API 请求都通过 `@/utils/request` 进行封装
2. 请求超时时间统一设置为 5 分钟（300000ms）
3. 文件上传使用 `multipart/form-data` 格式
4. 分页查询时，前端页码从 1 开始，后端从 0 开始，需要做转换
5. 所有错误都会通过 `ElMessage` 组件显示给用户
6. 错误处理会优先使用服务器返回的错误信息
7. 网络错误和超时错误有特殊的处理逻辑
8. 所有响应数据都包含 code、message 和 data 字段
9. 认证相关的接口会在响应头中返回 token
10. 文件下载接口需要设置 responseType: 'blob'