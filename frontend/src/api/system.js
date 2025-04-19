import request from '@/utils/request'

// 获取系统配置
export function getSystemConfig() {
  return request({
    url: '/api/system/config',
    method: 'get'
  })
}

// 更新系统配置
export function updateSystemConfig(data) {
  return request({
    url: '/api/system/config',
    method: 'put',
    data
  })
}

// 获取系统日志
export function getSystemLogs(params) {
  return request({
    url: '/api/system/logs',
    method: 'get',
    params
  })
}

// 获取系统指标
export function getSystemMetrics() {
  return request({
    url: '/api/system/metrics',
    method: 'get'
  })
}

// 获取系统状态
export function getSystemStatus() {
  return request({
    url: '/api/system/status',
    method: 'get'
  })
}

// 获取系统资源使用情况
export function getSystemResources() {
  return request({
    url: '/api/system/resources',
    method: 'get'
  })
}

// 获取系统性能指标
export function getSystemPerformance() {
  return request({
    url: '/api/system/performance',
    method: 'get'
  })
}

// 获取系统告警信息
export function getSystemAlerts(params) {
  return request({
    url: '/api/system/alerts',
    method: 'get',
    params
  })
}

// 更新系统告警状态
export function updateAlertStatus(id, data) {
  return request({
    url: `/api/system/alerts/${id}/status`,
    method: 'put',
    data
  })
} 