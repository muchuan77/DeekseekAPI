# 谣言溯源系统 API 文档

## 概述

本文档详细描述了谣言溯源系统的 RESTful API 接口。所有 API 请求都需要在请求头中包含 `Content-Type: application/json`，并且大多数接口需要 JWT 认证。

## API结构

API层根据功能分为不同的模块：

### 认证模块 (`auth.js`)
处理用户认证和授权：

#### 登录
- 路径: `/api/auth/login`
- 方法: POST
- 描述: 用户登录，获取访问令牌
- 前端实现: `frontend/src/api/auth.js`
- 请求体:
```json
{
  "username": "string",
  "password": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "string",
    "refreshToken": "string",
    "user": {
      "id": "number",
      "username": "string",
      "role": "string",
      "status": "string"
    }
  }
}
```
- 状态码:
  - 200: 登录成功
  - 401: 用户名或密码错误
  - 500: 服务器内部错误

#### 注册
- 路径: `/api/auth/register`
- 方法: POST
- 描述: 新用户注册
- 前端实现: `frontend/src/api/auth.js`
- 请求体:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "fullName": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "string",
    "refreshToken": "string",
    "user": {
      "id": "number",
      "username": "string",
      "role": "string",
      "status": "string"
    }
  }
}
```
- 状态码:
  - 200: 注册成功
  - 400: 用户名已存在
  - 500: 服务器内部错误

#### 刷新令牌
- 路径: `/api/auth/refresh`
- 方法: POST
- 描述: 使用刷新令牌获取新的访问令牌
- 前端实现: `frontend/src/api/auth.js`
- 请求头: `Authorization: Bearer {refreshToken}`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "string",
    "refreshToken": "string"
  }
}
```
- 状态码:
  - 200: 刷新成功
  - 401: 刷新令牌无效
  - 500: 服务器内部错误

#### 登出
- 路径: `/api/auth/logout`
- 方法: POST
- 描述: 用户登出，使令牌失效
- 前端实现: `frontend/src/api/auth.js`
- 请求头: `Authorization: Bearer {token}`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 登出成功
  - 401: 未授权
  - 500: 服务器内部错误

### 用户管理 (`user.js`)
管理用户相关操作：

#### 获取用户列表
- 路径: `/api/users`
- 方法: GET
- 描述: 分页获取用户列表
- 前端实现: `frontend/src/api/user.js`
- 权限: 需要管理员或版主权限
- 查询参数:
  - keyword: 搜索关键词
  - status: 用户状态
  - page: 页码（默认0）
  - size: 每页大小（默认10）
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "number",
        "username": "string",
        "email": "string",
        "fullName": "string",
        "role": "string",
        "status": "string",
        "createdAt": "string"
      }
    ],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 403: 无权限
  - 500: 服务器内部错误

#### 获取用户详情
- 路径: `/api/users/{id}`
- 方法: GET
- 描述: 获取指定用户的详细信息
- 前端实现: `frontend/src/api/user.js`
- 权限: 需要用户权限（只能查看自己的信息，管理员和版主可以查看所有）
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "number",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "role": "string",
    "status": "string",
    "createdAt": "string"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 403: 无权限
  - 404: 用户不存在
  - 500: 服务器内部错误

#### 更新用户信息
- 路径: `/api/users/{id}`
- 方法: PUT
- 描述: 更新指定用户的信息
- 前端实现: `frontend/src/api/user.js`
- 权限: 需要用户权限（只能修改自己的信息，管理员可以修改所有）
- 请求体:
```json
{
  "email": "string",
  "fullName": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "number",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "role": "string",
    "status": "string"
  }
}
```
- 状态码:
  - 200: 更新成功
  - 403: 无权限
  - 404: 用户不存在
  - 500: 服务器内部错误

#### 删除用户
- 路径: `/api/users/{id}`
- 方法: DELETE
- 描述: 删除指定用户
- 前端实现: `frontend/src/api/user.js`
- 权限: 需要管理员权限
- 响应:
```json
{
  "code": 200,
  "message": "success"
}
```
- 状态码:
  - 200: 删除成功
  - 403: 无权限
  - 404: 用户不存在
  - 500: 服务器内部错误

