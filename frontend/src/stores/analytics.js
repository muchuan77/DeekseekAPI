import { defineStore } from 'pinia'
import { analyticsApi } from '@/api/analytics'
import { ElMessage } from 'element-plus'

export const useAnalyticsStore = defineStore('analytics', {
  state: () => ({
    statistics: {},
    trends: [],
    loading: false
  }),

  getters: {
    getStatistics: (state) => state.statistics,
    getTrends: (state) => state.trends
  },

  actions: {
    async fetchStatistics(params = {}) {
      try {
        this.loading = true
        const response = await analyticsApi.getStatistics(params)
        if (response.code === 200) {
          this.statistics = response.data
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        ElMessage.error('获取统计数据失败')
      } finally {
        this.loading = false
      }
    },

    async fetchTrends(params = {}) {
      try {
        this.loading = true
        const response = await analyticsApi.getTrends(params)
        if (response.code === 200) {
          this.trends = response.data
        }
      } catch (error) {
        console.error('获取趋势数据失败:', error)
        ElMessage.error('获取趋势数据失败')
      } finally {
        this.loading = false
      }
    },

    async exportData(params = {}) {
      try {
        const response = await analyticsApi.exportData(params)
        if (response.code === 200) {
          ElMessage.success('导出数据成功')
          return true
        }
        return false
      } catch (error) {
        console.error('导出数据失败:', error)
        ElMessage.error('导出数据失败')
        return false
      }
    }
  }
}) 