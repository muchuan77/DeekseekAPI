<template>
  <div class="log-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>日志管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作人">
          <el-input v-model="searchForm.username" placeholder="请输入操作人" clearable />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择操作类型" clearable>
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
            <el-option label="创建" value="CREATE" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 日志列表 -->
      <el-table
        v-loading="loading"
        :data="logs"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationDesc" label="操作描述" min-width="200" />
        <el-table-column prop="ip" label="IP地址" width="150" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="50%"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(currentLog.operationType)">
            {{ getOperationTypeText(currentLog.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ currentLog.operationDesc }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre>{{ formatJson(currentLog.requestParams) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果">
          <pre>{{ formatJson(currentLog.responseResult) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// import { getLogs, deleteLog } from '@/api/log'
// import { ElMessage } from 'element-plus'

const logs = ref([
  // Add mock data
  { id: 1, timestamp: '2024-07-28 12:00:00', level: 'INFO', user: 'admin', action: '登录系统', details: 'IP: 192.168.1.1' },
  { id: 2, timestamp: '2024-07-28 12:01:00', level: 'WARN', user: 'system', action: '缓存清理', details: '清理了 50MB 缓存' },
  { id: 3, timestamp: '2024-07-28 12:02:00', level: 'ERROR', user: 'user1', action: '提交检测失败', details: 'API 连接超时' },
]);
const total = ref(logs.value.length);
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
// const searchParams = ref({ level: '', user: '', action: '', dateRange: [] })
const detailDialogVisible = ref(false)
const currentLog = ref({})

const searchForm = ref({
  username: '',
  operationType: '',
  timeRange: []
})

onMounted(() => {
  console.log("Log management mounted, using mock data.")
})

const handleSearch = () => {
  currentPage.value = 1
  console.log("Skipping search API call. Search form:", searchForm.value)
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  console.log("Skipping fetch on size change.")
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  console.log("Skipping fetch on current change.")
}

// 查看详情 - Modify to use existing row data or mock details
const handleViewDetail = (row) => {
  console.log(`Showing details for log ID: ${row.id}`) 
  // Use the row data directly or create mock details
  currentLog.value = {
    ...row, // Spread existing row data
    requestParams: JSON.stringify({ mockParam: true }), // Mock details
    responseResult: JSON.stringify({ mockResult: 'Success' }) // Mock details
  };
  detailDialogVisible.value = true
}

// 格式化JSON
const formatJson = (json) => {
  try {
    return JSON.stringify(JSON.parse(json), null, 2)
  } catch (e) {
    return json
  }
}

// 获取操作类型标签样式
const getOperationTypeTag = (type) => {
  const map = {
    LOGIN: 'success',
    LOGOUT: 'info',
    CREATE: 'primary',
    UPDATE: 'warning',
    DELETE: 'danger'
  }
  return map[type] || 'info'
}

// 获取操作类型文本
const getOperationTypeText = (type) => {
  const map = {
    LOGIN: '登录',
    LOGOUT: '登出',
    CREATE: '创建',
    UPDATE: '更新',
    DELETE: '删除'
  }
  return map[type] || type
}
</script>

<style scoped>
.log-management {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style> 