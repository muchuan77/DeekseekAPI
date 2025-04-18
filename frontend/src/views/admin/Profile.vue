<template>
  <div class="admin-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员信息</span>
        </div>
      </template>
      <el-form label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="profile.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profile.email" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="profile.role" disabled />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input v-model="profile.createdAt" disabled />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const profile = ref({
  username: '管理员',
  email: 'admin@example.com',
  role: '管理员',
  createdAt: '2024-01-01'
})
</script>

<style scoped>
.admin-profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 