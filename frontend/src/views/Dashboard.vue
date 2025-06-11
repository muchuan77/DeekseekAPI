<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col
        v-for="(card, index) in cards"
        :key="index"
        :span="4"
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
              <div class="card-title">
                {{ card.title }}
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
              <span>谣言趋势</span>
              <el-radio-group
                v-model="trendTimeRange"
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
              :option="trendOption"
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
              <span>谣言类型分布</span>
            </div>
          </template>
          <div
            v-if="showCharts"
            class="chart-container"
          >
            <v-chart
              class="chart"
              :option="categoryOption"
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
      class="chart-row"
    >
      <el-col :span="12">
        <el-card
          v-loading="loading"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>传播分析</span>
            </div>
          </template>
          <div
            v-if="showCharts"
            class="chart-container"
          >
            <v-chart
              class="chart"
              :option="propagationOption"
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
              <span>谣言类型分布</span>
            </div>
          </template>
          <div
            v-if="showCharts"
            class="chart-container"
          >
            <v-chart
              class="chart"
              :option="categoryOption"
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
              <span>最近谣言</span>
              <el-button
                link
                @click="viewAllRumors"
              >
                查看全部
              </el-button>
            </div>
          </template>
          <el-table
            :data="rumorStore.rumors"
            style="width: 100%"
          >
            <el-table-column
              prop="id"
              label="ID"
              width="80"
            />
            <el-table-column
              prop="content"
              label="内容"
              show-overflow-tooltip
            />
            <el-table-column
              prop="source"
              label="来源"
              width="120"
            />
            <el-table-column
              prop="status"
              label="状态"
              width="100"
            >
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="createTime"
              label="时间"
              width="180"
            >
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
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
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { Document, QuestionFilled, Check, Close, Connection } from '@element-plus/icons-vue'
import { useRumorStore } from '@/stores/rumor'
import { usePropagationStore } from '@/stores/propagation'
import { formatDate } from '@/utils/date'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const router = useRouter()
const rumorStore = useRumorStore()
const propagationStore = usePropagationStore()
const trendTimeRange = ref('week')
const loading = ref(false)
const showCharts = ref(false)

// 添加本地状态管理
const dashboardData = ref({
  totalRumors: 0,
  pendingRumors: 0,
  verifiedRumors: 0,
  falseRumors: 0,
  trendData: {
    dates: [],
    counts: []
  },
  categoryData: [],
  propagationData: {
    dates: [],
    counts: []
  }
})

// 数据卡片
const cards = computed(() => [
  {
    title: '总谣言数',
    value: dashboardData.value.totalRumors,
    icon: Document,
    color: '#409EFF'
  },
  {
    title: '待验证',
    value: dashboardData.value.pendingRumors,
    icon: QuestionFilled,
    color: '#E6A23C'
  },
  {
    title: '已验证',
    value: dashboardData.value.verifiedRumors,
    icon: Check,
    color: '#67C23A'
  },
  {
    title: '已证伪',
    value: dashboardData.value.falseRumors,
    icon: Close,
    color: '#F56C6C'
  },
  {
    title: '传播分析',
    value: dashboardData.value.propagationData.counts.length,
    icon: Connection,
    color: '#9C27B0'
  }
])

// 获取仪表盘数据
const fetchDashboardData = async () => {
  loading.value = true
  showCharts.value = false
  try {
    // 获取谣言数据
    await rumorStore.fetchRumors({
      page: 0,
      size: 10
    })
    
    // 获取传播数据 - 使用第一个谣言ID
    const firstRumorId = rumorStore.rumors[0]?.id
    if (firstRumorId) {
      await propagationStore.getPropagationTrends(firstRumorId)
    }
    
    // 更新本地状态
    dashboardData.value = {
      totalRumors: rumorStore.total || 0,
      pendingRumors: rumorStore.rumors?.filter(r => r.status === 'PENDING').length || 0,
      verifiedRumors: rumorStore.rumors?.filter(r => r.status === 'VERIFIED').length || 0,
      falseRumors: rumorStore.rumors?.filter(r => r.status === 'FALSE').length || 0,
      trendData: {
        dates: rumorStore.rumors?.map(r => formatDate(r.createTime)) || [],
        counts: rumorStore.rumors?.map((_, i) => i + 1) || []
      },
      categoryData: Object.entries(
        (rumorStore.rumors || []).reduce((acc, r) => {
          acc[r.type] = (acc[r.type] || 0) + 1
          return acc
        }, {})
      ).map(([name, value]) => ({ name, value })),
      propagationData: propagationStore.trendData || { dates: [], counts: [] }
    }

    // 等待DOM更新
    await nextTick()
    
    // 检查是否有数据
    const hasData = dashboardData.value.trendData.dates.length > 0 ||
                   dashboardData.value.categoryData.length > 0 ||
                   dashboardData.value.propagationData.dates.length > 0

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

// 处理时间范围变化
const handleTimeRangeChange = async () => {
  await fetchDashboardData()
}

// 查看全部谣言
const viewAllRumors = () => {
  router.push('/rumors')
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'VERIFIED': 'success',
    'FALSE': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待验证',
    'VERIFIED': '已验证',
    'FALSE': '已证伪'
  }
  return texts[status] || '未知'
}

onMounted(async () => {
  await fetchDashboardData()
})

// 趋势图表配置
const trendOption = computed(() => {
  if (!showCharts.value) return {}
  const dates = dashboardData.value.trendData.dates || []
  const counts = dashboardData.value.trendData.counts || []
  const propagationCounts = dashboardData.value.propagationData.counts || []
  
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
      name: '谣言数量',
      itemStyle: {
        color: '#409EFF'
      }
    }, {
      data: propagationCounts.length > 0 ? propagationCounts : [0],
      type: 'line',
      smooth: true,
      name: '传播分析',
      itemStyle: {
        color: '#9C27B0'
      }
    }]
  }
})

// 分类图表配置
const categoryOption = computed(() => {
  if (!showCharts.value) return {}
  const data = dashboardData.value.categoryData || []
  
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

// 传播分析图表配置
const propagationOption = computed(() => {
  if (!showCharts.value) return {}
  const dates = dashboardData.value.propagationData.dates || []
  const counts = dashboardData.value.propagationData.counts || []
  
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
      name: '传播量',
      itemStyle: {
        color: '#9C27B0'
      }
    }]
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard-card {
  margin-bottom: 20px;
  height: 120px;
}

.card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.card-icon .el-icon {
  font-size: 20px;
  color: white;
}

.card-info {
  flex: 1;
  overflow: hidden;
}

.card-value {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-title {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.no-data {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}
</style> 