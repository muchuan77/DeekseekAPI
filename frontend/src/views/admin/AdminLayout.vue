<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Monitor /></el-icon>
            <span>控制台</span>
          </el-menu-item>
          <el-menu-item index="/admin/user-management">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/comment-management">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/log-management">
            <el-icon><Document /></el-icon>
            <span>日志管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/analytics">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据分析</span>
          </el-menu-item>
          <el-menu-item index="/admin/system-settings">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <div class="left">
              <h2>谣言溯源系统管理后台</h2>
            </div>
            <div class="right">
              <el-dropdown>
                <span class="user-info">
                  {{ userStore.userInfo?.username }}
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="router.push('/profile')">
                      <el-icon><User /></el-icon>
                      个人信息
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleLogout">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  User,
  ChatDotRound,
  Document,
  DataAnalysis,
  Setting,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = async () => {
  try {
    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    ElMessage.error('退出登录失败')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info .el-icon {
  margin-left: 5px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style> 