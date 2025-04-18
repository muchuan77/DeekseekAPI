<template>
  <div class="permission-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="角色管理" name="roles">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>角色列表</span>
              <el-button type="primary" @click="handleAddRole">新增角色</el-button>
            </div>
          </template>
          
          <el-table :data="roles" style="width: 100%">
            <el-table-column prop="name" label="角色名称" />
            <el-table-column prop="description" label="角色描述" />
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="text" @click="handleEditRole(row)">编辑</el-button>
                <el-button type="text" @click="handleAssignPermissions(row)">分配权限</el-button>
                <el-button type="text" class="delete-btn" @click="handleDeleteRole(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="用户角色" name="userRoles">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>用户角色分配</span>
            </div>
          </template>
          
          <el-table :data="users" style="width: 100%">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column label="当前角色">
              <template #default="{ row }">
                <el-tag v-for="role in row.roles" :key="role.id" style="margin-right: 5px;">
                  {{ role.name }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" @click="handleEditUserRoles(row)">编辑角色</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 角色编辑对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="roleDialogType === 'add' ? '新增角色' : '编辑角色'"
      width="30%"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.name" />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="roleForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRole">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="50%"
    >
      <el-tree
        ref="permissionTree"
        :data="permissions"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
        :props="{
          label: 'name',
          children: 'children'
        }"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPermissions">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户角色编辑对话框 -->
    <el-dialog
      v-model="userRoleDialogVisible"
      title="编辑用户角色"
      width="30%"
    >
      <el-form :model="userRoleForm" label-width="80px">
        <el-form-item label="用户名">
          <span>{{ userRoleForm.username }}</span>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userRoleForm.roles" multiple placeholder="请选择角色">
            <el-option
              v-for="role in roles"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userRoleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserRoles">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRoles,
  createRole,
  updateRole,
  deleteRole,
  getPermissions,
  assignPermissions,
  getUserRoles,
  updateUserRoles
} from '@/api/permission'

const activeTab = ref('roles')
const roles = ref([])
const users = ref([])
const permissions = ref([])
const checkedPermissions = ref([])

const roleDialogVisible = ref(false)
const roleDialogType = ref('add')
const roleForm = ref({
  id: '',
  name: '',
  description: ''
})

const permissionDialogVisible = ref(false)
const currentRoleId = ref('')
const permissionTree = ref(null)

const userRoleDialogVisible = ref(false)
const userRoleForm = ref({
  id: '',
  username: '',
  roles: []
})

const loadRoles = async () => {
  try {
    const response = await getRoles()
    if (response.code === 200) {
      roles.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }
}

const loadPermissions = async () => {
  try {
    const response = await getPermissions()
    if (response.code === 200) {
      permissions.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  }
}

const handleAddRole = () => {
  roleDialogType.value = 'add'
  roleForm.value = {
    id: '',
    name: '',
    description: ''
  }
  roleDialogVisible.value = true
}

const handleEditRole = (role) => {
  roleDialogType.value = 'edit'
  roleForm.value = { ...role }
  roleDialogVisible.value = true
}

const handleDeleteRole = (role) => {
  ElMessageBox.confirm(
    `确定要删除角色 ${role.name} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteRole(role.id)
      if (response.code === 200) {
        ElMessage.success('删除角色成功')
        loadRoles()
      }
    } catch (error) {
      ElMessage.error('删除角色失败')
    }
  })
}

const handleAssignPermissions = async (role) => {
  currentRoleId.value = role.id
  try {
    const response = await getPermissions()
    if (response.code === 200) {
      permissions.value = response.data
      checkedPermissions.value = role.permissions || []
      permissionDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  }
}

const handleEditUserRoles = async (user) => {
  userRoleForm.value = {
    id: user.id,
    username: user.username,
    roles: user.roles.map(role => role.id)
  }
  userRoleDialogVisible.value = true
}

const submitRole = async () => {
  try {
    const response = roleDialogType.value === 'add'
      ? await createRole(roleForm.value)
      : await updateRole(roleForm.value.id, roleForm.value)
    
    if (response.code === 200) {
      ElMessage.success(roleDialogType.value === 'add' ? '创建角色成功' : '更新角色成功')
      roleDialogVisible.value = false
      loadRoles()
    }
  } catch (error) {
    ElMessage.error(roleDialogType.value === 'add' ? '创建角色失败' : '更新角色失败')
  }
}

const submitPermissions = async () => {
  const checkedKeys = permissionTree.value.getCheckedKeys()
  try {
    const response = await assignPermissions({
      roleId: currentRoleId.value,
      permissions: checkedKeys
    })
    if (response.code === 200) {
      ElMessage.success('分配权限成功')
      permissionDialogVisible.value = false
      loadRoles()
    }
  } catch (error) {
    ElMessage.error('分配权限失败')
  }
}

const submitUserRoles = async () => {
  try {
    const response = await updateUserRoles(userRoleForm.value.id, {
      roles: userRoleForm.value.roles
    })
    if (response.code === 200) {
      ElMessage.success('更新用户角色成功')
      userRoleDialogVisible.value = false
      loadUsers()
    }
  } catch (error) {
    ElMessage.error('更新用户角色失败')
  }
}

onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<style scoped>
.permission-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.delete-btn {
  color: #F56C6C;
}
</style>