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
  import { useSyncStore } from '@/stores/sync'
  import { ElMessageBox } from 'element-plus'
  
  const syncStore = useSyncStore()
  
  // 计算属性
  const {
    syncStatus,
    syncError,
    cacheSize,
    cacheItems,
    isSyncing,
    hasError,
    formattedLastSyncTime,
    syncSettings
  } = syncStore
  
  // 方法
  const startSync = () => {
    syncStore.startSync()
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
      syncStore.clearCache()
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
      syncStore.removeCacheItem(key)
    })
  }

  const handleSettingsChange = () => {
    syncStore.updateSyncSettings(syncSettings)
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
  
  // 生命周期
  onMounted(() => {
    syncStore.getCacheInfo()
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