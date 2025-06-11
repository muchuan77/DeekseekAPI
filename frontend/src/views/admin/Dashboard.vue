<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col
        v-for="(card, index) in cards"
        :key="index"
        :span="6"
      >
        <el-card
          v-loading="loading"
          shadow="hover"
          class="dashboard-card"
        >
          <div class="card-content">
            <div
              class="card-icon"
              :style="{ backgroundColor: card.color }"
            >
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">
                {{ card.value }}
              </div>
              <div class="card-label">
                {{ card.label }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row
      :gutter="20"
      class="chart-row"
    >
      <el-col :span="12">
        <el-card
          v-loading="loading"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>用户活跃度</span>
              <el-radio-group
                v-model="timeRange"
                size="small"
                @change="handleTimeRangeChange"
              >
                <el-radio-button value="week">
                  本周
                </el-radio-button>
                <el-radio-button value="month">
                  本月
                </el-radio-button>
                <el-radio-button value="year">
                  本年
                </el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div
            v-if="showCharts"
            class="chart-container"
          >
            <v-chart
              class="chart"
              :option="userActivityOption"
              autoresize
            />
          </div>
          <div
            v-else
            class="no-data"
          >
            暂无数据
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card
          v-loading="loading"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>操作类型分布</span>
            </div>
          </template>
          <div
            v-if="showCharts"
            class="chart-container"
          >
            <v-chart
              class="chart"
              :option="operationTypeOption"
              autoresize
            />
          </div>
          <div
            v-else
            class="no-data"
          >
            暂无数据
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row
      :gutter="20"
      class="table-row"
    >
      <el-col :span="24">
        <el-card
          v-loading="loading"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>最近操作日志</span>
            </div>
          </template>
          <el-table
            :data="logStore.operationLogs"
            style="width: 100%"
          >
            <el-table-column
              prop="createTime"
              label="时间"
              width="180"
            >
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="username"
              label="用户"
              width="120"
            />
            <el-table-column
              prop="operation"
              label="操作"
            />
            <el-table-column
              prop="status"
              label="状态"
              width="100"
            >
              <template #default="{ row }">
                <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">
                  {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { User, Connection, Document, Warning } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useLogStore } from '@/stores/log'
import { useSystemStore } from '@/stores/system'
import { formatDate } from '@/utils/date'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const userStore = useUserStore()
const logStore = useLogStore()
const systemStore = useSystemStore()
const timeRange = ref('week')
const loading = ref(false)
const showCharts = ref(false)

// 数据卡片
const cards = computed(() => [
  {
    label: '总用户数',
    value: userStore.totalUsers || 0,
    icon: User,
    color: '#409EFF'
  },
  {
    label: '今日活跃',
    value: logStore.userActivityData.counts[logStore.userActivityData.counts.length - 1] || 0,
    icon: Connection,
    color: '#67C23A'
  },
  {
    label: '待处理任务',
    value: systemStore.status?.pendingTasks || 0,
    icon: Document,
    color: '#E6A23C'
  },
  {
    label: '系统警告',
    value: systemStore.status?.warnings || 0,
    icon: Warning,
    color: '#F56C6C'
  }
])

// 用户活跃度图表配置
const userActivityOption = computed(() => {
  if (!showCharts.value) return {}
  const dates = logStore.userActivityData.dates || []
  const counts = logStore.userActivityData.counts || []
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates.length > 0 ? dates : ['暂无数据'],
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [{
      data: counts.length > 0 ? counts : [0],
      type: 'line',
      smooth: true,
      name: '活跃用户数',
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }
})

// 操作类型分布图表配置
const operationTypeOption = computed(() => {
  if (!showCharts.value) return {}
  const data = logStore.operationTypeData || []
  
  return {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      data: data.length > 0 ? data : [{ name: '暂无数据', value: 1 }],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
})

// 获取仪表盘数据
const fetchDashboardData = async () => {
  loading.value = true
  showCharts.value = false
  try {
    // 获取用户数据
    await userStore.fetchUserList({
      page: 0,
      size: 1
    })
    
    // 获取系统状态
    await systemStore.fetchStatus()
    
    // 获取日志数据
    const [operationLogs, userActivity, operationTypes] = await Promise.all([
      logStore.fetchOperationLogs({
        page: 0,
        size: 10
      }),
      logStore.fetchUserActivityData(
        getTimeRangeStart(timeRange.value),
        new Date().toISOString()
      ),
      logStore.fetchOperationTypeData(
        getTimeRangeStart(timeRange.value),
        new Date().toISOString()
      )
    ])

    // 等待DOM更新
    await nextTick()
    
    // 检查是否有数据
    const hasData = logStore.userActivityData.dates.length > 0 ||
                   logStore.operationTypeData.length > 0 ||
                   logStore.operationLogs.length > 0

    if (hasData) {
      showCharts.value = true
      await nextTick()
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取时间范围起始时间
const getTimeRangeStart = (range) => {
  const now = new Date()
  switch (range) {
    case 'week':
      return new Date(now.setDate(now.getDate() - 7)).toISOString()
    case 'month':
      return new Date(now.setMonth(now.getMonth() - 1)).toISOString()
    case 'year':
      return new Date(now.setFullYear(now.getFullYear() - 1)).toISOString()
    default:
      return new Date(now.setDate(now.getDate() - 7)).toISOString()
  }
}

// 处理时间范围变化
const handleTimeRangeChange = async () => {
  await fetchDashboardData()
}

onMounted(async () => {
  await fetchDashboardData()
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

.no-data {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}
</style> 