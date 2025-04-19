import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 封装错误处理函数
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

// 文本分析
export function analyzeText(data) {
  return request({
    url: '/api/deepseek/analyze/text',
    method: 'post',
    timeout: 300000, // 5分钟超时设置
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  }).catch(error => handleApiError(error, '文本分析'))
}

// 图像分析
export function analyzeImage(data) {
  return request({
    url: '/api/deepseek/analyze/image',
    method: 'post',
    timeout: 300000, // 5分钟超时设置
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  }).catch(error => handleApiError(error, '图像分析'))
}

// 视频分析
export function analyzeVideo(data) {
  return request({
    url: '/api/deepseek/analyze/video',
    method: 'post',
    timeout: 300000, // 5分钟超时设置
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  }).catch(error => handleApiError(error, '视频分析'))
}

// 多模态分析
export function analyzeMultiModal(data) {
  return request({
    url: '/api/deepseek/analyze/multimodal',
    method: 'post',
    timeout: 300000, // 5分钟超时设置
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  }).catch(error => handleApiError(error, '多模态分析'))
}

// 获取分析结果
export function getAnalysisResult(analysisId) {
  return request({
    url: `/api/deepseek/results/${analysisId}`,
    method: 'get'
  }).catch(error => {
    console.error('获取分析结果失败:', error)
    throw error
  })
}

// 获取分析历史
export function getAnalysisHistory(params) {
  return request({
    url: '/api/deepseek/history',
    method: 'get',
    params
  }).catch(error => {
    console.error('获取分析历史失败:', error)
    throw error
  })
}

// 获取分析统计
export function getAnalysisStatistics() {
  return request({
    url: '/api/deepseek/statistics',
    method: 'get'
  }).catch(error => {
    console.error('获取分析统计失败:', error)
    throw error
  })
}

// 获取分析配置
export function getAnalysisConfig() {
  return request({
    url: '/api/deepseek/config',
    method: 'get'
  }).catch(error => {
    console.error('获取分析配置失败:', error)
    throw error
  })
}

// 更新分析配置
export function updateAnalysisConfig(data) {
  return request({
    url: '/api/deepseek/config',
    method: 'put',
    data
  }).catch(error => {
    console.error('更新分析配置失败:', error)
    throw error
  })
} 