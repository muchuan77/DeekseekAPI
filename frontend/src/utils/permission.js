import { useAuthStore } from '../stores/auth'

// 检查用户是否具有指定角色
export const hasRole = (role) => {
  const authStore = useAuthStore()
  return authStore.userInfo?.roles?.includes(role)
}

// 检查用户是否具有指定权限
export const hasPermission = (permission) => {
  const authStore = useAuthStore()
  return authStore.userInfo?.permissions?.includes(permission)
}

// 检查路由权限
export const checkRoutePermission = (to) => {
  if (to.meta.roles) {
    const authStore = useAuthStore()
    const userRoles = authStore.userInfo?.roles || []
    return to.meta.roles.some(role => userRoles.includes(role))
  }
  return true
}

// 权限指令
export const permission = {
  mounted(el, binding) {
    const { value } = binding
    if (value && !hasPermission(value)) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

// 角色指令
export const role = {
  mounted(el, binding) {
    const { value } = binding
    if (value && !hasRole(value)) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}