<template>
  <div class="user-role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户角色管理</span>
        </div>
      </template>

      <el-table :data="users" style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="当前角色">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role" style="margin-right: 5px;">
              {{ role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditRoles(row)">
              编辑角色
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户角色"
      width="30%"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="roleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="roleForm.roles">
            <el-checkbox label="ADMIN">管理员</el-checkbox>
            <el-checkbox label="MODERATOR">审核员</el-checkbox>
            <el-checkbox label="USER">普通用户</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoles">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList } from '@/api/user'
import { getUserRoles, updateUserRoles } from '@/api/permission'

const users = ref([])
const dialogVisible = ref(false)
const roleForm = ref({
  userId: '',
  username: '',
  roles: []
})

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await getUserList()
    if (response.code === 200) {
      users.value = response.data
      // 获取每个用户的角色
      for (const user of users.value) {
        const roleResponse = await getUserRoles(user.id)
        if (roleResponse.code === 200) {
          user.roles = roleResponse.data.map(role => role.name)
        }
      }
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  }
}

// 打开编辑角色对话框
const handleEditRoles = (row) => {
  roleForm.value = {
    userId: row.id,
    username: row.username,
    roles: [...row.roles]
  }
  dialogVisible.value = true
}

// 提交角色修改
const submitRoles = async () => {
  try {
    const response = await updateUserRoles(roleForm.value.userId, roleForm.value.roles)
    if (response.code === 200) {
      ElMessage.success('角色更新成功')
      dialogVisible.value = false
      fetchUsers() // 刷新用户列表
    }
  } catch (error) {
    console.error('更新角色失败:', error)
    ElMessage.error('更新角色失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-role-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 