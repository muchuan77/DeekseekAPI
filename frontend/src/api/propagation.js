import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 统一的错误处理函数
const handleError = (error, defaultMessage) => {
  console.error('API请求错误:', error)
  const message = error.response?.data?.message || error.message || defaultMessage
  ElMessage.error(message)
  throw error
}

// 获取传播路径
export function getPropagationPaths(rumorId, params) {
  if (!rumorId) {
    return Promise.reject(new Error('谣言ID不能为空'))
  }
  return request({
    url: `/api/propagation/paths/${rumorId}`,
    method: 'get',
    params: {
      ...params,
      page: params.page - 1 // 后端页码从0开始
    }
  }).catch(error => handleError(error, '获取传播路径失败'))
}

// 获取影响力分析
export function getInfluenceAnalysis(rumorId) {
  if (!rumorId) {
    return Promise.reject(new Error('谣言ID不能为空'))
  }
  return request({
    url: `/api/propagation/influence/${rumorId}`,
    method: 'get'
  }).catch(error => handleError(error, '获取影响力分析失败'))
}

// 获取传播趋势
export function getPropagationTrends(rumorId) {
  if (!rumorId) {
    return Promise.reject(new Error('谣言ID不能为空'))
  }
  return request({
    url: `/api/propagation/trends/${rumorId}`,
    method: 'get'
  }).catch(error => handleError(error, '获取传播趋势失败'))
}

// 获取关键节点
export function getKeyNodes(rumorId) {
  if (!rumorId) {
    return Promise.reject(new Error('谣言ID不能为空'))
  }
  return request({
    url: `/api/propagation/key-nodes/${rumorId}`,
    method: 'get'
  }).catch(error => handleError(error, '获取关键节点失败'))
}

// 获取传播网络
export function getPropagationNetwork(rumorId) {
  if (!rumorId) {
    return Promise.reject(new Error('谣言ID不能为空'))
  }
  return request({
    url: `/api/propagation/network/${rumorId}`,
    method: 'get'
  }).catch(error => handleError(error, '获取传播网络失败'))
}

// 获取传播统计
export function getPropagationStatistics(rumorId) {
  if (!rumorId) {
    return Promise.reject(new Error('谣言ID不能为空'))
  }
  return request({
    url: `/api/propagation/statistics/${rumorId}`,
    method: 'get'
  }).catch(error => handleError(error, '获取传播统计失败'))
} 