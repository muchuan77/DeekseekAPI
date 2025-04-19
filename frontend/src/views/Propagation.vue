<template>
  <div class="propagation-analysis">
    <el-card class="box-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>传播分析</span>
          <el-button type="primary" @click="handleRefresh" :loading="refreshing">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="statistics-card" v-loading="loading">
            <template #header>
              <div class="card-header">
                <span>传播统计</span>
              </div>
            </template>
            <div v-if="statistics">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="总传播路径数">
                  {{ statistics.totalPaths }}
                </el-descriptions-item>
                <el-descriptions-item label="平均传播路径长度">
                  {{ statistics.averagePathLength }}
                </el-descriptions-item>
                <el-descriptions-item label="最大传播路径长度">
                  {{ statistics.maxPathLength }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="influence-card" v-loading="loading">
            <template #header>
              <div class="card-header">
                <span>影响力分析</span>
              </div>
            </template>
            <div v-if="influence">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="总影响力">
                  {{ influence.totalInfluence }}
                </el-descriptions-item>
                <el-descriptions-item label="关键用户数">
                  {{ influence.userInfluence.length }}
                </el-descriptions-item>
                <el-descriptions-item label="关键内容数">
                  {{ influence.contentInfluence.length }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card class="paths-card" v-loading="loading">
            <template #header>
              <div class="card-header">
                <span>传播路径</span>
                <el-select v-model="pathType" placeholder="传播类型" style="width: 120px">
                  <el-option label="全部" value="ALL" />
                  <el-option label="直接传播" value="DIRECT" />
                  <el-option label="间接传播" value="INDIRECT" />
                </el-select>
              </div>
            </template>
            <div v-if="paths && paths.length > 0">
              <el-table :data="paths" style="width: 100%">
                <el-table-column prop="sourceNode" label="源节点" />
                <el-table-column prop="targetNode" label="目标节点" />
                <el-table-column prop="propagationTime" label="传播时间" />
                <el-table-column prop="pathLength" label="路径长度" />
                <el-table-column prop="type" label="传播类型" />
              </el-table>
            </div>
            <el-empty v-else description="暂无传播路径数据" />
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card class="key-nodes-card" v-loading="loading">
            <template #header>
              <div class="card-header">
                <span>关键节点</span>
              </div>
            </template>
            <div v-if="keyNodes && keyNodes.length > 0">
              <el-table :data="keyNodes" style="width: 100%">
                <el-table-column prop="nodeId" label="节点ID" />
                <el-table-column prop="influence" label="影响力" />
                <el-table-column prop="pathCount" label="路径数" />
              </el-table>
            </div>
            <el-empty v-else description="暂无关键节点数据" />
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="trends-card" v-loading="loading">
            <template #header>
              <div class="card-header">
                <span>传播趋势</span>
                <el-radio-group v-model="trendType" size="small">
                  <el-radio-button label="hour">按小时</el-radio-button>
                  <el-radio-button label="day">按天</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div v-if="trends && trends.length > 0" class="trends-chart" ref="trendsChart"></div>
            <el-empty v-else description="暂无传播趋势数据" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import {
  getPropagationPaths,
  getInfluenceAnalysis,
  getPropagationTrends,
  getKeyNodes,
  getPropagationStatistics
} from '@/api/propagation'

const route = useRoute()
const rumorId = ref(route.params?.id || '')
const trendsChart = ref(null)
let chartInstance = null

const loading = ref(false)
const refreshing = ref(false)
const trendType = ref('hour')
const pathType = ref('ALL')
const paths = ref([])
const influence = ref(null)
const trends = ref([])
const keyNodes = ref([])
const statistics = ref(null)

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    rumorId.value = newId
    loadData()
  }
})

const loadData = async () => {
  if (!rumorId.value) {
    ElMessage.error('请先选择要分析的谣言')
    return
  }

  try {
    loading.value = true
    const [pathsRes, influenceRes, trendsRes, keyNodesRes, statisticsRes] = await Promise.all([
      getPropagationPaths(rumorId.value, { type: pathType.value }),
      getInfluenceAnalysis(rumorId.value),
      getPropagationTrends(rumorId.value, { type: trendType.value }),
      getKeyNodes(rumorId.value),
      getPropagationStatistics(rumorId.value)
    ])

    if (pathsRes.code !== 200) throw new Error(pathsRes.message || '获取传播路径失败')
    if (influenceRes.code !== 200) throw new Error(influenceRes.message || '获取影响力分析失败')
    if (trendsRes.code !== 200) throw new Error(trendsRes.message || '获取传播趋势失败')
    if (keyNodesRes.code !== 200) throw new Error(keyNodesRes.message || '获取关键节点失败')
    if (statisticsRes.code !== 200) throw new Error(statisticsRes.message || '获取统计信息失败')

    paths.value = pathsRes.data?.paths || []
    influence.value = influenceRes.data?.analysis || null
    trends.value = trendsRes.data || []
    keyNodes.value = keyNodesRes.data || []
    statistics.value = statisticsRes.data || null
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error(error.message || '加载数据失败，请稍后重试')
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const handleRefresh = () => {
  refreshing.value = true
  loadData()
}

const initTrendsChart = () => {
  if (!trendsChart.value) return
  
  chartInstance = echarts.init(trendsChart.value)
  const option = {
    title: {
      text: trendType.value === 'hour' ? '24小时传播趋势' : '传播趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        return `${data.name}<br/>传播数量: ${data.value}`
      }
    },
    toolbox: {
      feature: {
        saveAsImage: { title: '保存图片' },
        dataZoom: { title: { zoom: '区域缩放', back: '还原' } },
        restore: { title: '还原' }
      }
    },
    xAxis: {
      type: 'category',
      data: trends.value.map(item => item.time),
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '传播数量'
    },
    series: [{
      name: '传播数量',
      type: 'line',
      data: trends.value.map(item => item.count),
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      itemStyle: {
        color: '#409EFF'
      },
      markPoint: {
        data: [
          { type: 'max', name: '最大值' },
          { type: 'min', name: '最小值' }
        ]
      },
      markLine: {
        data: [{ type: 'average', name: '平均值' }]
      }
    }]
  }
  
  chartInstance.setOption(option)
}

watch([trends, trendType], () => {
  if (trends.value.length > 0) {
    nextTick(() => {
      initTrendsChart()
    })
  }
})

// 初始化加载
onMounted(() => {
  if (rumorId.value) {
    loadData()
  }
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', () => {
    chartInstance?.resize()
  })
  chartInstance?.dispose()
})
</script>

<style scoped>
.propagation-analysis {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-card,
.influence-card,
.paths-card,
.key-nodes-card,
.trends-card {
  margin-bottom: 20px;
}

.trends-chart {
  height: 300px;
  width: 100%;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 