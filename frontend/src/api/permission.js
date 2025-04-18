import request from '@/utils/request'

// 获取权限列表
export function getPermissions() {
  return request({
    url: '/api/permission/permissions',
    method: 'get'
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/api/permission/permissions',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/api/permission/permissions/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/api/permission/permissions/${id}`,
    method: 'delete'
  })
}

// 获取角色列表
export function getRoles(params) {
  return request({
    url: '/api/permission/roles',
    method: 'get',
    params
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/api/permission/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/api/permission/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/api/permission/roles/${id}`,
    method: 'delete'
  })
}

// 分配权限
export function assignPermissions(data) {
  return request({
    url: '/api/permission/assign',
    method: 'post',
    data
  })
}

// 获取用户角色
export function getUserRoles(userId) {
  return request({
    url: `/api/permission/users/${userId}/roles`,
    method: 'get'
  })
}

// 更新用户角色
export function updateUserRoles(userId, data) {
  return request({
    url: `/api/permission/users/${userId}/roles`,
    method: 'put',
    data
  })
}

// 获取角色权限
export function getRolePermissions(roleId) {
  return request({
    url: `/api/permission/roles/${roleId}/permissions`,
    method: 'get'
  })
}

// 更新角色权限
export function updateRolePermissions(roleId, data) {
  return request({
    url: `/api/permission/roles/${roleId}/permissions`,
    method: 'put',
    data
  })
} 