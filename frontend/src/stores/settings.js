import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  // 状态
  const theme = ref('light')
  const language = ref('zh-CN')
  const sidebarCollapsed = ref(false)
  const systemSettings = ref({})

  // 计算属性
  const isDark = computed(() => theme.value === 'dark')
  const isChinese = computed(() => language.value === 'zh-CN')

  // 方法
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    document.documentElement.setAttribute('data-theme', theme.value)
  }

  const toggleLanguage = () => {
    language.value = language.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  }

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  const updateSystemSettings = (settings) => {
    systemSettings.value = { ...systemSettings.value, ...settings }
  }

  // 初始化
  const init = () => {
    const savedTheme = localStorage.getItem('theme')
    const savedLanguage = localStorage.getItem('language')
    const savedSidebarCollapsed = localStorage.getItem('sidebarCollapsed')

    if (savedTheme) theme.value = savedTheme
    if (savedLanguage) language.value = savedLanguage
    if (savedSidebarCollapsed) sidebarCollapsed.value = JSON.parse(savedSidebarCollapsed)

    document.documentElement.setAttribute('data-theme', theme.value)
  }

  // 监听状态变化
  watch(theme, (newValue) => {
    localStorage.setItem('theme', newValue)
  })

  watch(language, (newValue) => {
    localStorage.setItem('language', newValue)
  })

  watch(sidebarCollapsed, (newValue) => {
    localStorage.setItem('sidebarCollapsed', JSON.stringify(newValue))
  })

  return {
    // 状态
    theme,
    language,
    sidebarCollapsed,
    systemSettings,
    
    // 计算属性
    isDark,
    isChinese,
    
    // 方法
    toggleTheme,
    toggleLanguage,
    toggleSidebar,
    updateSystemSettings,
    init
  }
}) 