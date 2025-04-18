import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 15000, // 请求超时时间
  retry: 3, // 重试次数
  retryDelay: 1000 // 重试间隔
})

// 请求取消控制器
const controller = new AbortController()

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    console.log("后端响应: ", res, 'code:'+res.code); // 打印响应内容，调试用
    
    // 如果请求不成功
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        // 尝试刷新token
        const refreshToken = localStorage.getItem('refreshToken')
        if (refreshToken) {
          return refreshTokenRequest(refreshToken)
            .then(() => {
              // 重新发起请求
              return service(response.config)
            })
            .catch(() => {
              // 刷新token失败，清除token并跳转登录页面
              clearAuth()
              router.push('/login')
              return Promise.reject(new Error('登录已过期，请重新登录'))
            })
        } else {
          clearAuth()
          router.push('/login')
          return Promise.reject(new Error('请先登录'))
        }
      }
      
      // 403: 无权限
      if (res.code === 403) {
        router.push('/')
        return Promise.reject(new Error('无权限访问'))
      }
      
      return Promise.reject(new Error(res.message || '系统错误'))
    }
    
    return res
  },
  error => {
    // 处理请求错误
    if (error.response) {
      // 请求已发出，但服务器响应的状态码不在 2xx 范围内
      const res = error.response.data
      const message = res?.message || '系统错误'
      
      switch (error.response.status) {
        case 401:
          clearAuth()
          router.push('/login')
          break
        case 403:
          router.push('/')
          break
        case 404:
          router.push('/404')
          break
        case 500:
          ElMessage.error(message)
          break
        default:
          ElMessage.error(message)
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      ElMessage.error('网络连接失败')
    } else {
      // 请求配置出错
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

// 刷新token请求
function refreshTokenRequest(refreshToken) {
  return service({
    url: '/api/auth/refresh',
    method: 'post',
    data: { refreshToken }
  }).then(res => {
    if (res.code === 200) {
      const authStore = useAuthStore()
      authStore.setToken(res.data.token)
      authStore.setRefreshToken(res.data.refreshToken)
      return res
    }
    return Promise.reject(new Error(res.message || '刷新token失败'))
  })
}

// 清除认证信息
function clearAuth() {
  const authStore = useAuthStore()
  authStore.token = ''
  authStore.userInfo = null
  authStore.refreshToken = ''
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('refreshToken')
}

// 取消所有请求
export function cancelAllRequests() {
  controller.abort()
}

export default service 