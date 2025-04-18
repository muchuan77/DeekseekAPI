import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { permission, role } from './utils/permission'
import './permission'
import { ElMessage } from 'element-plus'

import { useSettingsStore } from './stores/settings'

const app = createApp(App)
const pinia = createPinia()

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册权限指令
app.directive('permission', permission)
app.directive('role', role)

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('Global error:', err)
  ElMessage.error('发生错误，请刷新页面重试')
}

// 注册插件
app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 初始化设置
const settingsStore = useSettingsStore()
settingsStore.init()

app.mount('#app')