import { defineStore } from 'pinia'
import { getStatistics, getTrends } from '@/api/analytics'
import { ElMessage } from 'element-plus'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    overview: {},
    statistics: {},
    trends: [],
    alerts: [],
    loading: false,
    trendData: {
      dates: [],
      counts: []
    },
    categoryData: [],
    totalRumors: 0,
    pendingRumors: 0,
    verifiedRumors: 0,
    falseRumors: 0,
    recentRumors: []
  }),

  getters: {
    getOverview: (state) => state.overview,
    getStatistics: (state) => state.statistics,
    getTrends: (state) => state.trends,
    getAlerts: (state) => state.alerts
  },

  actions: {
    async fetchDashboardData(timeRange = 'week') {
      try {
        this.loading = true
        const [statistics, trends] = await Promise.all([
          getStatistics(timeRange),
          getTrends(timeRange)
        ])

        if (statistics.code === 200) {
          this.statistics = statistics.data
          this.totalRumors = statistics.data.totalRumors || 0
          this.pendingRumors = statistics.data.pendingRumors || 0
          this.verifiedRumors = statistics.data.verifiedRumors || 0
          this.falseRumors = statistics.data.fakeRumors || 0
        }

        if (trends.code === 200) {
          this.trends = trends.data
          this.trendData = {
            dates: trends.data.dailyStats.map(item => item.date),
            counts: trends.data.dailyStats.map(item => item.rumors)
          }
          this.categoryData = Object.entries(trends.data.typeDistribution).map(([name, value]) => ({
            name,
            value
          }))
        }
      } catch (error) {
        console.error('获取仪表盘数据失败:', error)
        ElMessage.error('获取仪表盘数据失败')
      } finally {
        this.loading = false
      }
    },

    async fetchStatistics(timeRange = 'week') {
      try {
        this.loading = true
        const response = await getStatistics(timeRange)
        if (response.code === 200) {
          this.statistics = response.data
          this.totalRumors = response.data.totalRumors || 0
          this.pendingRumors = response.data.pendingRumors || 0
          this.verifiedRumors = response.data.verifiedRumors || 0
          this.falseRumors = response.data.fakeRumors || 0
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        ElMessage.error('获取统计数据失败')
      } finally {
        this.loading = false
      }
    },

    async fetchTrends(timeRange = 'week') {
      try {
        this.loading = true
        const response = await getTrends(timeRange)
        if (response.code === 200) {
          this.trends = response.data
          this.trendData = {
            dates: response.data.dailyStats.map(item => item.date),
            counts: response.data.dailyStats.map(item => item.rumors)
          }
          this.categoryData = Object.entries(response.data.typeDistribution).map(([name, value]) => ({
            name,
            value
          }))
        }
      } catch (error) {
        console.error('获取趋势数据失败:', error)
        ElMessage.error('获取趋势数据失败')
      } finally {
        this.loading = false
      }
    }
  }
}) 