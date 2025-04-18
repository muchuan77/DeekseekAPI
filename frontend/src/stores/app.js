import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: localStorage.getItem('sidebarStatus') 
        ? !!+localStorage.getItem('sidebarStatus') 
        : true
    },
    language: localStorage.getItem('language') || 'zh-CN',
    theme: localStorage.getItem('theme') || 'light'
  }),
  
  actions: {
    toggleSidebar() {
      this.sidebar.opened = !this.sidebar.opened
      localStorage.setItem('sidebarStatus', this.sidebar.opened ? '1' : '0')
    },
    
    setLanguage(language) {
      this.language = language
      localStorage.setItem('language', language)
    },
    
    setTheme(theme) {
      this.theme = theme
      localStorage.setItem('theme', theme)
      // 更新根元素的主题类名
      document.documentElement.className = `theme-${theme}`
    }
  }
}) 