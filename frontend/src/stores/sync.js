import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getRumorList, getRumorDetail } from '@/api/detection'
import { getLogStatistics } from '@/api/log'
import { getSystemConfig } from '@/api/system'

export const useSyncStore = defineStore('sync', () => {
  // 状态
  const lastSyncTime = ref(null)
  const syncStatus = ref('idle') // idle, syncing, success, error
  const syncError = ref(null)
  const cacheSize = ref(0)
  const cacheItems = ref([])
  const syncSettings = ref({
    autoSync: true,
    interval: '5',
    dataTypes: ['user', 'rumor', 'analysis', 'blockchain']
  })

  // 计算属性
  const isSyncing = computed(() => syncStatus.value === 'syncing')
  const hasError = computed(() => syncStatus.value === 'error')
  const formattedLastSyncTime = computed(() => {
    if (!lastSyncTime.value) return '从未同步'
    return new Date(lastSyncTime.value).toLocaleString()
  })

  // 方法
  const startSync = async () => {
    try {
      syncStatus.value = 'syncing'
      syncError.value = null
      
      // 同步谣言数据
      if (syncSettings.value.dataTypes.includes('rumor')) {
        await getRumorList()
      }
      
      // 同步日志数据
      if (syncSettings.value.dataTypes.includes('analysis')) {
        await getLogStatistics()
      }
      
      // 同步系统配置
      if (syncSettings.value.dataTypes.includes('system')) {
        await getSystemConfig()
      }
      
      lastSyncTime.value = new Date().toISOString()
      syncStatus.value = 'success'
      ElMessage.success('数据同步成功')
    } catch (error) {
      syncStatus.value = 'error'
      syncError.value = error.message
      ElMessage.error('数据同步失败：' + error.message)
    }
  }

  const clearCache = () => {
    try {
      // 清除本地缓存
      localStorage.clear()
      sessionStorage.clear()
      
      cacheSize.value = 0
      cacheItems.value = []
      
      ElMessage.success('缓存清除成功')
    } catch (error) {
      ElMessage.error('缓存清除失败：' + error.message)
    }
  }

  const getCacheInfo = () => {
    try {
      // 计算缓存大小
      let size = 0
      const items = []
      
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i)
        const value = localStorage.getItem(key)
        size += value.length
        items.push({ key, size: value.length })
      }
      
      cacheSize.value = size
      cacheItems.value = items
    } catch (error) {
      ElMessage.error('获取缓存信息失败：' + error.message)
    }
  }

  const removeCacheItem = (key) => {
    try {
      localStorage.removeItem(key)
      getCacheInfo()
      ElMessage.success('缓存项删除成功')
    } catch (error) {
      ElMessage.error('缓存项删除失败：' + error.message)
    }
  }

  const updateSyncSettings = (settings) => {
    syncSettings.value = { ...syncSettings.value, ...settings }
    // 如果启用了自动同步，重新设置定时器
    if (settings.autoSync) {
      autoSync()
    }
  }

  // 自动同步
  const autoSync = () => {
    // 清除现有的定时器
    if (window.syncInterval) {
      clearInterval(window.syncInterval)
    }
    
    // 根据设置的时间间隔设置新的定时器
    const interval = parseInt(syncSettings.value.interval) * 60 * 1000
    window.syncInterval = setInterval(() => {
      if (syncStatus.value !== 'syncing') {
        startSync()
      }
    }, interval)
  }

  // 初始化
  getCacheInfo()
  if (syncSettings.value.autoSync) {
    autoSync()
  }

  return {
    // 状态
    lastSyncTime,
    syncStatus,
    syncError,
    cacheSize,
    cacheItems,
    syncSettings,
    
    // 计算属性
    isSyncing,
    hasError,
    formattedLastSyncTime,
    
    // 方法
    startSync,
    clearCache,
    getCacheInfo,
    removeCacheItem,
    updateSyncSettings
  }
})