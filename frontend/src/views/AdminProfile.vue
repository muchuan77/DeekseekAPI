<template>
  <div class="admin-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button 
            type="primary" 
            @click="handleAdd"
            v-if="userStore.hasPermission('admin:create')"
          >
            添加管理员
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              type="text" 
              @click="handleEdit(row)"
              v-if="userStore.hasPermission('admin:update')"
            >
              编辑
            </el-button>
            <el-button 
              type="text" 
              @click="handleDelete(row)"
              v-if="userStore.hasPermission('admin:delete')"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const tableData = ref([])

const handleAdd = () => {
  ElMessage.info('添加管理员功能待实现')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑管理员 ${row.username} 功能待实现`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除管理员 ${row.username} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

onMounted(() => {
  // 检查权限
  if (!userStore.hasPermission('admin:view')) {
    ElMessage.error('您没有权限访问此页面')
    return
  }
  
  // 模拟获取管理员列表
  tableData.value = [
    { username: 'admin1', email: 'admin1@example.com', role: '超级管理员' },
    { username: 'admin2', email: 'admin2@example.com', role: '普通管理员' }
  ]
})
</script>

<style scoped>
.admin-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 