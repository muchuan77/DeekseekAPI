import router from './router'
import { useAuthStore } from './stores/auth'
import { ElMessage } from 'element-plus'

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查认证状态
  if (authStore.token) {
    // 如果已登录，检查用户信息是否有效
    if (!authStore.userInfo) {
      try {
        const isValid = await authStore.checkAuth()
        if (!isValid) {
          // 尝试刷新token
          const refreshed = await authStore.refreshToken()
          if (!refreshed) {
            authStore.clearAuth()
            ElMessage.error('登录已过期，请重新登录')
            next(`/login?redirect=${to.path}`)
            return
          }
        }
      } catch (error) {
        console.error('检查认证状态失败:', error)
        authStore.clearAuth()
        next(`/login?redirect=${to.path}`)
        return
      }
    }

    // 已登录用户访问登录页，重定向到首页
    if (to.path === '/login' || to.path === '/register') {
      next({ path: '/' })
      return
    }

    // 检查角色权限
    if (to.meta.roles) {
      const userRoles = authStore.userInfo?.roles || []
      if (to.meta.roles.some(role => userRoles.includes(role))) {
        next()
      } else {
        ElMessage.error('无权限访问')
        next({ path: '/403' })
      }
    } else {
      next()
    }
  } else {
    // 未登录用户访问需要认证的页面，重定向到登录页
    if (to.path === '/login' || to.path === '/register') {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})