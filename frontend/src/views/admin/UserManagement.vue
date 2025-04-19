<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-upload
              class="upload-demo"
              action="/api/users/import"
              :headers="uploadHeaders"
              :on-success="handleImportSuccess"
              :on-error="handleImportError"
              :show-file-list="false"
              accept=".xlsx"
            >
              <el-button type="primary">导入用户</el-button>
            </el-upload>
            <el-button type="success" @click="handleExport">导出用户</el-button>
            <el-button type="primary" @click="handleAdd">添加用户</el-button>
          </div>
        </div>
      </template>

      <!-- 批量操作工具栏 -->
      <div class="batch-actions" v-if="selectedUsers.length > 0">
        <span>已选择 {{ selectedUsers.length }} 个用户</span>
        <el-button type="primary" size="small" @click="handleBatchEnable">批量启用</el-button>
        <el-button type="warning" size="small" @click="handleBatchDisable">批量禁用</el-button>
        <el-button type="danger" size="small" @click="handleBatchDelete">批量删除</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/邮箱" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 用户表格 -->
      <el-table 
        :data="users" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="fullName" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag 
              v-for="role in row.roles" 
              :key="role"
              :type="role === 'ADMIN' ? 'danger' : (role === 'MODERATOR' ? 'warning' : 'primary')"
              class="mx-1"
            >
              {{ role === 'ADMIN' ? '管理员' : (role === 'MODERATOR' ? '审核员' : '普通用户') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handleEditRoles(row)">角色</el-button>
            <el-button type="warning" size="small" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
              :disabled="row.id === currentUser.id"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="form.fullName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="ENABLED">启用</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码确认对话框 -->
    <el-dialog
      title="重置密码"
      v-model="resetPasswordDialogVisible"
      width="400px"
    >
      <p>确定要重置用户 {{ selectedUser?.username }} 的密码吗？</p>
      <p>重置后的密码将设置为：123456</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmResetPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量删除确认对话框 -->
    <el-dialog
      title="批量删除"
      v-model="batchDeleteDialogVisible"
      width="400px"
    >
      <p>确定要删除选中的 {{ selectedUsers.length }} 个用户吗？</p>
      <p>此操作不可恢复，请谨慎操作！</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDeleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmBatchDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="编辑用户角色"
      width="30%"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="roleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox value="ADMIN">管理员</el-checkbox>
            <el-checkbox value="MODERATOR">审核员</el-checkbox>
            <el-checkbox value="USER">普通用户</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoles">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = computed(() => userStore.loading)
const users = computed(() => userStore.userList)
const total = computed(() => userStore.totalUsers)

const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const resetPasswordDialogVisible = ref(false)
const selectedUser = ref(null)
const roleDialogVisible = ref(false)
const roleForm = reactive({
  userId: '',
  username: '',
  roles: []
})
const selectedRoles = ref([])

const currentUser = JSON.parse(localStorage.getItem('user'))

const searchForm = reactive({
  keyword: '',
  status: ''
})

const form = reactive({
  id: '',
  username: '',
  fullName: '',
  email: '',
  status: 'ENABLED'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const fetchUsers = async () => {
  try {
    await userStore.fetchUserList({
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: searchForm.keyword,
      status: searchForm.status
    })
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUsers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUsers()
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  form.id = ''
  form.username = ''
  form.fullName = ''
  form.email = ''
  form.status = 'ENABLED'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  form.id = row.id
  form.username = row.username
  form.fullName = row.fullName
  form.email = row.email
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await userStore.deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleResetPassword = (row) => {
  selectedUser.value = row
  resetPasswordDialogVisible.value = true
}

const confirmResetPassword = async () => {
  try {
    await userStore.resetPassword(selectedUser.value.id)
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '密码重置失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (form.id) {
      // 编辑用户
      await userStore.updateUser(form.id, form)
      ElMessage.success('更新成功')
    } else {
      // 添加用户
      await userStore.createUser(form)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 批量操作相关
const selectedUsers = ref([])
const batchDeleteDialogVisible = ref(false)

// 上传相关
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 批量启用
const handleBatchEnable = async () => {
  try {
    await userStore.batchUpdateStatus(
      selectedUsers.value.map(user => user.id),
      'ENABLED'
    )
    ElMessage.success('批量启用成功')
    fetchUsers()
  } catch (error) {
    ElMessage.error(error.message || '批量启用失败')
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  try {
    await userStore.batchUpdateStatus(
      selectedUsers.value.map(user => user.id),
      'DISABLED'
    )
    ElMessage.success('批量禁用成功')
    fetchUsers()
  } catch (error) {
    ElMessage.error(error.message || '批量禁用失败')
  }
}

// 批量删除
const handleBatchDelete = () => {
  batchDeleteDialogVisible.value = true
}

// 确认批量删除
const confirmBatchDelete = async () => {
  try {
    await userStore.batchDelete(selectedUsers.value.map(user => user.id))
    ElMessage.success('批量删除成功')
    batchDeleteDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error(error.message || '批量删除失败')
  }
}

// 导出用户
const handleExport = async () => {
  try {
    await userStore.exportUsers({
      keyword: searchForm.keyword,
      status: searchForm.status
    })
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 导入成功处理
const handleImportSuccess = (response) => {
  ElMessage.success('导入成功')
  fetchUsers()
}

// 导入失败处理
const handleImportError = (error) => {
  ElMessage.error(error.message || '导入失败')
}

// 打开编辑角色对话框
const handleEditRoles = (row) => {
  roleForm.userId = row.id
  roleForm.username = row.username
  selectedRoles.value = [...row.roles]
  roleDialogVisible.value = true
}

// 提交角色修改
const submitRoles = async () => {
  try {
    await userStore.updateUserRoles(roleForm.userId, selectedRoles.value)
    ElMessage.success('角色更新成功')
    roleDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error('更新角色失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.batch-actions {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.upload-demo {
  display: inline-block;
}
</style> 