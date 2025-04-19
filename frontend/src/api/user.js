import request from '@/utils/request'

export const userApi = {
  // 获取当前用户信息
  getUserInfo() {
    return request({
      url: '/api/users/me',
      method: 'get'
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 获取用户列表
  getUsers(params) {
    return request({
      url: '/api/users',
      method: 'get',
      params
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 获取单个用户
  getUser(id) {
    return request({
      url: `/api/users/${id}`,
      method: 'get'
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 创建用户
  createUser(data) {
    return request({
      url: '/api/users',
      method: 'post',
      data
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 更新用户
  updateUser(id, data) {
    return request({
      url: `/api/users/${id}`,
      method: 'put',
      data
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 删除用户
  deleteUser(id) {
    return request({
      url: `/api/users/${id}`,
      method: 'delete'
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 更新用户状态
  updateUserStatus(id, status) {
    return request({
      url: `/api/users/${id}/status`,
      method: 'put',
      data: { status }
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 重置密码
  resetPassword(id) {
    return request({
      url: `/api/users/${id}/reset-password`,
      method: 'post'
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 修改密码
  changePassword(id, data) {
    return request({
      url: `/api/users/${id}/change-password`,
      method: 'post',
      data
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 批量删除用户
  batchDelete(userIds) {
    return request({
      url: '/api/users/batch-delete',
      method: 'post',
      data: { userIds }
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 批量更新用户状态
  batchUpdateStatus(userIds, status) {
    return request({
      url: '/api/users/batch-update-status',
      method: 'post',
      data: { userIds, status }
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  },

  // 导出用户
  exportUsers() {
    return request({
      url: '/api/users/export',
      method: 'get',
      responseType: 'blob'
    }).then(response => {
      const url = window.URL.createObjectURL(new Blob([response]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `users_${new Date().getTime()}.xlsx`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
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
    }).then(response => {
      if (response.code === 200) {
        return response.data
      }
      throw new Error(response.message)
    })
  }
} 