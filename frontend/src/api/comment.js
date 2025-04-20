import request from '@/utils/request'

const commentApi = {
  createComment(data) {
    return request({
      url: '/api/comments',
      method: 'post',
      data
    })
  },

  getComments(params) {
    const requestParams = {
      ...params,
      rumorId: params.rumorId || '1'
    }
    
    return request({
      url: '/api/comments',
      method: 'get',
      params: requestParams
    }).catch(error => {
      if (error.response?.status === 500) {
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
      throw error
    })
  },

  deleteComment(id) {
    return request({
      url: `/api/comments/${id}`,
      method: 'delete'
    })
  },

  updateComment(id, data) {
    return request({
      url: `/api/comments/${id}`,
      method: 'put',
      data
    })
  }
}

export { commentApi } 