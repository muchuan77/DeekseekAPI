import { defineStore } from 'pinia'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    userList: [],
    currentUser: null,
    totalUsers: 0,
    loading: false
  }),
  
  getters: {
    getUserById: (state) => (id) => {
      return state.userList.find(user => user.id === id)
    }
  },
  
  actions: {
    async fetchUserList(params = {}) {
      try {
        this.loading = true
        const response = await userApi.getUsers(params)
        if (response.code === 200) {
          this.userList = response.data.content
          this.totalUsers = response.data.totalElements
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      } finally {
        this.loading = false
      }
    },

    async updateUser(userId, userData) {
      try {
        const response = await userApi.updateUser(userId, userData)
        if (response.code === 200 && response.success) {
          ElMessage.success('更新用户信息成功')
          // 更新本地用户列表
          const index = this.userList.findIndex(user => user.id === userId)
          if (index !== -1) {
            this.userList[index] = { ...this.userList[index], ...userData }
          }
          // 更新当前用户信息
          if (this.currentUser && this.currentUser.id === userId) {
            this.currentUser = { ...this.currentUser, ...userData }
            // 如果更新了当前用户，同步更新 auth store
            const authStore = useAuthStore()
            authStore.setUser(this.currentUser)
          }
          return true
        }
        throw new Error(response.message || '更新用户信息失败')
      } catch (error) {
        console.error('更新用户信息失败:', error)
        ElMessage.error(error.message || '更新用户信息失败')
        throw error
      }
    },

    async deleteUser(userId) {
      try {
        const response = await userApi.deleteUser(userId)
        if (response.code === 200) {
          ElMessage.success('删除用户成功')
          // 从本地用户列表中移除
          this.userList = this.userList.filter(user => user.id !== userId)
          return true
        }
        return false
      } catch (error) {
        console.error('删除用户失败:', error)
        ElMessage.error('删除用户失败')
        return false
      }
    },

    async resetPassword(userId) {
      try {
        const response = await userApi.resetPassword(userId)
        if (response.code === 200) {
          ElMessage.success('重置密码成功')
          return true
        }
        return false
      } catch (error) {
        console.error('重置密码失败:', error)
        ElMessage.error('重置密码失败')
        return false
      }
    },

    async changePassword(userId, data) {
      try {
        const response = await userApi.changePassword(userId, data)
        if (response.code === 200) {
          ElMessage.success('修改密码成功')
          return true
        }
        return false
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败')
        return false
      }
    },

    async updateUserStatus(userId, status) {
      try {
        const response = await userApi.updateUserStatus(userId, status)
        if (response.code === 200) {
          ElMessage.success('更新用户状态成功')
          // 更新本地用户列表
          const index = this.userList.findIndex(user => user.id === userId)
          if (index !== -1) {
            this.userList[index].status = status
          }
          return true
        }
        return false
      } catch (error) {
        console.error('更新用户状态失败:', error)
        ElMessage.error('更新用户状态失败')
        return false
      }
    },

    async updateUserRoles(userId, roles) {
      try {
        const response = await userApi.updateUserRoles(userId, roles)
        if (response.code === 200) {
          ElMessage.success('更新用户角色成功')
          // 更新本地用户列表
          const index = this.userList.findIndex(user => user.id === userId)
          if (index !== -1) {
            this.userList[index].roles = roles
          }
          return true
        }
        return false
      } catch (error) {
        console.error('更新用户角色失败:', error)
        ElMessage.error('更新用户角色失败')
        return false
      }
    },

    async batchUpdateStatus(ids, status) {
      try {
        const response = await userApi.batchUpdateStatus(ids, status)
        if (response.code === 200) {
          ElMessage.success('批量更新状态成功')
          // 更新本地用户列表
          this.userList = this.userList.map(user => {
            if (ids.includes(user.id)) {
              return { ...user, status }
            }
            return user
          })
          return true
        }
        return false
      } catch (error) {
        console.error('批量更新状态失败:', error)
        ElMessage.error('批量更新状态失败')
        return false
      }
    },

    async batchDelete(ids) {
      try {
        const response = await userApi.batchDelete(ids)
        if (response.code === 200) {
          ElMessage.success('批量删除成功')
          // 从本地用户列表中移除
          this.userList = this.userList.filter(user => !ids.includes(user.id))
          return true
        }
        return false
      } catch (error) {
        console.error('批量删除失败:', error)
        ElMessage.error('批量删除失败')
        return false
      }
    },

    async importUsers(file) {
      try {
        const response = await userApi.importUsers(file)
        if (response.code === 200) {
          ElMessage.success('导入用户成功')
          return true
        }
        return false
      } catch (error) {
        console.error('导入用户失败:', error)
        ElMessage.error('导入用户失败')
        return false
      }
    },

    async exportUsers(params) {
      try {
        const response = await userApi.exportUsers(params)
        if (response) {
          const url = window.URL.createObjectURL(new Blob([response]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', `users_${new Date().getTime()}.xlsx`)
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          return true
        }
        return false
      } catch (error) {
        console.error('导出用户失败:', error)
        ElMessage.error('导出用户失败')
        return false
      }
    },

    async lockUser(userId) {
      try {
        const response = await userApi.lockUser(userId)
        if (response.code === 200) {
          ElMessage.success('锁定用户成功')
          // 更新本地用户列表
          const index = this.userList.findIndex(user => user.id === userId)
          if (index !== -1) {
            this.userList[index].status = 'LOCKED'
          }
          return true
        }
        return false
      } catch (error) {
        console.error('锁定用户失败:', error)
        ElMessage.error('锁定用户失败')
        return false
      }
    },

    async unlockUser(userId) {
      try {
        const response = await userApi.unlockUser(userId)
        if (response.code === 200) {
          ElMessage.success('解锁用户成功')
          // 更新本地用户列表
          const index = this.userList.findIndex(user => user.id === userId)
          if (index !== -1) {
            this.userList[index].status = 'ENABLED'
          }
          return true
        }
        return false
      } catch (error) {
        console.error('解锁用户失败:', error)
        ElMessage.error('解锁用户失败')
        return false
      }
    },

    async fetchCurrentUser() {
      try {
        const response = await userApi.getCurrentUser()
        if (response.code === 200 && response.success) {
          this.currentUser = response.data
          return this.currentUser
        }
        throw new Error(response.message || '获取当前用户信息失败')
      } catch (error) {
        console.error('获取当前用户信息失败:', error)
        throw error
      }
    }
  }
}) 