<template>
  <div class="analytics">
    <el-row :gutter="20">
      <!-- 数据概览卡片 -->
      <el-col
        v-for="(item, index) in overviewData"
        :key="index"
        :span="6"
      >
        <el-card
          shadow="hover"
          class="overview-card"
        >
          <div class="overview-content">
            <div
              class="overview-icon"
              :style="{ backgroundColor: item.color }"
            >
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-title">
                {{ item.title }}
              </div>
              <div class="overview-value">
                {{ item.value }}
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
      <!-- 谣言类型分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>谣言类型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="typeChartOption"
              autoresize
            />
          </div>
        </el-card>
      </el-col>

      <!-- 谣言趋势分析 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>谣言趋势分析</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="trendChartOption"
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
      <!-- 用户活跃度 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户活跃度</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="userActivityChartOption"
              autoresize
            />
          </div>
        </el-card>
      </el-col>

      <!-- 评论情感分析 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>评论情感分析</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart
              class="chart"
              :option="sentimentChartOption"
              autoresize
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const overviewData = ref([
  {
    title: '总谣言数',
    value: '0',
    icon: 'Warning',
    color: '#409EFF'
  },
  {
    title: '今日新增',
    value: '0',
    icon: 'Plus',
    color: '#67C23A'
  },
  {
    title: '总评论数',
    value: '0',
    icon: 'ChatDotRound',
    color: '#E6A23C'
  },
  {
    title: '活跃用户',
    value: '0',
    icon: 'User',
    color: '#F56C6C'
  }
])

// Chart options (used in template by v-chart)
const typeChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 10,
    data: []
  },
  series: [
    {
      name: '谣言类型',
      type: 'pie',
      radius: ['50%', '70%'],
      avoidLabelOverlap: false,
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: []
    }
  ]
})

const trendChartOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['新增谣言', '新增评论']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '新增谣言',
      type: 'line',
      data: []
    },
    {
      name: '新增评论',
      type: 'line',
      data: []
    }
  ]
})

const userActivityChartOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  legend: {
    data: ['活跃用户']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '活跃用户',
      type: 'bar',
      data: []
    }
  ]
})

const sentimentChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 10,
    data: ['正面', '中性', '负面']
  },
  series: [
    {
      name: '评论情感',
      type: 'pie',
      radius: ['50%', '70%'],
      avoidLabelOverlap: false,
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 0, name: '正面' },
        { value: 0, name: '中性' },
        { value: 0, name: '负面' }
      ]
    }
  ]
})

// Commented out fetchData
// const fetchData = async () => { ... }

// initCharts is unused
// const initCharts = () => {
//   console.log("Chart initialization logic (potentially unused if v-chart handles options)")
// }

onMounted(() => {
  console.log("Analytics component mounted. Using static options for charts.")
  // initCharts() // Call remains commented out
})
</script>

<style scoped>
.analytics {
  padding: 20px;
}

.overview-card {
  margin-bottom: 20px;
}

.overview-content {
  display: flex;
  align-items: center;
}

.overview-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.overview-icon .el-icon {
  font-size: 24px;
  color: white;
}

.overview-info {
  flex: 1;
}

.overview-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.overview-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}
</style> 