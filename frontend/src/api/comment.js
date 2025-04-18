import request from '@/utils/request'

export function createComment(data) {
  return request({
    url: '/comments',
    method: 'post',
    data
  })
}

export function getComments(params) {
  return request({
    url: '/comments',
    method: 'get',
    params
  })
}

export function deleteComment(id) {
  return request({
    url: `/comments/${id}`,
    method: 'delete'
  })
} 