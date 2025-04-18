import request from '@/utils/request'

export function getOperationLogs(params) {
  return request({
    url: '/api/logs/operation',
    method: 'get',
    params
  })
}

export function getSystemLogs(params) {
  return request({
    url: '/api/logs/system',
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

export function getLogStatistics(start, end) {
  return request({
    url: '/logs/visualization/statistics',
    method: 'get',
    params: { start, end }
  })
}

export function getLogTrends(start, end, interval) {
  return request({
    url: '/logs/visualization/trends',
    method: 'get',
    params: { start, end, interval }
  })
}

export function getErrorDetails(errorType, start, end) {
  return request({
    url: `/logs/visualization/errors/${errorType}`,
    method: 'get',
    params: { start, end }
  })
}

export function getServiceHealth(serviceName, start, end) {
  return request({
    url: `/logs/visualization/services/${serviceName}/health`,
    method: 'get',
    params: { start, end }
  })
} 