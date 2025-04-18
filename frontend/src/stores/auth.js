import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, logout as logoutApi, refreshToken as refreshTokenApi } from '@/api/auth'
import router from '@/router'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const token = localStorage.getItem('token')
    const userStr = localStorage.getItem('user')
    return {
      token: token || '',
      userInfo: userStr ? JSON.parse(userStr) : null
    }
  },
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.roles?.includes('ADMIN')
  },
  
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('user', JSON.stringify(userInfo))
    },

    clearAuth() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    async login(userInfo) {
      try {
        const { username, password } = userInfo
        const response = await loginApi({ username: username.trim(), password })
        console.log("登录响应: ", response)
        
        if (response.code === 200 && response.success) {
          const { token, userId, username, roles } = response.data
          this.setToken(token)
          this.setUserInfo({ userId, username, roles })
          ElMessage.success('登录成功')
          return true
        }
        
        ElMessage.error(response.message || '登录失败')
        return false
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败，请稍后重试')
        return false
      }
    },

    async register(userInfo) {
      try {
        const response = await registerApi(userInfo)
        console.log("注册响应: ", response)
        
        if (response.code === 200 && response.success) {
          const { token, userId, username, roles } = response.data
          this.setToken(token)
          this.setUserInfo({ userId, username, roles })
          ElMessage.success('注册成功')
          return true
        }
        
        ElMessage.error(response.message || '注册失败')
        return false
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error(error.message || '注册失败，请稍后重试')
        return false
      }
    },

    async logout() {
      try {
        await logoutApi()
        this.clearAuth()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch (error) {
        console.error('登出失败:', error)
        ElMessage.error('登出失败，请稍后重试')
      }
    },

    async checkAuth() {
      if (!this.token) return false
      return true
    }
  }
})