#### 重置用户密码
- 路径: `/api/users/{id}/reset-password`
- 方法: POST
- 描述: 重置指定用户的密码
- 前端实现: `frontend/src/api/user.js`
- 权限: 需要管理员权限
- 响应:
```json
{
  "code": 200,
  "message": "success"
}
```
- 状态码:
  - 200: 重置成功
  - 403: 无权限
  - 404: 用户不存在
  - 500: 服务器内部错误

#### 修改密码
- 路径: `/api/users/{id}/change-password`
- 方法: POST
- 描述: 修改当前用户的密码
- 前端实现: `frontend/src/api/user.js`
- 权限: 需要用户权限（只能修改自己的密码）
- 请求体:
```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success"
}
```
- 状态码:
  - 200: 修改成功
  - 403: 无权限
  - 400: 旧密码错误
  - 500: 服务器内部错误

### 谣言管理 (`rumor.js`)
谣言相关操作：

#### 创建谣言记录
- 路径: `/api/rumors`
- 方法: POST
- 描述: 创建新的谣言记录
- 前端实现: `frontend/src/api/rumor.js`
- 请求体:
```json
{
  "title": "string",
  "content": "string",
  "source": "string",
  "type": "string",
  "tags": ["string"]
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "title": "string",
    "content": "string",
    "source": "string",
    "type": "string",
    "tags": ["string"],
    "status": "string",
    "createdAt": "string"
  }
}
```
- 状态码:
  - 200: 创建成功
  - 400: 请求参数错误
  - 500: 服务器内部错误

#### 获取谣言列表
- 路径: `/api/rumors`
- 方法: GET
- 描述: 获取谣言列表
- 前端实现: `frontend/src/api/rumor.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "title": "string",
      "content": "string",
      "source": "string",
      "type": "string",
      "tags": ["string"],
      "status": "string",
      "createdAt": "string"
    }
  ]
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 获取谣言详情
- 路径: `/api/rumors/{id}`
- 方法: GET
- 描述: 获取指定谣言的详细信息
- 前端实现: `frontend/src/api/rumor.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "title": "string",
    "content": "string",
    "source": "string",
    "type": "string",
    "tags": ["string"],
    "status": "string",
    "createdAt": "string"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 404: 谣言不存在
  - 500: 服务器内部错误

#### 更新谣言状态
- 路径: `/api/rumors/{id}/status`
- 方法: PUT
- 描述: 更新谣言状态
- 前端实现: `frontend/src/api/rumor.js`
- 请求参数:
  - status: 新状态
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "title": "string",
    "content": "string",
    "source": "string",
    "type": "string",
    "tags": ["string"],
    "status": "string",
    "createdAt": "string"
  }
}
```
- 状态码:
  - 200: 更新成功
  - 404: 谣言不存在
  - 500: 服务器内部错误

### 评论管理 (`comment.js`)
评论系统操作：

#### 创建评论
- 路径: `/api/comments`
- 方法: POST
- 描述: 创建新的评论
- 前端实现: `frontend/src/api/comment.js`
- 请求体:
```json
{
  "rumorId": "string",
  "content": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "rumorId": "string",
    "content": "string",
    "userId": "number",
    "username": "string",
    "createdAt": "string"
  }
}
```
- 状态码:
  - 200: 创建成功
  - 400: 请求参数错误
  - 500: 服务器内部错误

#### 获取评论列表
- 路径: `/api/comments`
- 方法: GET
- 描述: 获取评论列表
- 前端实现: `frontend/src/api/comment.js`
- 查询参数:
  - rumorId: 谣言ID
  - page: 页码（默认0）
  - size: 每页大小（默认10）
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "string",
        "rumorId": "string",
        "content": "string",
        "userId": "number",
        "username": "string",
        "createdAt": "string"
      }
    ],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 删除评论
- 路径: `/api/comments/{id}`
- 方法: DELETE
- 描述: 删除指定评论
- 前端实现: `frontend/src/api/comment.js`
- 响应:
```json
{
  "code": 200,
  "message": "success"
}
```
- 状态码:
  - 200: 删除成功
  - 404: 评论不存在
  - 500: 服务器内部错误

### 系统管理 (`system.js`)
系统和应用设置：

#### 获取系统配置
- 路径: `/api/system/config`
- 方法: GET
- 描述: 获取系统配置信息
- 前端实现: `frontend/src/api/system.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "key": "string",
    "value": "string"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 更新系统配置
