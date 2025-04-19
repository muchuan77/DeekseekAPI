<template>
  <div class="propagation-analysis">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>传播分析</span>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="statistics-card">
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
          <el-card class="influence-card">
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
          <el-card class="paths-card">
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
            <div v-if="paths.length > 0">
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
          <el-card class="key-nodes-card">
            <template #header>
              <div class="card-header">
                <span>关键节点</span>
              </div>
            </template>
            <div v-if="keyNodes.length > 0">
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
          <el-card class="trends-card">
            <template #header>
              <div class="card-header">
                <span>传播趋势</span>
              </div>
            </template>
            <div v-if="trends.length > 0" class="trends-chart">
              <!-- 这里可以集成图表组件，如 ECharts -->
              <div class="chart-placeholder">传播趋势图表</div>
            </div>
            <el-empty v-else description="暂无传播趋势数据" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import {
  getPropagationPaths,
  getInfluenceAnalysis,
  getPropagationTrends,
  getKeyNodes,
  getPropagationStatistics
} from '@/api/propagation'

const route = useRoute()
const rumorId = route.params.id

const pathType = ref('ALL')
const paths = ref([])
const influence = ref(null)
const trends = ref([])
const keyNodes = ref([])
const statistics = ref(null)

const loadData = async () => {
  try {
    const [pathsRes, influenceRes, trendsRes, keyNodesRes, statisticsRes] = await Promise.all([
      getPropagationPaths(rumorId, { type: pathType.value }),
      getInfluenceAnalysis(rumorId),
      getPropagationTrends(rumorId),
      getKeyNodes(rumorId),
      getPropagationStatistics(rumorId)
    ])

    paths.value = pathsRes.data.paths
    influence.value = influenceRes.data.analysis
    trends.value = trendsRes.data
    keyNodes.value = keyNodesRes.data
    statistics.value = statisticsRes.data
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleRefresh = () => {
  loadData()
}

onMounted(() => {
  loadData()
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
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.chart-placeholder {
  color: #909399;
  font-size: 14px;
}
</style> 