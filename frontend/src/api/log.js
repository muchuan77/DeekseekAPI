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
    params,
    transformResponse: [(data) => {
      try {
        // 尝试解析响应数据
        const parsedData = JSON.parse(data)
        console.log('解析后的数据:', parsedData)
        
        // 如果后端直接返回Page对象，包装成标准格式
        if (parsedData && !parsedData.code) {
          return {
            code: 200,
            data: {
              content: parsedData.content || [],
              totalElements: parsedData.totalElements || 0,
              totalPages: parsedData.totalPages || 0,
              size: parsedData.size || params.size,
              number: parsedData.number || params.page
            }
          }
        }
        
        // 如果已经是标准格式，直接返回
        return parsedData
      } catch (e) {
        console.error('响应数据解析失败:', e)
        // 如果解析失败，返回原始数据
        return data
      }
    }]
  }).catch(error => {
    console.error('请求错误:', error)
    
    // 处理500错误
    if (error.response?.status === 500) {
      return {
        code: 500,
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          size: params.size,
          number: params.page
        },
        message: error.response?.data?.message || '服务器内部错误，请稍后重试'
      }
    }
    
    // 处理404错误
    if (error.response?.status === 404) {
      return {
        code: 200,
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          size: params.size,
          number: params.page
        }
      }
    }
    
    // 其他错误直接抛出
    throw error
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