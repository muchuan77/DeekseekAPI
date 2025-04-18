import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as detectionApi from '@/api/detection'

export const useDetectionStore = defineStore('detection', () => {
  const detectionResults = ref([])
  const loading = ref(false)

  const detectRumor = async (data) => {
    try {
      loading.value = true
      return await detectionApi.detectRumor(data)
    } catch (error) {
      ElMessage.error('检测失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  const getDetectionHistory = async (params) => {
    try {
      loading.value = true
      const { data } = await detectionApi.getDetectionHistory(params)
      detectionResults.value = data.data
    } catch (error) {
      ElMessage.error('获取检测历史失败')
    } finally {
      loading.value = false
    }
  }

  const getDetectionResult = async (id) => {
    try {
      loading.value = true
      const { data } = await detectionApi.getDetectionResult(id)
      return data.data
    } catch (error) {
      ElMessage.error('获取检测结果失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  const saveRumor = async (data) => {
    try {
      loading.value = true
      const { data: response } = await detectionApi.createRumor(data)
      return response.data
    } catch (error) {
      ElMessage.error('保存失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    detectionResults,
    loading,
    detectRumor,
    getDetectionHistory,
    getDetectionResult,
    saveRumor
  }
}) 