- 路径: `/api/system/config`
- 方法: PUT
- 描述: 更新系统配置
- 前端实现: `frontend/src/api/system.js`
- 请求体:
```json
{
  "key": "string",
  "value": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 更新成功
  - 500: 服务器内部错误

#### 获取系统日志
- 路径: `/api/system/logs`
- 方法: GET
- 描述: 获取系统日志
- 前端实现: `frontend/src/api/system.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "string"
  ]
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 获取系统指标
- 路径: `/api/system/metrics`
- 方法: GET
- 描述: 获取系统指标
- 前端实现: `frontend/src/api/system.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "cpuUsage": "number",
    "memoryUsage": "number",
    "diskUsage": "number",
    "uptime": "string"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 获取系统状态
- 路径: `/api/system/status`
- 方法: GET
- 描述: 获取系统状态
- 前端实现: `frontend/src/api/system.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "string",
    "version": "string",
    "lastUpdate": "string"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

### 权限管理 (`permission.js`)
权限和角色管理：

#### 获取权限列表
- 路径: `/api/permission/permissions`
- 方法: GET
- 描述: 获取所有权限列表
- 前端实现: `frontend/src/api/permission.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "description": "string"
    }
  ]
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 创建权限
- 路径: `/api/permission/permissions`
- 方法: POST
- 描述: 创建新权限
- 前端实现: `frontend/src/api/permission.js`
- 请求体:
```json
{
  "name": "string",
  "description": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "name": "string",
    "description": "string"
  }
}
```
- 状态码:
  - 200: 创建成功
  - 500: 服务器内部错误

#### 更新权限
- 路径: `/api/permission/permissions/{id}`
- 方法: PUT
- 描述: 更新权限信息
- 前端实现: `frontend/src/api/permission.js`
- 请求体:
```json
{
  "name": "string",
  "description": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "name": "string",
    "description": "string"
  }
}
```
- 状态码:
  - 200: 更新成功
  - 404: 权限不存在
  - 500: 服务器内部错误

#### 删除权限
- 路径: `/api/permission/permissions/{id}`
- 方法: DELETE
- 描述: 删除指定权限
- 前端实现: `frontend/src/api/permission.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 删除成功
  - 404: 权限不存在
  - 500: 服务器内部错误

#### 获取角色列表
- 路径: `/api/permission/roles`
- 方法: GET
- 描述: 获取所有角色列表
- 前端实现: `frontend/src/api/permission.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "description": "string"
    }
  ]
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 创建角色
- 路径: `/api/permission/roles`
- 方法: POST
- 描述: 创建新角色
- 前端实现: `frontend/src/api/permission.js`
- 请求体:
```json
{
  "name": "string",
  "description": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "name": "string",
    "description": "string"
  }
}
```
- 状态码:
  - 200: 创建成功
  - 500: 服务器内部错误

#### 更新角色
- 路径: `/api/permission/roles/{id}`
- 方法: PUT
- 描述: 更新角色信息
- 前端实现: `frontend/src/api/permission.js`
- 请求体:
```json
{
  "name": "string",
  "description": "string"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "name": "string",
    "description": "string"
  }
}
```
- 状态码:
  - 200: 更新成功
  - 404: 角色不存在
  - 500: 服务器内部错误

#### 删除角色
- 路径: `/api/permission/roles/{id}`
- 方法: DELETE
- 描述: 删除指定角色
- 前端实现: `frontend/src/api/permission.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 删除成功
  - 404: 角色不存在
  - 500: 服务器内部错误

#### 分配权限
- 路径: `/api/permission/assign`
- 方法: POST
- 描述: 为角色分配权限
- 前端实现: `frontend/src/api/permission.js`
- 请求参数:
  - roleId: 角色ID
  - permissionId: 权限ID
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 分配成功
  - 404: 角色或权限不存在
  - 500: 服务器内部错误

#### 获取用户角色
- 路径: `/api/permission/users/{userId}/roles`
- 方法: GET
- 描述: 获取指定用户的所有角色
- 前端实现: `frontend/src/api/permission.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "description": "string"
    }
  ]
}
```
- 状态码:
  - 200: 获取成功
  - 404: 用户不存在
  - 500: 服务器内部错误

