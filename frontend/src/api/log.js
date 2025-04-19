import request from '@/utils/request'

export function getSystemLogs(params) {
  return request({
    url: '/api/logs/system',
    method: 'get',
    params
  })
}

export function getOperationLogs(params) {
  return request({
    url: '/api/logs/operation',
    method: 'get',
    params
  })
}

export function getAuditLogs(params) {
  return request({
    url: '/api/logs/audit',
    method: 'get',
    params
  })
}

export function getLogStatistics(params) {
  return request({
    url: '/api/logs/statistics',
    method: 'get',
    params
  })
}

export function getLogTrends(params) {
  return request({
    url: '/api/logs/trends',
    method: 'get',
    params
  })
}

export function getErrorDetails(type, params) {
  return request({
    url: `/api/logs/errors/${type}`,
    method: 'get',
    params
  })
}

export function getServiceHealth(name, params) {
  return request({
    url: `/api/logs/services/${name}/health`,
    method: 'get',
    params
  })
} 