import { defineStore } from 'pinia'
import { permissionApi } from '@/api/permission'
import { ElMessage } from 'element-plus'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    permissions: [],
    roles: [],
    loading: false,
    totalPermissions: 0,
    totalRoles: 0
  }),

  getters: {
    getPermissionById: (state) => (id) => {
      return state.permissions.find(permission => permission.id === id)
    },
    getRoleById: (state) => (id) => {
      return state.roles.find(role => role.id === id)
    }
  },

  actions: {
    async fetchPermissions(params = {}) {
      try {
        this.loading = true
        const response = await permissionApi.getPermissions(params)
        if (response.code === 200) {
          this.permissions = response.data.content
          this.totalPermissions = response.data.totalElements
        }
      } catch (error) {
        console.error('获取权限列表失败:', error)
        ElMessage.error('获取权限列表失败')
      } finally {
        this.loading = false
      }
    },

    async fetchRoles(params = {}) {
      try {
        this.loading = true
        const response = await permissionApi.getRoles(params)
        if (response.code === 200) {
          this.roles = response.data.content
          this.totalRoles = response.data.totalElements
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
        ElMessage.error('获取角色列表失败')
      } finally {
        this.loading = false
      }
    },

    async createPermission(permissionData) {
      try {
        const response = await permissionApi.createPermission(permissionData)
        if (response.code === 200) {
          ElMessage.success('创建权限成功')
          return true
        }
        return false
      } catch (error) {
        console.error('创建权限失败:', error)
        ElMessage.error('创建权限失败')
        return false
      }
    },

    async updatePermission(permissionId, permissionData) {
      try {
        const response = await permissionApi.updatePermission(permissionId, permissionData)
        if (response.code === 200) {
          ElMessage.success('更新权限成功')
          return true
        }
        return false
      } catch (error) {
        console.error('更新权限失败:', error)
        ElMessage.error('更新权限失败')
        return false
      }
    },

    async deletePermission(permissionId) {
      try {
        const response = await permissionApi.deletePermission(permissionId)
        if (response.code === 200) {
          ElMessage.success('删除权限成功')
          return true
        }
        return false
      } catch (error) {
        console.error('删除权限失败:', error)
        ElMessage.error('删除权限失败')
        return false
      }
    }
  }
}) 