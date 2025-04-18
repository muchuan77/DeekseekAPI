import request from '@/utils/request'

// 获取传播路径
export function getPropagationPaths(rumorId, params) {
  return request({
    url: `/propagation/paths/${rumorId}`,
    method: 'get',
    params
  })
}

// 获取影响力分析
export function getInfluenceAnalysis(rumorId) {
  return request({
    url: `/propagation/influence/${rumorId}`,
    method: 'get'
  })
}

// 获取传播趋势
export function getPropagationTrends(rumorId) {
  return request({
    url: `/propagation/trends/${rumorId}`,
    method: 'get'
  })
}

// 获取关键节点
export function getKeyNodes(rumorId) {
  return request({
    url: `/propagation/key-nodes/${rumorId}`,
    method: 'get'
  })
}

// 获取传播网络
export function getPropagationNetwork(rumorId) {
  return request({
    url: `/propagation/network/${rumorId}`,
    method: 'get'
  })
}

// 获取传播统计
export function getPropagationStatistics(rumorId) {
  return request({
    url: `/propagation/statistics/${rumorId}`,
    method: 'get'
  })
} 