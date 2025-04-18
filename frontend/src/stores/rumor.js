import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as rumorApi from '@/api/rumor'

export const useRumorStore = defineStore('rumor', () => {
  const rumors = ref([])
  const currentRumor = ref(null)
  const loading = ref(false)
  const total = ref(0)
  const totalPages = ref(0)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const searchParams = ref({
    keyword: '',
    status: ''
  })

  const fetchRumors = async (params = {}) => {
    try {
      loading.value = true
      // 合并搜索参数
      const searchParams = {
        ...params,
        page: currentPage.value,
        size: pageSize.value
      }
      
      const response = await rumorApi.getRumors(searchParams)
      if (response.code === 200 && response.data) {
        rumors.value = response.data.content
        total.value = response.data.totalElements
        totalPages.value = response.data.totalPages
      } else {
        rumors.value = []
        total.value = 0
        totalPages.value = 0
        ElMessage.error(response.message || '获取谣言列表失败')
      }
    } catch (error) {
      console.error('获取谣言列表失败:', error)
      rumors.value = []
      total.value = 0
      totalPages.value = 0
      ElMessage.error('获取谣言列表失败')
    } finally {
      loading.value = false
    }
  }

  const fetchRumor = async (id) => {
    try {
      loading.value = true
      const response = await rumorApi.getRumor(id)
      if (response.code === 200 && response.data) {
        currentRumor.value = response.data
      } else {
        currentRumor.value = null
        ElMessage.error(response.message || '获取谣言详情失败')
      }
    } catch (error) {
      console.error('获取谣言详情失败:', error)
      currentRumor.value = null
      ElMessage.error('获取谣言详情失败')
    } finally {
      loading.value = false
    }
  }

  const createRumor = async (rumorData) => {
    try {
      loading.value = true
      const response = await rumorApi.createRumor(rumorData)
      if (response.code === 200) {
        ElMessage.success('创建谣言成功')
        return response.data
      } else {
        ElMessage.error(response.message || '创建谣言失败')
        throw new Error(response.message || '创建谣言失败')
      }
    } catch (error) {
      console.error('创建谣言失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateRumor = async (id, rumorData) => {
    try {
      loading.value = true
      const response = await rumorApi.updateRumor(id, rumorData)
      if (response.code === 200) {
        ElMessage.success('更新谣言成功')
        return response.data
      } else {
        ElMessage.error(response.message || '更新谣言失败')
        throw new Error(response.message || '更新谣言失败')
      }
    } catch (error) {
      console.error('更新谣言失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateRumorStatus = async (id, status) => {
    try {
      loading.value = true
      const response = await rumorApi.updateRumorStatus(id, status)
      if (response.code === 200) {
        ElMessage.success('更新状态成功')
        return response.data
      } else {
        ElMessage.error(response.message || '更新状态失败')
        throw new Error(response.message || '更新状态失败')
      }
    } catch (error) {
      console.error('更新状态失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteRumor = async (id) => {
    try {
      loading.value = true
      const response = await rumorApi.deleteRumor(id)
      if (response.code === 200) {
        ElMessage.success('删除谣言成功')
        // 重新获取列表
        await fetchRumors(searchParams.value)
      } else {
        ElMessage.error(response.message || '删除谣言失败')
        throw new Error(response.message || '删除谣言失败')
      }
    } catch (error) {
      console.error('删除谣言失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateSearchParams = (params) => {
    searchParams.value = { ...searchParams.value, ...params }
    currentPage.value = 0 // 重置页码
    fetchRumors(searchParams.value)
  }

  return {
    rumors,
    currentRumor,
    loading,
    total,
    totalPages,
    currentPage,
    pageSize,
    searchParams,
    fetchRumors,
    fetchRumor,
    createRumor,
    updateRumor,
    updateRumorStatus,
    deleteRumor,
    updateSearchParams
  }
}) 