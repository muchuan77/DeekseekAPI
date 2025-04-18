import request from '@/utils/request'

// 文本分析
export function analyzeText(data) {
  return request({
    url: '/api/deepseek/analyze/text',
    method: 'post',
    data: {
      content: data
    }
  })
}

// 图像分析
export function analyzeImage(data) {
  return request({
    url: '/api/deepseek/analyze/image',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: {
      content: data
    }
  })
}

// 视频分析
export function analyzeVideo(data) {
  return request({
    url: '/api/deepseek/analyze/video',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: {
      content: data
    }
  })
}

// 获取分析结果
export function getAnalysisResult(analysisId) {
  return request({
    url: `/api/deepseek/results/${analysisId}`,
    method: 'get'
  })
}

// 获取分析历史
export function getAnalysisHistory(params) {
  return request({
    url: '/api/deepseek/history',
    method: 'get',
    params
  })
}

// 获取分析统计
export function getAnalysisStatistics() {
  return request({
    url: '/api/deepseek/statistics',
    method: 'get'
  })
}

// 获取分析配置
export function getAnalysisConfig() {
  return request({
    url: '/api/deepseek/config',
    method: 'get'
  })
}

// 更新分析配置
export function updateAnalysisConfig(data) {
  return request({
    url: '/api/deepseek/config',
    method: 'put',
    data
  })
} 