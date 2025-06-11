<template>
  <div class="log-visualization">
    <el-card class="filter-card">
      <el-form
        :inline="true"
        :model="filterForm"
        class="filter-form"
      >
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item label="错误类型">
          <el-select
            v-model="filterForm.errorType"
            placeholder="选择错误类型"
            @change="handleErrorTypeChange"
          >
            <el-option
              label="系统错误"
              value="system"
            />
            <el-option
              label="业务错误"
              value="business"
            />
            <el-option
              label="网络错误"
              value="network"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务名称">
          <el-select
            v-model="filterForm.serviceName"
            placeholder="选择服务"
            @change="handleServiceChange"
          >
            <el-option
              label="认证服务"
              value="auth"
            />
            <el-option
              label="日志服务"
              value="log"
            />
            <el-option
              label="分析服务"
              value="analysis"
            />
            <el-option
              label="检测服务"
              value="detection"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row
      :gutter="20"
      class="chart-row"
    >
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户活跃度趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="userActivityOption"
              autoresize
            />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>操作类型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="operationTypeOption"
              autoresize
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row
      :gutter="20"
      class="chart-row"
    >
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>错误详情</span>
            </div>
          </template>
          <el-table
            :data="logStore.errorDetails"
            style="width: 100%"
          >
            <el-table-column
              prop="timestamp"
              label="时间"
              width="180"
            />
            <el-table-column
              prop="type"
              label="类型"
              width="120"
            />
            <el-table-column
              prop="message"
              label="错误信息"
            />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>服务健康状态</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="serviceHealthOption"
              autoresize
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useLogStore } from '@/stores/log'
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

const logStore = useLogStore()

const filterForm = ref({
  dateRange: [],
  errorType: '',
  serviceName: ''
})

// 用户活跃度图表配置
const userActivityOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: logStore.userActivityData.dates
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    data: logStore.userActivityData.counts,
    type: 'line',
    smooth: true
  }]
}))

// 操作类型分布图表配置
const operationTypeOption = computed(() => ({
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
    data: logStore.operationTypeData,
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
}))

// 服务健康状态图表配置
const serviceHealthOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'category',
    data: Object.keys(logStore.serviceHealth)
  },
  yAxis: {
    type: 'value',
    max: 100
  },
  series: [{
    data: Object.values(logStore.serviceHealth).map(health => health.availability),
    type: 'bar'
  }]
}))

const handleDateRangeChange = async (val) => {
  if (val) {
    await logStore.fetchUserActivityData(val[0], val[1])
    await logStore.fetchOperationTypeData(val[0], val[1])
  }
}

const handleErrorTypeChange = async (val) => {
  if (val && filterForm.value.dateRange) {
    await logStore.fetchErrorDetails(val, filterForm.value.dateRange[0], filterForm.value.dateRange[1])
  }
}

const handleServiceChange = async (val) => {
  if (val && filterForm.value.dateRange) {
    await logStore.fetchServiceHealth(val, filterForm.value.dateRange[0], filterForm.value.dateRange[1])
  }
}

onMounted(async () => {
  // 默认加载最近7天的数据
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 7)
  filterForm.value.dateRange = [start, end]
  
  await handleDateRangeChange(filterForm.value.dateRange)
})
</script>

<style scoped>
.log-visualization {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 