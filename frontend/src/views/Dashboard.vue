<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(card, index) in cards" :key="index">
        <el-card shadow="hover" class="dashboard-card" v-loading="dashboardStore.loading">
          <div class="card-content">
            <div class="card-icon" :style="{ backgroundColor: card.color }">
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ card.value }}</div>
              <div class="card-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" v-loading="dashboardStore.loading">
          <template #header>
            <div class="card-header">
              <span>谣言趋势</span>
              <el-radio-group v-model="trendTimeRange" size="small" @change="handleTimeRangeChange">
                <el-radio-button value="week">本周</el-radio-button>
                <el-radio-button value="month">本月</el-radio-button>
                <el-radio-button value="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="trendOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" v-loading="dashboardStore.loading">
          <template #header>
            <div class="card-header">
              <span>谣言类型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="categoryOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card shadow="hover" v-loading="dashboardStore.loading">
          <template #header>
            <div class="card-header">
              <span>最近谣言</span>
              <el-button link @click="viewAllRumors">查看全部</el-button>
            </div>
          </template>
          <el-table :data="dashboardStore.recentRumors" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="source" label="来源" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="timestamp" label="时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.timestamp) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button link @click="viewDetail(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
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
import { Document, QuestionFilled, Check, Close } from '@element-plus/icons-vue'
import { useDashboardStore } from '@/stores/dashboard'
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

const router = useRouter()
const dashboardStore = useDashboardStore()
const trendTimeRange = ref('week')

// 数据卡片
const cards = computed(() => [
  {
    title: '总谣言数',
    value: dashboardStore.totalRumors,
    icon: Document,
    color: '#409EFF'
  },
  {
    title: '待验证',
    value: dashboardStore.pendingRumors,
    icon: QuestionFilled,
    color: '#E6A23C'
  },
  {
    title: '已验证',
    value: dashboardStore.verifiedRumors,
    icon: Check,
    color: '#67C23A'
  },
  {
    title: '已证伪',
    value: dashboardStore.falseRumors,
    icon: Close,
    color: '#F56C6C'
  }
])

// 趋势图表配置
const trendOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: dashboardStore.trendData.dates || []
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    data: dashboardStore.trendData.counts || [],
    type: 'line',
    smooth: true
  }]
}))

// 分类图表配置
const categoryOption = computed(() => ({
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
    data: dashboardStore.categoryData || [],
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
}))

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    '待验证': 'warning',
    '已验证': 'success',
    '已证伪': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    '待验证': '待验证',
    '已验证': '已验证',
    '已证伪': '已证伪'
  }
  return texts[status] || '未知'
}

// 处理时间范围变化
const handleTimeRangeChange = async () => {
  await dashboardStore.fetchDashboardData(trendTimeRange.value)
}

// 查看全部谣言
const viewAllRumors = () => {
  router.push('/rumors')
}

// 查看详情
const viewDetail = (row) => {
  router.push(`/rumors/${row.id}`)
}

onMounted(async () => {
  await dashboardStore.fetchDashboardData(trendTimeRange.value)
})
</script>

<style scoped>
.dashboard {
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
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.card-icon .el-icon {
  font-size: 24px;
  color: white;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.card-title {
  font-size: 14px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.table-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 