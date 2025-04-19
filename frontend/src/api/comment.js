import request from '@/utils/request'

export function createComment(data) {
  return request({
    url: '/api/comments',
    method: 'post',
    data
  })
}

export function getComments(params) {
  return request({
    url: '/api/comments',
    method: 'get',
    params
  })
}

export function deleteComment(id) {
  return request({
    url: `/api/comments/${id}`,
    method: 'delete'
  })
} 