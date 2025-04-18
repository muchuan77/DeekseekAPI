import request from '@/utils/request'

// 多模态分析
export function analyzeMultiModal(data) {
  return request({
    url: '/analysis/multimodal',
    method: 'post',
    data
  })
}

// 文本分析
export function analyzeText(data) {
  return request({
    url: '/analysis/text',
    method: 'post',
    data
  })
}

// 图像分析
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

// 视频分析
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

// 获取分析历史
export function getAnalysisHistory(params) {
  return request({
    url: '/analysis/history',
    method: 'get',
    params
  })
}

// 获取分析结果
export function getAnalysisResult(id) {
  return request({
    url: `/analysis/results/${id}`,
    method: 'get'
  })
} 