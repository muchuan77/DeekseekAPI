import { defineStore } from 'pinia'
import { ref } from 'vue'
import { 
  getSystemLogs, 
  getOperationLogs, 
  getAuditLogs,
  getLogStatistics,
  getLogTrends,
  getErrorDetails,
  getServiceHealth
} from '@/api/log'
import { ElMessage } from 'element-plus'

export const useLogStore = defineStore('log', () => {
  // 状态
  const systemLogs = ref([])
  const operationLogs = ref([])
  const auditLogs = ref([])
  const userActivityData = ref({ dates: [], counts: [] })
  const operationTypeData = ref([])
  const errorDetails = ref([])
  const serviceHealth = ref({})
  const loading = ref(false)

  // 获取系统日志
  const fetchSystemLogs = async (params) => {
    try {
      loading.value = true
      const response = await getSystemLogs(params)
      if (response.code === 200) {
        systemLogs.value = response.data.content
      }
    } catch (error) {
      console.error('获取系统日志失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取操作日志
  const fetchOperationLogs = async (params) => {
    try {
      loading.value = true
      const response = await getOperationLogs(params)
      if (response.code === 200) {
        operationLogs.value = response.data.content
      }
    } catch (error) {
      console.error('获取操作日志失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取审计日志
  const fetchAuditLogs = async (params) => {
    try {
      loading.value = true
      const response = await getAuditLogs(params)
      if (response.code === 200) {
        auditLogs.value = response.data.content
      }
    } catch (error) {
      console.error('获取审计日志失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取用户活跃度数据
  const fetchUserActivityData = async (startTime, endTime) => {
    try {
      loading.value = true
      const response = await getLogStatistics({ start: startTime, end: endTime })
      if (response.code === 200) {
        userActivityData.value = {
          dates: Object.keys(response.data.userActivityCounts),
          counts: Object.values(response.data.userActivityCounts)
        }
      }
    } catch (error) {
      console.error('获取用户活跃度数据失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取操作类型数据
  const fetchOperationTypeData = async (startTime, endTime) => {
    try {
      loading.value = true
      const response = await getLogStatistics({ start: startTime, end: endTime })
      if (response.code === 200) {
        operationTypeData.value = Object.entries(response.data.operationTypeCounts)
          .map(([name, value]) => ({ name, value }))
      }
    } catch (error) {
      console.error('获取操作类型数据失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取错误详情
  const fetchErrorDetails = async (type, startTime, endTime) => {
    try {
      loading.value = true
      const response = await getErrorDetails(type, { startTime, endTime })
      if (response.code === 200) {
        errorDetails.value = response.data
      }
    } catch (error) {
      console.error('获取错误详情失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取服务健康状态
  const fetchServiceHealth = async (name, startTime, endTime) => {
    try {
      loading.value = true
      const response = await getServiceHealth(name, { startTime, endTime })
      if (response.code === 200) {
        serviceHealth.value[name] = response.data
      }
    } catch (error) {
      console.error('获取服务健康状态失败:', error)
    } finally {
      loading.value = false
    }
  }

  return {
    systemLogs,
    operationLogs,
    auditLogs,
    userActivityData,
    operationTypeData,
    errorDetails,
    serviceHealth,
    loading,
    fetchSystemLogs,
    fetchOperationLogs,
    fetchAuditLogs,
    fetchUserActivityData,
    fetchOperationTypeData,
    fetchErrorDetails,
    fetchServiceHealth
  }
}) 