<template>
  <div class="system-log">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
        </div>
      </template>

      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
      >
        <el-form-item label="日志级别">
          <el-select
            v-model="searchForm.level"
            placeholder="请选择日志级别"
          >
            <el-option
              label="全部"
              value=""
            />
            <el-option
              label="INFO"
              value="INFO"
            />
            <el-option
              label="WARN"
              value="WARN"
            />
            <el-option
              label="ERROR"
              value="ERROR"
            />
          </el-select>
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
          prop="level"
          label="级别"
          width="100"
        >
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="logger"
          label="来源"
          width="200"
        />
        <el-table-column
          prop="message"
          label="内容"
        />
        <el-table-column
          prop="exception"
          label="异常信息"
          width="200"
          show-overflow-tooltip
        />
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
  name: 'SystemLog',
  setup() {
    const logStore = useLogStore()
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const searchForm = reactive({
      level: '',
      dateRange: []
    })

    const fetchLogs = async () => {
      loading.value = true
      try {
        await logStore.fetchSystemLogs({
          page: currentPage.value - 1,
          size: pageSize.value,
          level: searchForm.level,
          startTime: searchForm.dateRange?.[0],
          endTime: searchForm.dateRange?.[1]
        })
        total.value = logStore.systemLogs.length
      } catch (error) {
        ElMessage.error('获取系统日志失败')
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
      searchForm.level = ''
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

    const getLevelType = (level) => {
      switch (level) {
        case 'ERROR':
          return 'danger'
        case 'WARN':
          return 'warning'
        case 'INFO':
          return 'info'
        default:
          return ''
      }
    }

    onMounted(() => {
      fetchLogs()
    })

    return {
      loading,
      logs: computed(() => logStore.systemLogs),
      currentPage,
      pageSize,
      total,
      searchForm,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      formatDate,
      getLevelType
    }
  }
}
</script>

<style scoped>
.system-log {
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