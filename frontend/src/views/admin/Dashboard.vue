<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(card, index) in cards" :key="index">
        <el-card shadow="hover" class="dashboard-card">
          <div class="card-content">
            <div class="card-icon" :style="{ backgroundColor: card.color }">
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ card.value }}</div>
              <div class="card-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- 这里可以添加图表组件 -->
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统资源使用情况</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- 这里可以添加图表组件 -->
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <el-table :data="activities" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="user" label="用户" width="120" />
            <el-table-column prop="action" label="操作" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '成功' ? 'success' : 'danger'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// import { getDashboardData, getSystemOverview, getRecentActivities, getPerformanceMetrics } from '@/api/dashboard' // Comment out unused imports

const cards = ref([
  {
    label: '总用户数',
    value: '0',
    icon: 'User',
    color: '#409EFF'
  },
  {
    label: '今日活跃',
    value: '0',
    icon: 'Connection',
    color: '#67C23A'
  },
  {
    label: '待处理任务',
    value: '0',
    icon: 'Document',
    color: '#E6A23C'
  },
  {
    label: '系统警告',
    value: '0',
    icon: 'Warning',
    color: '#F56C6C'
  }
])

const activities = ref([]) // Keep this as it's used in the template's el-table
// Comment out unused state variables
// const loading = ref(false)
// const statistics = ref({})
// const systemOverview = ref({})
// const recentActivities = ref([]) // activities is used instead
// const performanceMetrics = ref({})
// const error = ref(null)

// Comment out the unused function definition
// const loadData = async () => {
//   console.log("Skipping data loading in admin dashboard for now.")
// }

onMounted(() => {
  // loadData() // Call remains commented out
  // If activities needs mock data, add it here:
  activities.value = [
    { time: '2024-07-28 10:00', user: 'admin', action: '登录系统', status: '成功' },
    { time: '2024-07-28 09:30', user: 'user1', action: '提交谣言检测', status: '成功' },
    { time: '2024-07-28 09:00', user: 'admin', action: '更新用户角色', status: '成功' },
  ];
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.dashboard-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.card-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.table-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}
</style> 