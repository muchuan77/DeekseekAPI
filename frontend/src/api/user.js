import request from '@/utils/request'

export const userApi = {
  // 获取当前用户信息
  getCurrentUser() {
    return request({
      url: '/api/users/me',
      method: 'get'
    })
  },

  // 获取用户列表
  getUsers(params) {
    return request({
      url: '/api/users',
      method: 'get',
      params
    })
  },

  // 获取用户详情
  getUser(id) {
    return request({
      url: `/api/users/${id}`,
      method: 'get'
    })
  },

  // 创建用户
  createUser(data) {
    return request({
      url: '/api/users',
      method: 'post',
      data
    })
  },

  // 更新用户
  updateUser(id, data) {
    return request({
      url: `/api/users/${id}`,
      method: 'put',
      data
    })
  },

  // 删除用户
  deleteUser(id) {
    return request({
      url: `/api/users/${id}`,
      method: 'delete'
    })
  },

  // 更新用户状态
  updateUserStatus(id, status) {
    return request({
      url: `/api/users/${id}/status`,
      method: 'put',
      params: { status }
    })
  },

  // 更新用户角色
  updateUserRoles(id, roles) {
    return request({
      url: `/api/users/${id}/roles`,
      method: 'put',
      data: roles
    })
  },

  // 重置密码
  resetPassword(id) {
    return request({
      url: `/api/users/${id}/reset-password`,
      method: 'post'
    })
  },

  // 修改密码
  changePassword(id, data) {
    return request({
      url: `/api/users/${id}/change-password`,
      method: 'post',
      data
    })
  },

  // 批量更新状态
  batchUpdateStatus(ids, status) {
    return request({
      url: '/api/users/batch/status',
      method: 'post',
      data: { ids, status }
    })
  },

  // 批量删除
  batchDelete(ids) {
    return request({
      url: '/api/users/batch/delete',
      method: 'post',
      data: ids
    })
  },

  // 导入用户
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
  },

  // 导出用户
  exportUsers(params) {
    return request({
      url: '/api/users/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 锁定用户
  lockUser(id) {
    return request({
      url: `/api/users/${id}/lock`,
      method: 'post'
    })
  },

  // 解锁用户
  unlockUser(id) {
    return request({
      url: `/api/users/${id}/unlock`,
      method: 'post'
    })
  }
} 