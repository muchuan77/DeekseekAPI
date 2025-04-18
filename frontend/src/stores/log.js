import { defineStore } from 'pinia'
import { logApi } from '@/api/log'
import { ElMessage } from 'element-plus'

export const useLogStore = defineStore('log', {
  state: () => ({
    systemLogs: [],
    operationLogs: [],
    auditLogs: [],
    loading: false,
    totalSystemLogs: 0,
    totalOperationLogs: 0,
    totalAuditLogs: 0,
    // 可视化数据
    userActivityData: {
      dates: [],
      counts: []
    },
    operationTypeData: [],
    errorDetails: [],
    serviceHealth: {}
  }),

  getters: {
    getSystemLogById: (state) => (id) => {
      return state.systemLogs.find(log => log.id === id)
    },
    getOperationLogById: (state) => (id) => {
      return state.operationLogs.find(log => log.id === id)
    },
    getAuditLogById: (state) => (id) => {
      return state.auditLogs.find(log => log.id === id)
    }
  },

  actions: {
    async fetchSystemLogs(params = {}) {
      try {
        this.loading = true
        const response = await logApi.getSystemLogs(params)
        if (response.code === 200) {
          this.systemLogs = response.data.content
          this.totalSystemLogs = response.data.totalElements
        }
      } catch (error) {
        console.error('获取系统日志失败:', error)
        ElMessage.error('获取系统日志失败')
      } finally {
        this.loading = false
      }
    },

    async fetchOperationLogs(params = {}) {
      try {
        this.loading = true
        const response = await logApi.getOperationLogs(params)
        if (response.code === 200) {
          this.operationLogs = response.data.content
          this.totalOperationLogs = response.data.totalElements
        }
      } catch (error) {
        console.error('获取操作日志失败:', error)
        ElMessage.error('获取操作日志失败')
      } finally {
        this.loading = false
      }
    },

    async fetchAuditLogs(params = {}) {
      try {
        this.loading = true
        const response = await logApi.getAuditLogs(params)
        if (response.code === 200) {
          this.auditLogs = response.data.content
          this.totalAuditLogs = response.data.totalElements
        }
      } catch (error) {
        console.error('获取审计日志失败:', error)
        ElMessage.error('获取审计日志失败')
      } finally {
        this.loading = false
      }
    },

    async clearLogs(logType) {
      try {
        const response = await logApi.clearLogs(logType)
        if (response.code === 200) {
          ElMessage.success('清除日志成功')
          return true
        }
        return false
      } catch (error) {
        console.error('清除日志失败:', error)
        ElMessage.error('清除日志失败')
        return false
      }
    },

    // 可视化相关方法
    async fetchUserActivityData(start, end) {
      try {
        this.loading = true
        const response = await logApi.getLogStatistics(start, end)
        if (response.code === 200) {
          this.userActivityData = response.data.userActivity
        }
      } catch (error) {
        console.error('获取用户活跃度数据失败:', error)
        ElMessage.error('获取用户活跃度数据失败')
      } finally {
        this.loading = false
      }
    },

    async fetchOperationTypeData(start, end) {
      try {
        this.loading = true
        const response = await logApi.getLogStatistics(start, end)
        if (response.code === 200) {
          this.operationTypeData = response.data.operationTypes
        }
      } catch (error) {
        console.error('获取操作类型数据失败:', error)
        ElMessage.error('获取操作类型数据失败')
      } finally {
        this.loading = false
      }
    },

    async fetchErrorDetails(errorType, start, end) {
      try {
        this.loading = true
        const response = await logApi.getErrorDetails(errorType, start, end)
        if (response.code === 200) {
          this.errorDetails = response.data
        }
      } catch (error) {
        console.error('获取错误详情失败:', error)
        ElMessage.error('获取错误详情失败')
      } finally {
        this.loading = false
      }
    },

    async fetchServiceHealth(serviceName, start, end) {
      try {
        this.loading = true
        const response = await logApi.getServiceHealth(serviceName, start, end)
        if (response.code === 200) {
          this.serviceHealth[serviceName] = response.data
        }
      } catch (error) {
        console.error('获取服务健康状态失败:', error)
        ElMessage.error('获取服务健康状态失败')
      } finally {
        this.loading = false
      }
    }
  }
}) 