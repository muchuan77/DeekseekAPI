import request from '@/utils/request'

// 获取谣言列表
export const getRumors = (params) => {
  return request({
    url: '/api/rumors/search',
    method: 'get',
    params: {
      page: params?.page || 0,
      size: params?.size || 10,
      keyword: params?.keyword,
      status: params?.status
    }
  })
}

// 获取谣言详情
export const getRumor = (id) => {
  return request({
    url: `/api/rumors/${id}`,
    method: 'get'
  })
}

// 创建谣言
export const createRumor = (data) => {
  return request({
    url: '/api/rumors',
    method: 'post',
    data: {
      title: data.title,
      content: data.content,
      source: data.source,
      status: 'PENDING'  // 默认状态
    }
  })
}

// 更新谣言
export const updateRumor = (id, data) => {
  return request({
    url: `/api/rumors/${id}`,
    method: 'put',
    data: {
      title: data.title,
      content: data.content,
      source: data.source
    }
  })
}

// 更新谣言状态
export const updateRumorStatus = (id, status) => {
  return request({
    url: `/api/rumors/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 删除谣言
export const deleteRumor = (id) => {
  return request({
    url: `/api/rumors/${id}`,
    method: 'delete'
  })
}