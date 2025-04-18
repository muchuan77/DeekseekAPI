import request from '@/utils/request'

// 谣言检测
export function detectRumor(data) {
  return request({
    url: '/api/detection/analyze',
    method: 'post',
    data
  })
}

export function getDetectionHistory(params) {
  return request({
    url: '/api/detection/history',
    method: 'get',
    params
  })
}

export function getDetectionResult(id) {
  return request({
    url: `/api/detection/results/${id}`,
    method: 'get'
  })
}