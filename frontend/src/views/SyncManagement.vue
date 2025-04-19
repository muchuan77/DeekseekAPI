<template>
    <div class="sync-container">
      <el-row :gutter="20">
        <!-- 同步状态卡片 -->
        <el-col :span="12">
          <el-card class="sync-card">
            <template #header>
              <div class="card-header">
                <span>数据同步状态</span>
                <el-button 
                  type="primary" 
                  :loading="isSyncing" 
                  @click="startSync"
                  :disabled="isSyncing"
                >
                  立即同步
                </el-button>
              </div>
            </template>
            <div class="sync-info">
              <p>最后同步时间：{{ formattedLastSyncTime }}</p>
              <p>同步状态：
                <el-tag :type="getStatusType(syncStatus)">
                  {{ getStatusText(syncStatus) }}
                </el-tag>
              </p>
              <p v-if="hasError" class="error-message">
                错误信息：{{ syncError }}
              </p>
            </div>
          </el-card>
        </el-col>
  
        <!-- 缓存管理卡片 -->
        <el-col :span="12">
          <el-card class="cache-card">
            <template #header>
              <div class="card-header">
                <span>缓存管理</span>
                <el-button type="danger" @click="clearCache">清除缓存</el-button>
              </div>
            </template>
            <div class="cache-info">
              <p>缓存大小：{{ formatSize(cacheSize) }}</p>
              <p>缓存项数量：{{ cacheItems.length }}</p>
            </div>
            <el-table :data="cacheItems" style="width: 100%">
              <el-table-column prop="key" label="键名" />
              <el-table-column prop="size" label="大小">
                <template #default="scope">
                  {{ formatSize(scope.row.size) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="removeCacheItem(scope.row.key)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 同步设置 -->
      <el-card class="settings-card">
        <template #header>
          <div class="card-header">
            <span>同步设置</span>
          </div>
        </template>
        <el-form :model="syncSettings" label-width="120px">
          <el-form-item label="自动同步">
            <el-switch v-model="syncSettings.autoSync" @change="handleSettingsChange" />
          </el-form-item>
          <el-form-item label="同步间隔">
            <el-select v-model="syncSettings.interval" :disabled="!syncSettings.autoSync" @change="handleSettingsChange">
              <el-option label="5分钟" value="5" />
              <el-option label="15分钟" value="15" />
              <el-option label="30分钟" value="30" />
              <el-option label="1小时" value="60" />
            </el-select>
          </el-form-item>
          <el-form-item label="同步数据类型">
            <el-checkbox-group v-model="syncSettings.dataTypes" @change="handleSettingsChange">
              <el-checkbox label="user">用户数据</el-checkbox>
              <el-checkbox label="rumor">谣言数据</el-checkbox>
              <el-checkbox label="analysis">分析数据</el-checkbox>
              <el-checkbox label="blockchain">区块链数据</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import { getRumors } from '@/api/rumor'
  import { getLogStatistics } from '@/api/log'
  import { getSystemConfig } from '@/api/system'
  
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
  const isSyncing = ref(false)
  const hasError = ref(false)
  const formattedLastSyncTime = ref('从未同步')
  
  // 方法
  const startSync = async () => {
    try {
      syncStatus.value = 'syncing'
      syncError.value = null
      isSyncing.value = true
      
      // 同步谣言数据
      if (syncSettings.value.dataTypes.includes('rumor')) {
        await getRumors()
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
      formattedLastSyncTime.value = new Date().toLocaleString()
      ElMessage.success('数据同步成功')
    } catch (error) {
      syncStatus.value = 'error'
      syncError.value = error.message
      hasError.value = true
      ElMessage.error('数据同步失败：' + error.message)
    } finally {
      isSyncing.value = false
    }
  }
  
  const clearCache = () => {
    ElMessageBox.confirm(
      '确定要清除所有缓存数据吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      try {
        localStorage.clear()
        sessionStorage.clear()
        cacheSize.value = 0
        cacheItems.value = []
        ElMessage.success('缓存清除成功')
      } catch (error) {
        ElMessage.error('缓存清除失败：' + error.message)
      }
    })
  }
  
  const removeCacheItem = (key) => {
    ElMessageBox.confirm(
      `确定要删除缓存项 "${key}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      try {
        localStorage.removeItem(key)
        getCacheInfo()
        ElMessage.success('缓存项删除成功')
      } catch (error) {
        ElMessage.error('缓存项删除失败：' + error.message)
      }
    })
  }

  const handleSettingsChange = () => {
    // 如果启用了自动同步，重新设置定时器
    if (syncSettings.value.autoSync) {
      autoSync()
    }
  }
  
  const getStatusType = (status) => {
    switch (status) {
      case 'success':
        return 'success'
      case 'error':
        return 'danger'
      case 'syncing':
        return 'warning'
      default:
        return 'info'
    }
  }
  
  const getStatusText = (status) => {
    switch (status) {
      case 'success':
        return '同步成功'
      case 'error':
        return '同步失败'
      case 'syncing':
        return '同步中'
      default:
        return '未同步'
    }
  }
  
  const formatSize = (bytes) => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  const getCacheInfo = () => {
    try {
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
  
  // 生命周期
  onMounted(() => {
    getCacheInfo()
    if (syncSettings.value.autoSync) {
      autoSync()
    }
  })
  </script>
  
  <style scoped>
  .sync-container {
    padding: 20px;
  }
  
  .sync-card,
  .cache-card,
  .settings-card {
    margin-bottom: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .sync-info,
  .cache-info {
    margin-bottom: 20px;
  }
  
  .error-message {
    color: #f56c6c;
    margin-top: 10px;
  }
  </style>