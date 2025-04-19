<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Logo">
        <span>谣言追踪系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <template v-if="!isVisitor">
          <el-menu-item index="/rumors">
            <el-icon><Document /></el-icon>
            <span>谣言列表</span>
          </el-menu-item>
          <el-menu-item index="/ai">
            <el-icon><Cpu /></el-icon>
            <span>AI分析</span>
          </el-menu-item>
          <el-menu-item index="/trace"> 
            <el-icon><Connection /></el-icon>
            <span>溯源可视化</span> 
          </el-menu-item>
          <el-menu-item index="/sync">
            <el-icon><Refresh /></el-icon>
            <span>数据同步</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item v-if="hasRole('ADMIN') || hasRole('MODERATOR')" index="/admin/dashboard">
            <el-icon><Setting /></el-icon>
            <span>管理后台</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-button v-if="isVisitor" type="primary" link @click="goToLogin">
            登录
          </el-button>
          <el-dropdown v-else>
            <span class="user-info">
              {{ username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">个人信息</el-dropdown-item>
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
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { 
  HomeFilled,
  Document,
  Search,
  Connection,
  PictureFilled,
  Refresh,
  User,
  Setting,
  ArrowDown,
  Cpu
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const userInfo = computed(() => authStore.userInfo)

const username = computed(() => {
  if (userInfo.value) {
    if (hasRole('ADMIN')) {
      return '管理员'
    } else if (hasRole('MODERATOR')) {
      return '审核员'
    }
    return userInfo.value.username || '用户'
  }
  return '用户'
})

const hasRole = (role) => {
  return userInfo.value?.roles?.includes(role) || false
}

const isVisitor = computed(() => !userInfo.value)

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route.meta?.title || '页面')

const handleProfile = () => {
  router.push('/profile')
}

const goToLogin = () => {
  router.push('/login')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
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
</style>