#### 更新用户角色
- 路径: `/api/permission/users/{userId}/roles`
- 方法: PUT
- 描述: 更新指定用户的角色
- 前端实现: `frontend/src/api/permission.js`
- 请求体:
```json
[
  "string"
]
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 更新成功
  - 404: 用户不存在
  - 500: 服务器内部错误

#### 获取角色权限
- 路径: `/api/permission/roles/{roleId}/permissions`
- 方法: GET
- 描述: 获取指定角色的所有权限
- 前端实现: `frontend/src/api/permission.js`
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "description": "string"
    }
  ]
}
```
- 状态码:
  - 200: 获取成功
  - 404: 角色不存在
  - 500: 服务器内部错误

#### 更新角色权限
- 路径: `/api/permission/roles/{roleId}/permissions`
- 方法: PUT
- 描述: 更新指定角色的权限
- 前端实现: `frontend/src/api/permission.js`
- 请求体:
```json
[
  "string"
]
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```
- 状态码:
  - 200: 更新成功
  - 404: 角色不存在
  - 500: 服务器内部错误

### 日志管理 (`log.js`)
日志相关操作：

#### 获取系统日志
- 路径: `/api/logs/system`
- 方法: GET
- 描述: 分页获取系统日志信息
- 前端实现: `frontend/src/api/log.js`
- 查询参数:
  - level: 日志级别
  - page: 页码（默认0）
  - size: 每页大小（默认10）
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "string",
        "level": "string",
        "message": "string",
        "createdAt": "string"
      }
    ],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 获取操作日志
- 路径: `/api/logs/operation`
- 方法: GET
- 描述: 分页获取操作日志信息
- 前端实现: `frontend/src/api/log.js`
- 查询参数:
  - operationType: 操作类型
  - username: 用户名
  - page: 页码（默认0）
  - size: 每页大小（默认10）
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "string",
        "operationType": "string",
        "username": "string",
        "message": "string",
        "createdAt": "string"
      }
    ],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

#### 获取审计日志
- 路径: `/api/logs/audit`
- 方法: GET
- 描述: 分页获取审计日志信息
- 前端实现: `frontend/src/api/log.js`
- 查询参数:
  - auditType: 审计类型
  - username: 用户名
  - page: 页码（默认0）
  - size: 每页大小（默认10）
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "string",
        "auditType": "string",
        "username": "string",
        "message": "string",
        "createdAt": "string"
      }
    ],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 500: 服务器内部错误

### 数据分析 (`analytics.js`)
数据分析相关操作：

#### 获取统计数据
- 路径: `/api/analytics/statistics`
- 方法: GET
- 描述: 获取谣言的统计信息
- 前端实现: `frontend/src/api/analytics.js`
- 权限: 需要管理员权限
- 查询参数:
  - timeRange: 时间范围
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalRumors": "number",
    "verifiedRumors": "number",
    "fakeRumors": "number",
    "pendingRumors": "number",
    "totalComments": "number",
    "activeUsers": "number"
  }
}
```
- 状态码:
  - 200: 获取成功
  - 403: 无权限
  - 500: 服务器内部错误

#### 获取趋势数据
- 路径: `/api/analytics/trends`
- 方法: GET
- 描述: 获取谣言的趋势信息
- 前端实现: `frontend/src/api/analytics.js`
- 权限: 需要管理员权限
- 查询参数:
  - timeRange: 时间范围
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dailyStats": [
      {
        "date": "string",
        "rumors": "number",
        "comments": "number",
        "users": "number"
      }
    ],
    "typeDistribution": {
      "TEXT": "number",
      "IMAGE": "number",
      "VIDEO": "number"
    },
    "tagDistribution": {
      "tag1": "number",
      "tag2": "number"
    }
  }
}
```
- 状态码:
  - 200: 获取成功
  - 403: 无权限
  - 500: 服务器内部错误

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 204 | 成功，无返回内容 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有 API 请求都需要在请求头中包含 `Content-Type: application/json`
2. 需要认证的接口需要在请求头中包含 `Authorization: Bearer {token}`
3. 分页查询默认从第 0 页开始，每页 10 条记录
4. 日期格式统一使用 ISO 8601 标准：`yyyy-MM-ddTHH:mm:ssZ`
5. 文件上传使用 multipart/form-data 格式
6. 所有响应都包含 code、message 和 data 字段
7. 错误响应会包含详细的错误信息 