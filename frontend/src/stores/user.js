import { defineStore } from 'pinia'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

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
        if (response.code === 200) {
          ElMessage.success('更新用户信息成功')
          // 更新本地用户列表
          const index = this.userList.findIndex(user => user.id === userId)
          if (index !== -1) {
            this.userList[index] = { ...this.userList[index], ...userData }
          }
          return true
        }
        return false
      } catch (error) {
        console.error('更新用户信息失败:', error)
        ElMessage.error('更新用户信息失败')
        return false
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
    }
  }
}) 