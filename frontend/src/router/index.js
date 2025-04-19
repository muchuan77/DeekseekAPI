import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layouts/Layout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { useAuthStore } from '@/stores/auth'
import Error400 from '@/views/error/400.vue'
import Error401 from '@/views/error/401.vue'
import Error403 from '@/views/error/403.vue'
import Error404 from '@/views/error/404.vue'
import Error500 from '@/views/error/500.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'rumors',
        name: 'Rumors',
        component: () => import('@/views/Rumors.vue'),
        meta: { title: '谣言管理', requiresAuth: true }
      },
      {
        path: 'rumor/:id',
        name: 'RumorDetail',
        component: () => import('@/views/RumorDetail.vue'),
        meta: { title: '谣言详情', requiresAuth: true }
      },
      {
        path: 'ai',
        name: 'AI',
        component: () => import('@/views/AI.vue'),
        meta: { title: 'AI分析' }
      },
      {
        path: 'propagation',
        name: 'Propagation',
        component: () => import('@/views/Propagation.vue'),
        meta: { title: '传播分析' }
      },
      {
        path: 'sync',
        name: 'Sync',
        component: () => import('@/views/SyncManagement.vue'),
        meta: { title: '数据同步' }
      },
      {
        path: 'log-visualization',
        name: 'LogVisualization',
        component: () => import('@/views/LogVisualization.vue'),
        meta: { title: '日志可视化' }
      },
      {
        path: 'trace',
        name: 'Trace',
        component: () => import('@/views/Trace.vue'),
        meta: { title: '溯源可视化' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理控制台', roles: ['ADMIN'] }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'comments',
        name: 'CommentManagement',
        component: () => import('@/views/admin/CommentManagement.vue'),
        meta: { title: '评论管理', roles: ['ADMIN'] }
      },
      {
        path: 'logs',
        name: 'LogManagement',
        component: () => import('@/views/admin/LogManagement.vue'),
        meta: { title: '日志管理', roles: ['ADMIN'] }
      },
      {
        path: 'system-logs',
        name: 'SystemLog',
        component: () => import('@/views/admin/SystemLog.vue'),
        meta: { title: '系统日志', roles: ['ADMIN'] }
      },
      {
        path: 'operation-logs',
        name: 'OperationLog',
        component: () => import('@/views/admin/OperationLog.vue'),
        meta: { title: '操作日志', roles: ['ADMIN'] }
      },
      {
        path: 'audit-logs',
        name: 'AuditLog',
        component: () => import('@/views/admin/AuditLog.vue'),
        meta: { title: '审计日志', roles: ['ADMIN'] }
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: () => import('@/views/admin/Analytics.vue'),
        meta: { title: '数据分析', roles: ['ADMIN'] }
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/admin/SystemSettings.vue'),
        meta: { title: '系统设置', roles: ['ADMIN'] }
      },
      {
        path: 'permission',
        name: 'PermissionManagement',
        component: () => import('@/views/admin/PermissionManagement.vue'),
        meta: { title: '权限管理', roles: ['ADMIN'] }
      },
      {
        path: 'ai-management',
        name: 'AIManagement',
        component: () => import('@/views/admin/AIManagement.vue'),
        meta: { title: 'AI管理', roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/Profile.vue'),
        meta: { title: '管理员信息', roles: ['ADMIN'] }
      },
    ]
  },
  {
    path: '/400',
    name: '400',
    component: Error400,
    meta: {
      title: '400 Bad Request'
    }
  },
  {
    path: '/401',
    name: '401',
    component: Error401,
    meta: {
      title: '401 Unauthorized'
    }
  },
  {
    path: '/403',
    name: '403',
    component: Error403,
    meta: {
      title: '403 Forbidden'
    }
  },
  {
    path: '/404',
    name: '404',
    component: Error404,
    meta: {
      title: '404 Not Found'
    }
  },
  {
    path: '/500',
    name: '500',
    component: Error500,
    meta: {
      title: '500 Internal Server Error'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: Error404,
    meta: {
      title: '404 Not Found'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isLoggedIn
  const isAdmin = authStore.isAdmin

  // 不需要登录的页面
  if (to.path === '/login' || to.path === '/register') {
    if (isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 需要登录的页面
  if (!isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 需要管理员权限的页面
  if (to.path.startsWith('/admin') && !isAdmin) {
    next('/')
    return
  }

  next()
})

export default router