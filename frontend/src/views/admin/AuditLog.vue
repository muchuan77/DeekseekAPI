<template>
  <div class="audit-log">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审计日志</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="审计类型">
          <el-select v-model="searchForm.auditType" placeholder="请选择审计类型">
            <el-option label="全部" value="" />
            <el-option label="认证" value="AUTHENTICATION" />
            <el-option label="授权" value="AUTHORIZATION" />
            <el-option label="数据访问" value="DATA_ACCESS" />
            <el-option label="配置变更" value="CONFIG_CHANGE" />
            <el-option label="安全事件" value="SECURITY_EVENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" />
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

      <el-table :data="logs" style="width: 100%" v-loading="loading">
        <el-table-column prop="timestamp" label="时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="auditType" label="审计类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getAuditType(row.auditType)">
              {{ getAuditTypeText(row.auditType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resource" label="资源" width="150" />
        <el-table-column prop="action" label="操作" width="120" />
        <el-table-column prop="details" label="详细信息" />
        <el-table-column prop="ip" label="IP地址" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">
              {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { format } from 'date-fns'

export default {
  name: 'AuditLog',
  setup() {
    const loading = ref(false)
    const logs = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const searchForm = reactive({
      auditType: '',
      username: '',
      dateRange: []
    })

    const fetchLogs = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/logs/audit', {
          params: {
            page: currentPage.value - 1,
            size: pageSize.value,
            auditType: searchForm.auditType,
            username: searchForm.username,
            startDate: searchForm.dateRange?.[0],
            endDate: searchForm.dateRange?.[1]
          }
        })
        logs.value = response.data.data.content
        total.value = response.data.data.totalElements
      } catch (error) {
        ElMessage.error('获取审计日志失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      fetchLogs()
    }

    const resetSearch = () => {
      searchForm.auditType = ''
      searchForm.username = ''
      searchForm.dateRange = []
      handleSearch()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchLogs()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchLogs()
    }

    const formatDate = (timestamp) => {
      return format(new Date(timestamp), 'yyyy-MM-dd HH:mm:ss')
    }

    const getAuditType = (type) => {
      switch (type) {
        case 'AUTHENTICATION':
          return 'primary'
        case 'AUTHORIZATION':
          return 'warning'
        case 'DATA_ACCESS':
          return 'info'
        case 'CONFIG_CHANGE':
          return 'success'
        case 'SECURITY_EVENT':
          return 'danger'
        default:
          return ''
      }
    }

    const getAuditTypeText = (type) => {
      switch (type) {
        case 'AUTHENTICATION':
          return '认证'
        case 'AUTHORIZATION':
          return '授权'
        case 'DATA_ACCESS':
          return '数据访问'
        case 'CONFIG_CHANGE':
          return '配置变更'
        case 'SECURITY_EVENT':
          return '安全事件'
        default:
          return type
      }
    }

    onMounted(() => {
      fetchLogs()
    })

    return {
      loading,
      logs,
      currentPage,
      pageSize,
      total,
      searchForm,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      formatDate,
      getAuditType,
      getAuditTypeText
    }
  }
}
</script>

<style scoped>
.audit-log {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 