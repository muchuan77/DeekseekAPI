<template>
  <div class="trace-container">
    <el-card v-loading="propagationStore.loading">
      <template #header>
        <div class="card-header">
          <span>传播路径分析</span>
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="谣言ID">
          <el-input v-model="searchForm.rumorId" placeholder="请输入谣言ID" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 传播路径图 -->
      <div class="trace-chart">
        <v-chart class="chart" :option="traceOption" autoresize />
      </div>

      <!-- 传播数据表格 -->
      <el-table :data="propagationStore.traceData" style="width: 100%" class="trace-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="source" label="来源" width="120" />
        <el-table-column prop="target" label="目标" width="120" />
        <el-table-column prop="timestamp" label="时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" @click="viewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="propagationStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { GraphChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { usePropagationStore } from '@/stores/propagation'
import { formatDate } from '@/utils/date'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

use([
  CanvasRenderer,
  GraphChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const route = useRoute()
const propagationStore = usePropagationStore()

// 搜索表单
const searchForm = ref({
  rumorId: route.params.id || '',
  dateRange: []
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)

// 传播路径图配置
const traceOption = computed(() => ({
  title: {
    text: '传播路径图'
  },
  tooltip: {},
  animationDurationUpdate: 1500,
  animationEasingUpdate: 'quinticInOut',
  series: [{
    type: 'graph',
    layout: 'force',
    force: {
      repulsion: 100,
      edgeLength: 100
    },
    roam: true,
    label: {
      show: true
    },
    data: propagationStore.nodes,
    edges: propagationStore.edges
  }]
}))

// 获取类型标签
const getTypeTag = (type) => {
  const tags = {
    'forward': 'primary',
    'comment': 'success',
    'like': 'warning',
    'report': 'danger'
  }
  return tags[type] || 'info'
}

// 获取类型文本
const getTypeText = (type) => {
  const texts = {
    'forward': '转发',
    'comment': '评论',
    'like': '点赞',
    'report': '举报'
  }
  return texts[type] || '未知'
}

// 搜索
const handleSearch = async () => {
  currentPage.value = 1
  await loadData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    rumorId: '',
    dateRange: []
  }
  handleSearch()
}

// 刷新数据
const refreshData = async () => {
  await loadData()
}

// 查看详情
const viewDetail = (row) => {
  // TODO: 实现查看详情功能
  console.log('查看详情:', row)
}

// 加载数据
const loadData = async () => {
  try {
    if (!searchForm.value.rumorId) {
      ElMessage.warning('请输入谣言ID')
      return
    }
    
    // 验证日期范围
    if (searchForm.value.dateRange?.length === 2) {
      const [start, end] = searchForm.value.dateRange
      if (new Date(start) > new Date(end)) {
        ElMessage.warning('结束日期不能早于开始日期')
        return
      }
    }
    
    await propagationStore.fetchTraceData({
      rumorId: searchForm.value.rumorId,
      startDate: searchForm.value.dateRange[0],
      endDate: searchForm.value.dateRange[1],
      page: currentPage.value,
      pageSize: pageSize.value
    })
  } catch (error) {
    // 错误已经在store中处理，这里只做日志记录
    console.error('加载数据失败:', error)
  }
}

// 处理分页大小变化
const handleSizeChange = async (val) => {
  if (val < 1 || val > 100) {
    ElMessage.warning('每页显示数量必须在1-100之间')
    return
  }
  pageSize.value = val
  await loadData()
}

// 处理页码变化
const handleCurrentChange = async (val) => {
  if (val < 1) {
    ElMessage.warning('页码不能小于1')
    return
  }
  currentPage.value = val
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.trace-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.trace-chart {
  height: 500px;
  margin-bottom: 20px;
}

.trace-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style> 