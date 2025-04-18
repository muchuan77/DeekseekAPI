import request from '@/utils/request'

// 统计分析
export function getStatistics(params) {
  return request({
    url: '/api/analytics/statistics',
    method: 'get',
    params
  })
}

export function getTrends(params) {
  return request({
    url: '/api/analytics/trends',
    method: 'get',
    params
  })
}

export function getPropagationAnalysis(rumorId) {
  return request({
    url: `/analytics/propagation/${rumorId}`,
    method: 'get'
  })
}

// 内容分析
export function analyzeText(data) {
  return request({
    url: '/analysis/text',
    method: 'post',
    data
  })
}

export function analyzeImage(data) {
  return request({
    url: '/analysis/image',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

export function analyzeVideo(data) {
  return request({
    url: '/analysis/video',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

export function analyzeMultiModal(data) {
  return request({
    url: '/analysis/multimodal',
    method: 'post',
    data
  })
} 