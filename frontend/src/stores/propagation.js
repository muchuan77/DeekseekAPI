import { defineStore } from 'pinia'
import { propagationApi } from '@/api/propagation'
import { ElMessage } from 'element-plus'

export const usePropagationStore = defineStore('propagation', {
  state: () => ({
    propagationData: [],
    analysisResults: {},
    loading: false,
    totalData: 0
  }),

  getters: {
    getPropagationData: (state) => state.propagationData,
    getAnalysisResults: (state) => state.analysisResults
  },

  actions: {
    async fetchPropagationData(params = {}) {
      try {
        this.loading = true
        const response = await propagationApi.getPropagationData(params)
        if (response.code === 200) {
          this.propagationData = response.data.content
          this.totalData = response.data.totalElements
        }
      } catch (error) {
        console.error('获取传播数据失败:', error)
        ElMessage.error('获取传播数据失败')
      } finally {
        this.loading = false
      }
    },

    async analyzePropagation(params = {}) {
      try {
        this.loading = true
        const response = await propagationApi.analyzePropagation(params)
        if (response.code === 200) {
          this.analysisResults = response.data
          return true
        }
        return false
      } catch (error) {
        console.error('传播分析失败:', error)
        ElMessage.error('传播分析失败')
        return false
      } finally {
        this.loading = false
      }
    },

    async exportPropagationData(params = {}) {
      try {
        const response = await propagationApi.exportPropagationData(params)
        if (response.code === 200) {
          ElMessage.success('导出传播数据成功')
          return true
        }
        return false
      } catch (error) {
        console.error('导出传播数据失败:', error)
        ElMessage.error('导出传播数据失败')
        return false
      }
    },

    async getPropagationTrends(params = {}) {
      try {
        this.loading = true
        const response = await propagationApi.getPropagationTrends(params)
        if (response.code === 200) {
          return response.data
        }
        return null
      } catch (error) {
        console.error('获取传播趋势失败:', error)
        ElMessage.error('获取传播趋势失败')
        return null
      } finally {
        this.loading = false
      }
    }
  }
}) 