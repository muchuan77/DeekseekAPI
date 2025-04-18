import { defineStore } from 'pinia'
import { aiApi } from '@/api/ai'
import { ElMessage } from 'element-plus'

export const useAIStore = defineStore('ai', {
  state: () => ({
    analysisResults: [],
    models: [],
    loading: false,
    totalResults: 0
  }),

  getters: {
    getAnalysisResultById: (state) => (id) => {
      return state.analysisResults.find(result => result.id === id)
    },
    getModelById: (state) => (id) => {
      return state.models.find(model => model.id === id)
    }
  },

  actions: {
    async analyzeText(text) {
      try {
        this.loading = true
        const response = await aiApi.analyzeText(text)
        if (response.code === 200) {
          ElMessage.success('分析成功')
          return response.data
        }
        return null
      } catch (error) {
        console.error('文本分析失败:', error)
        ElMessage.error('文本分析失败')
        return null
      } finally {
        this.loading = false
      }
    },

    async fetchModels(params = {}) {
      try {
        this.loading = true
        const response = await aiApi.getModels(params)
        if (response.code === 200) {
          this.models = response.data.content
        }
      } catch (error) {
        console.error('获取模型列表失败:', error)
        ElMessage.error('获取模型列表失败')
      } finally {
        this.loading = false
      }
    },

    async trainModel(modelData) {
      try {
        const response = await aiApi.trainModel(modelData)
        if (response.code === 200) {
          ElMessage.success('模型训练成功')
          return true
        }
        return false
      } catch (error) {
        console.error('模型训练失败:', error)
        ElMessage.error('模型训练失败')
        return false
      }
    },

    async updateModel(modelId, modelData) {
      try {
        const response = await aiApi.updateModel(modelId, modelData)
        if (response.code === 200) {
          ElMessage.success('模型更新成功')
          return true
        }
        return false
      } catch (error) {
        console.error('模型更新失败:', error)
        ElMessage.error('模型更新失败')
        return false
      }
    }
  }
}) 