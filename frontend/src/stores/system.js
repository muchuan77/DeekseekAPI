import { defineStore } from 'pinia'
import { 
  getSystemConfig,
  updateSystemConfig,
  getSystemStatus,
  getSystemResources,
  getSystemPerformance,
  getSystemAlerts,
  updateAlertStatus
} from '@/api/system'
import { ElMessage } from 'element-plus'

export const useSystemStore = defineStore('system', {
  state: () => ({
    config: {},
    status: {},
    resources: {},
    performance: {},
    alerts: [],
    loading: false
  }),

  getters: {
    getConfig: (state) => state.config,
    getStatus: (state) => state.status,
    getResources: (state) => state.resources,
    getPerformance: (state) => state.performance,
    getAlerts: (state) => state.alerts
  },

  actions: {
    async fetchConfig() {
      try {
        this.loading = true
        const response = await getSystemConfig()
        if (response.code === 200) {
          this.config = response.data
        }
      } catch (error) {
        console.error('获取系统配置失败:', error)
        ElMessage.error('获取系统配置失败')
      } finally {
        this.loading = false
      }
    },

    async updateConfig(configData) {
      try {
        const response = await updateSystemConfig(configData)
        if (response.code === 200) {
          ElMessage.success('更新系统配置成功')
          return true
        }
        return false
      } catch (error) {
        console.error('更新系统配置失败:', error)
        ElMessage.error('更新系统配置失败')
        return false
      }
    },

    async fetchStatus() {
      try {
        this.loading = true
        const response = await getSystemStatus()
        if (response.code === 200) {
          this.status = response.data
        }
      } catch (error) {
        console.error('获取系统状态失败:', error)
        ElMessage.error('获取系统状态失败')
      } finally {
        this.loading = false
      }
    },

    async fetchResources() {
      try {
        this.loading = true
        const response = await getSystemResources()
        if (response.code === 200) {
          this.resources = response.data
        }
      } catch (error) {
        console.error('获取系统资源失败:', error)
        ElMessage.error('获取系统资源失败')
      } finally {
        this.loading = false
      }
    },

    async fetchPerformance() {
      try {
        this.loading = true
        const response = await getSystemPerformance()
        if (response.code === 200) {
          this.performance = response.data
        }
      } catch (error) {
        console.error('获取系统性能失败:', error)
        ElMessage.error('获取系统性能失败')
      } finally {
        this.loading = false
      }
    },

    async fetchAlerts() {
      try {
        this.loading = true
        const response = await getSystemAlerts()
        if (response.code === 200) {
          this.alerts = response.data
        }
      } catch (error) {
        console.error('获取系统告警失败:', error)
        ElMessage.error('获取系统告警失败')
      } finally {
        this.loading = false
      }
    },

    async updateAlertStatus(alertId, status) {
      try {
        const response = await updateAlertStatus(alertId, status)
        if (response.code === 200) {
          ElMessage.success('更新告警状态成功')
          return true
        }
        return false
      } catch (error) {
        console.error('更新告警状态失败:', error)
        ElMessage.error('更新告警状态失败')
        return false
      }
    }
  }
}) 