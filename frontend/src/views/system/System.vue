<template>
  <div class="system-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>系统配置</span>
              <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
            </div>
          </template>
          
          <el-form :model="systemConfig" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="systemConfig.name" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="systemConfig.description" type="textarea" />
            </el-form-item>
            <el-form-item label="日志级别">
              <el-select v-model="systemConfig.logLevel">
                <el-option label="DEBUG" value="DEBUG" />
                <el-option label="INFO" value="INFO" />
                <el-option label="WARN" value="WARN" />
                <el-option label="ERROR" value="ERROR" />
              </el-select>
            </el-form-item>
            <el-form-item label="数据保留天数">
              <el-input-number v-model="systemConfig.dataRetentionDays" :min="1" :max="365" />
            </el-form-item>
            <el-form-item label="自动备份">
              <el-switch v-model="systemConfig.autoBackup" />
            </el-form-item>
            <el-form-item label="备份频率">
              <el-select v-model="systemConfig.backupFrequency" :disabled="!systemConfig.autoBackup">
                <el-option label="每天" value="daily" />
                <el-option label="每周" value="weekly" />
                <el-option label="每月" value="monthly" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
            </div>
          </template>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统版本">{{ systemStatus.version }}</el-descriptions-item>
            <el-descriptions-item label="运行时间">{{ systemStatus.uptime }}</el-descriptions-item>
            <el-descriptions-item label="CPU使用率">
              <el-progress :percentage="systemStatus.cpuUsage" />
            </el-descriptions-item>
            <el-descriptions-item label="内存使用率">
              <el-progress :percentage="systemStatus.memoryUsage" />
            </el-descriptions-item>
            <el-descriptions-item label="磁盘使用率">
              <el-progress :percentage="systemStatus.diskUsage" />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>系统告警</span>
            </div>
          </template>
          
          <el-table :data="systemAlerts" style="width: 100%">
            <el-table-column prop="level" label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="row.level === 'ERROR' ? 'danger' : row.level === 'WARN' ? 'warning' : 'info'">
                  {{ row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="告警信息" />
            <el-table-column prop="timestamp" label="时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" @click="handleResolveAlert(row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>系统日志</span>
              <div>
                <el-select v-model="logLevel" placeholder="日志级别" style="width: 120px; margin-right: 10px;">
                  <el-option label="全部" value="" />
                  <el-option label="DEBUG" value="DEBUG" />
                  <el-option label="INFO" value="INFO" />
                  <el-option label="WARN" value="WARN" />
                  <el-option label="ERROR" value="ERROR" />
                </el-select>
                <el-button type="primary" @click="loadLogs">查询</el-button>
              </div>
            </div>
          </template>
          
          <el-table :data="systemLogs" style="width: 100%">
            <el-table-column prop="timestamp" label="时间" width="180" />
            <el-table-column prop="level" label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="row.level === 'ERROR' ? 'danger' : row.level === 'WARN' ? 'warning' : 'info'">
                  {{ row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="module" label="模块" width="120" />
            <el-table-column prop="message" label="日志信息" />
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getSystemConfig,
  updateSystemConfig,
  getSystemStatus,
  getSystemAlerts,
  updateAlertStatus,
  getSystemLogs
} from '@/api/system'

const systemConfig = ref({
  name: '',
  description: '',
  logLevel: 'INFO',
  dataRetentionDays: 30,
  autoBackup: true,
  backupFrequency: 'daily'
})

const systemStatus = ref({
  version: '',
  uptime: '',
  cpuUsage: 0,
  memoryUsage: 0,
  diskUsage: 0
})

const systemAlerts = ref([])
const systemLogs = ref([])
const logLevel = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadSystemConfig = async () => {
  try {
    const response = await getSystemConfig()
    if (response.code === 200) {
      systemConfig.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取系统配置失败')
  }
}

const loadSystemStatus = async () => {
  try {
    const response = await getSystemStatus()
    if (response.code === 200) {
      systemStatus.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取系统状态失败')
  }
}

const loadSystemAlerts = async () => {
  try {
    const response = await getSystemAlerts()
    if (response.code === 200) {
      systemAlerts.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取系统告警失败')
  }
}

const loadLogs = async () => {
  try {
    const response = await getSystemLogs({
      level: logLevel.value,
      page: currentPage.value,
      size: pageSize.value
    })
    if (response.code === 200) {
      systemLogs.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取系统日志失败')
  }
}

const handleSaveConfig = async () => {
  try {
    const response = await updateSystemConfig(systemConfig.value)
    if (response.code === 200) {
      ElMessage.success('保存配置成功')
    }
  } catch (error) {
    ElMessage.error('保存配置失败')
  }
}

const handleResolveAlert = async (alert) => {
  try {
    const response = await updateAlertStatus(alert.id, { status: 'RESOLVED' })
    if (response.code === 200) {
      ElMessage.success('处理告警成功')
      loadSystemAlerts()
    }
  } catch (error) {
    ElMessage.error('处理告警失败')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadLogs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadLogs()
}

onMounted(() => {
  loadSystemConfig()
  loadSystemStatus()
  loadSystemAlerts()
  loadLogs()
})
</script>

<style scoped>
.system-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-20 {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>