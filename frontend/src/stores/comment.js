import { defineStore } from 'pinia'
import { commentApi } from '@/api/comment'
import { ElMessage } from 'element-plus'

export const useCommentStore = defineStore('comment', {
  state: () => ({
    comments: [],
    loading: false,
    totalComments: 0
  }),

  getters: {
    getCommentById: (state) => (id) => {
      return state.comments.find(comment => comment.id === id)
    },
    getCommentsByRumorId: (state) => (rumorId) => {
      return state.comments.filter(comment => comment.rumorId === rumorId)
    }
  },

  actions: {
    async fetchComments(params = {}) {
      try {
        this.loading = true
        const response = await commentApi.getComments(params)
        if (response.code === 200) {
          this.comments = response.data.content
          this.totalComments = response.data.totalElements
        }
      } catch (error) {
        console.error('获取评论列表失败:', error)
        ElMessage.error('获取评论列表失败')
      } finally {
        this.loading = false
      }
    },

    async createComment(commentData) {
      try {
        const response = await commentApi.createComment(commentData)
        if (response.code === 200) {
          ElMessage.success('评论发布成功')
          return true
        }
        return false
      } catch (error) {
        console.error('评论发布失败:', error)
        ElMessage.error('评论发布失败')
        return false
      }
    },

    async updateComment(commentId, commentData) {
      try {
        const response = await commentApi.updateComment(commentId, commentData)
        if (response.code === 200) {
          ElMessage.success('评论更新成功')
          return true
        }
        return false
      } catch (error) {
        console.error('评论更新失败:', error)
        ElMessage.error('评论更新失败')
        return false
      }
    },

    async deleteComment(commentId) {
      try {
        const response = await commentApi.deleteComment(commentId)
        if (response.code === 200) {
          ElMessage.success('评论删除成功')
          return true
        }
        return false
      } catch (error) {
        console.error('评论删除失败:', error)
        ElMessage.error('评论删除失败')
        return false
      }
    }
  }
}) 