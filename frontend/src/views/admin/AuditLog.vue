<template>
  <div class="audit-log">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审计日志</span>
        </div>
      </template>

      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
      >
        <el-form-item label="审计类型">
          <el-select
            v-model="searchForm.auditType"
            placeholder="请选择审计类型"
          >
            <el-option
              label="全部"
              value=""
            />
            <el-option
              label="认证"
              value="AUTHENTICATION"
            />
            <el-option
              label="授权"
              value="AUTHORIZATION"
            />
            <el-option
              label="数据访问"
              value="DATA_ACCESS"
            />
            <el-option
              label="配置变更"
              value="CONFIG_CHANGE"
            />
            <el-option
              label="安全事件"
              value="SECURITY_EVENT"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
          />
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
          <el-button
            type="primary"
            @click="handleSearch"
          >
            搜索
          </el-button>
          <el-button @click="resetSearch">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="logs"
        style="width: 100%"
      >
        <el-table-column
          prop="timestamp"
          label="时间"
          width="180"
        >
          <template #default="{ row }">
            {{ formatDate(row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="username"
          label="用户名"
          width="120"
        />
        <el-table-column
          prop="auditType"
          label="审计类型"
          width="120"
        >
          <template #default="{ row }">
            <el-tag :type="getAuditType(row.auditType)">
              {{ getAuditTypeText(row.auditType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="resource"
          label="资源"
          width="150"
        />
        <el-table-column
          prop="action"
          label="操作"
          width="120"
        />
        <el-table-column
          prop="details"
          label="详细信息"
        />
        <el-table-column
          prop="ip"
          label="IP地址"
          width="150"
        />
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useLogStore } from '@/stores/log'
import { format } from 'date-fns'

export default {
  name: 'AuditLog',
  setup() {
    const logStore = useLogStore()
    const loading = ref(false)
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
        await logStore.fetchAuditLogs({
          page: currentPage.value - 1,
          size: pageSize.value,
          auditType: searchForm.auditType,
          username: searchForm.username,
          startTime: searchForm.dateRange?.[0],
          endTime: searchForm.dateRange?.[1]
        })
        total.value = logStore.auditLogs.length
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
        case 'LOGIN':
          return 'success'
        case 'LOGOUT':
          return 'info'
        case 'CREATE':
          return 'primary'
        case 'UPDATE':
          return 'warning'
        case 'DELETE':
          return 'danger'
        default:
          return ''
      }
    }

    const getAuditTypeText = (type) => {
      switch (type) {
        case 'LOGIN':
          return '登录'
        case 'LOGOUT':
          return '登出'
        case 'CREATE':
          return '创建'
        case 'UPDATE':
          return '更新'
        case 'DELETE':
          return '删除'
        default:
          return type
      }
    }

    onMounted(() => {
      fetchLogs()
    })

    return {
      loading,
      logs: computed(() => logStore.auditLogs),
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

/* 添加下拉框样式 */
:deep(.el-select) {
  width: 200px;
}

:deep(.el-select-dropdown__item) {
  white-space: normal;
  height: auto;
  padding: 8px 10px;
  line-height: 1.5;
}

:deep(.el-select-dropdown__item span) {
  display: inline-block;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-select-dropdown__item.selected) {
  color: #409EFF;
  font-weight: bold;
}
</style> 