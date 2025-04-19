import request from '@/utils/request'

// 获取系统设置
export function getSettings() {
  return request({
    url: '/api/settings',
    method: 'get'
  })
}

// 更新系统设置
export function updateSettings(data) {
  return request({
    url: '/api/settings',
    method: 'put',
    data
  })
}

// 获取系统状态
export function getSystemStatus() {
  return request({
    url: '/api/settings/status',
    method: 'get'
  })
}

// 重置系统设置
export function resetSettings() {
  return request({
    url: '/api/settings/reset',
    method: 'post'
  })
} 