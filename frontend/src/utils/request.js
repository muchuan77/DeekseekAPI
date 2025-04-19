import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 300000, // 请求超时时间 5分钟
  retry: 3, // 重试次数
  retryDelay: 1000, // 重试间隔
  withCredentials: true, // 允许跨域请求携带认证信息
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'Accept': 'application/json'
  }
})

// 请求取消控制器
const controller = new AbortController()

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 确保请求具有正确的字符编码
    if (config.method === 'post' || config.method === 'put') {
      if (!config.headers) {
        config.headers = {}
      }
      
      if (!config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json;charset=UTF-8'
      }
      
      // 如果是form-data，不设置application/json
      if (config.data instanceof FormData) {
        delete config.headers['Content-Type']
      }
    }
    
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
    console.log("后端响应: ", res)
    
    // 如果响应是直接的数据对象（没有code字段），直接返回
    if (res && typeof res === 'object' && !('code' in res)) {
      return res
    }
    
    // 如果请求不成功
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        router.push('/401')
        return Promise.reject(new Error('请先登录'))
      }
      
      // 403: 无权限
      if (res.code === 403) {
        router.push('/403')
        return Promise.reject(new Error('无权限访问'))
      }

      // 400: 请求参数错误
      if (res.code === 400) {
        router.push('/400')
        return Promise.reject(new Error('请求参数错误'))
      }

      // 500: 服务器内部错误
      if (res.code === 500) {
        router.push('/500')
        return Promise.reject(new Error('服务器内部错误'))
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
        case 400:
          router.push('/400')
          break
        case 401:
          router.push('/401')
          break
        case 403:
          router.push('/403')
          break
        case 404:
          router.push('/404')
          break
        case 500:
          router.push('/500')
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