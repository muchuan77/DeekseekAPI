<template>
  <el-container class="admin-layout-container">
    <el-aside width="200px">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Logo">
        <span>管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><el-icon-s-home /></el-icon>
          <span>管理控制台</span>
        </el-menu-item>
        <el-menu-item v-if="hasRole('ADMIN')" index="/admin/users">
          <el-icon><el-icon-user-filled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasRole('ADMIN')" index="/admin/user-roles">
          <el-icon><el-icon-user /></el-icon>
          <span>用户角色管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasRole('ADMIN') || hasRole('MODERATOR')" index="/admin/comments">
          <el-icon><el-icon-chat-dot-round /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        <el-sub-menu v-if="hasRole('ADMIN')" index="/admin/logs">
          <template #title>
            <el-icon><el-icon-document-copy /></el-icon>
            <span>日志管理</span>
          </template>
          <el-menu-item index="/admin/logs">
            <el-icon><el-icon-document-copy /></el-icon>
            <span>日志管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/system-logs">
            <el-icon><el-icon-monitor /></el-icon>
            <span>系统日志</span>
          </el-menu-item>
          <el-menu-item index="/admin/operation-logs">
            <el-icon><el-icon-operation /></el-icon>
            <span>操作日志</span>
          </el-menu-item>
          <el-menu-item index="/admin/audit-logs">
            <el-icon><el-icon-view /></el-icon>
            <span>审计日志</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-if="hasRole('ADMIN')" index="/admin/analytics">
          <el-icon><el-icon-data-analysis /></el-icon>
          <span>数据分析</span>
        </el-menu-item>
        <el-menu-item v-if="hasRole('ADMIN')" index="/admin/ai-management">
          <el-icon><el-icon-data-analysis /></el-icon>
          <span>AI管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasRole('ADMIN')" index="/admin/settings">
          <el-icon><el-icon-setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
        <el-menu-item index="/admin/profile">
          <el-icon><el-icon-user /></el-icon>
          <span>管理员信息</span>
        </el-menu-item>
        <!-- Link back to the main application -->
        <el-divider></el-divider>
        <el-menu-item index="/">
          <el-icon><el-icon-back /></el-icon>
          <span>返回主系统</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              {{ username }}
              <el-icon><el-icon-arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">管理员信息</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElDivider } from 'element-plus' 
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const userInfo = computed(() => authStore.userInfo)

const username = computed(() => {
  if (hasRole('ADMIN')) {
    return '管理员'
  } else if (hasRole('MODERATOR')) {
    return '审核员'
  }
  return userInfo.value?.username || '用户'
})

const hasRole = (role) => {
  return userInfo.value?.roles?.includes(role) || false
}

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route.meta?.title || '页面')

const handleProfile = () => {
  router.push('/admin/profile')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    localStorage.clear()
    sessionStorage.clear()
    router.push('/login')
    window.location.reload()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}
</script>

<style scoped>
/* Styles are similar to Layout.vue, but potentially with admin-specific tweaks */
.admin-layout-container {
  height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #2b2f3a;
  color: #fff;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.el-menu-vertical {
  height: calc(100vh - 60px);
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-divider {
  margin: 10px 0;
}
</style> 