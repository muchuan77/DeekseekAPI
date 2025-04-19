import { defineStore } from 'pinia'
import * as propagationApi from '@/api/propagation'
import { ElMessage } from 'element-plus'

export const usePropagationStore = defineStore('propagation', {
  state: () => ({
    propagationData: [],
    analysisResults: {},
    loading: false,
    totalData: 0,
    traceData: [], // 传播路径数据
    nodes: [], // 图表节点数据
    edges: [], // 图表边数据
    total: 0, // 总数据量
    error: null
  }),

  getters: {
    getPropagationData: (state) => state.propagationData,
    getAnalysisResults: (state) => state.analysisResults
  },

  actions: {
    async fetchPropagationData(params = {}) {
      try {
        this.loading = true
        const response = await propagationApi.getPropagationData(params)
        if (response.code === 200) {
          this.propagationData = response.data.content
          this.totalData = response.data.totalElements
        }
      } catch (error) {
        console.error('获取传播数据失败:', error)
        ElMessage.error('获取传播数据失败')
      } finally {
        this.loading = false
      }
    },

    async analyzePropagation(params = {}) {
      try {
        this.loading = true
        const response = await propagationApi.analyzePropagation(params)
        if (response.code === 200) {
          this.analysisResults = response.data
          return true
        }
        return false
      } catch (error) {
        console.error('传播分析失败:', error)
        ElMessage.error('传播分析失败')
        return false
      } finally {
        this.loading = false
      }
    },

    async exportPropagationData(params = {}) {
      try {
        const response = await propagationApi.exportPropagationData(params)
        if (response.code === 200) {
          ElMessage.success('导出传播数据成功')
          return true
        }
        return false
      } catch (error) {
        console.error('导出传播数据失败:', error)
        ElMessage.error('导出传播数据失败')
        return false
      }
    },

    async getPropagationTrends(params = {}) {
      try {
        this.loading = true
        const response = await propagationApi.getPropagationTrends(params)
        if (response.code === 200) {
          return response.data
        }
        return null
      } catch (error) {
        console.error('获取传播趋势失败:', error)
        ElMessage.error('获取传播趋势失败')
        return null
      } finally {
        this.loading = false
      }
    },

    // 获取传播路径数据
    async fetchTraceData(params) {
      try {
        this.loading = true
        this.error = null
        
        const response = await propagationApi.getPropagationPaths(params.rumorId, {
          startDate: params.startDate,
          endDate: params.endDate,
          page: params.page,
          pageSize: params.pageSize
        })
        
        if (response.code === 200) {
          this.traceData = response.data.content || []
          this.total = response.data.totalElements || 0
          this.processChartData(response.data.content)
        } else {
          throw new Error(response.message || '获取传播路径数据失败')
        }
      } catch (error) {
        this.error = error
        console.error('获取传播路径数据失败:', error)
        ElMessage.error(error.message || '获取传播路径数据失败')
        throw error
      } finally {
        this.loading = false
      }
    },

    // 处理图表数据
    processChartData(data) {
      if (!data || !Array.isArray(data)) {
        this.nodes = []
        this.edges = []
        return
      }
      
      try {
        const nodeMap = new Map()
        const edges = []
        
        data.forEach(item => {
          // 处理源节点
          if (!nodeMap.has(item.source)) {
            nodeMap.set(item.source, {
              id: item.source,
              name: item.source,
              symbolSize: 30
            })
          }
          
          // 处理目标节点
          if (!nodeMap.has(item.target)) {
            nodeMap.set(item.target, {
              id: item.target,
              name: item.target,
              symbolSize: 30
            })
          }
          
          // 添加边
          edges.push({
            source: item.source,
            target: item.target,
            label: {
              show: true,
              formatter: getTypeText(item.type)
            }
          })
        })
        
        this.nodes = Array.from(nodeMap.values())
        this.edges = edges
      } catch (error) {
        console.error('处理图表数据失败:', error)
        this.nodes = []
        this.edges = []
      }
    }
  }
})

// 获取类型文本
function getTypeText(type) {
  const texts = {
    'forward': '转发',
    'comment': '评论',
    'like': '点赞',
    'report': '举报'
  }
  return texts[type] || '未知'
} 