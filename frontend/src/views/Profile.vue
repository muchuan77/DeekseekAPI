<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
      >
        <el-form-item label="用户ID">
          <el-input v-model="profileForm.userId" disabled />
        </el-form-item>

        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" placeholder="请输入用户名" :disabled="!isAdmin">
            <template #prefix>
              <span class="current-value">{{ profileForm.username }}</span>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag 
            :type="getRoleType(profileForm.roles[0])"
            class="mx-1"
          >
            {{ getRoleLabel(profileForm.roles[0]) }}
          </el-tag>
        </el-form-item>
        
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="profileForm.fullName" placeholder="请输入姓名">
            <template #prefix>
              <span class="current-value">{{ profileForm.fullName || '未设置' }}</span>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱">
            <template #prefix>
              <span class="current-value">{{ profileForm.email || '未设置' }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="profileForm.phoneNumber" placeholder="请输入手机号码">
            <template #prefix>
              <span class="current-value">{{ profileForm.phoneNumber || '未设置' }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="状态">
          <template v-if="isAdmin">
            <el-select
              v-model="profileForm.status"
              placeholder="请选择状态"
              style="width: 100%"
            >
              <el-option
                v-for="status in statusOptions"
                :key="status.value"
                :label="status.label"
                :value="status.value"
              />
            </el-select>
          </template>
          <template v-else>
            <el-input v-model="profileForm.status" disabled>
              <template #prefix>
                <span class="current-value">{{ getStatusText(profileForm.status) }}</span>
              </template>
            </el-input>
          </template>
        </el-form-item>

        <el-form-item label="最后登录时间">
          <el-input v-model="profileForm.lastLogin" :disabled="!isAdmin">
            <template #prefix>
              <span class="current-value">{{ formatDateTime(profileForm.lastLogin) }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="创建时间">
          <el-input v-model="profileForm.createdAt" disabled>
            <template #prefix>
              <span class="current-value">{{ formatDateTime(profileForm.createdAt) }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="登录尝试次数">
          <el-input v-model="profileForm.loginAttempts" :disabled="!isAdmin">
            <template #prefix>
              <span class="current-value">{{ profileForm.loginAttempts }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="账户锁定时间">
          <el-input v-model="profileForm.accountLockedUntil" :disabled="!isAdmin">
            <template #prefix>
              <span class="current-value">{{ formatDateTime(profileForm.accountLockedUntil) }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="修改密码">
          <el-button type="link" @click="showPasswordDialog">修改密码</el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="30%"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePasswordChange" :loading="passwordLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const authStore = useAuthStore()
const userStore = useUserStore()
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)
const passwordLoading = ref(false)
const passwordDialogVisible = ref(false)

// 计算当前用户是否是管理员
const isAdmin = computed(() => {
  return userStore.currentUser?.roles?.includes('ADMIN')
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '未设置'
  return new Date(dateTime).toLocaleString()
}

// 状态选项
const statusOptions = [
  { value: 'ENABLED', label: '启用' },
  { value: 'DISABLED', label: '禁用' },
  { value: 'LOCKED', label: '锁定' }
]

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'ENABLED': '启用',
    'DISABLED': '禁用',
    'LOCKED': '锁定'
  }
  return statusMap[status] || '未知'
}

const profileForm = reactive({
  userId: '',
  username: '',
  roles: [],
  fullName: '',
  email: '',
  phoneNumber: '',
  status: '',
  lastLogin: '',
  createdAt: '',
  loginAttempts: 0,
  accountLockedUntil: null
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符之间', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phoneNumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 角色选项
const roleOptions = [
  { value: 'ADMIN', label: '管理员' },
  { value: 'MODERATOR', label: '审核员' },
  { value: 'USER', label: '普通用户' }
]

// 获取角色类型
const getRoleType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'MODERATOR': 'warning',
    'USER': 'primary'
  }
  return typeMap[role] || 'info'
}

// 获取角色标签
const getRoleLabel = (role) => {
  const labelMap = {
    'ADMIN': '管理员',
    'MODERATOR': '审核员',
    'USER': '普通用户'
  }
  return labelMap[role] || role
}

onMounted(async () => {
  // 检查用户是否已登录
  if (!authStore.token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }

  try {
    // 获取最新的用户信息
    const currentUser = await userStore.fetchCurrentUser()
    
    // 使用解构赋值来更新表单数据
    const {
      id,
      username,
      roles = [],
      fullName = '',
      email = '',
      phoneNumber = '',
      status,
      lastLogin,
      createdAt,
      loginAttempts = 0,
      accountLockedUntil
    } = currentUser
    
    // 更新表单数据
    profileForm.userId = id
    profileForm.username = username
    profileForm.roles = roles
    profileForm.fullName = fullName
    profileForm.email = email
    profileForm.phoneNumber = phoneNumber
    profileForm.status = status
    profileForm.lastLogin = lastLogin
    profileForm.createdAt = createdAt
    profileForm.loginAttempts = loginAttempts
    profileForm.accountLockedUntil = accountLockedUntil
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error(error.message || '获取用户信息失败')
    // 如果获取用户信息失败，可能是 token 过期，需要重新登录
    if (error.response?.status === 401) {
      authStore.clearAuth()
      router.push('/login')
    }
  }
})

const handleSave = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    loading.value = true
    
    // 准备要更新的数据
    const updateData = {
      fullName: profileForm.fullName,
      email: profileForm.email,
      phoneNumber: profileForm.phoneNumber
    }
    
    // 如果是管理员，可以修改更多字段
    if (isAdmin.value) {
      updateData.username = profileForm.username
      updateData.status = profileForm.status
      updateData.loginAttempts = profileForm.loginAttempts
      updateData.accountLockedUntil = profileForm.accountLockedUntil
      updateData.lastLogin = profileForm.lastLogin
    }
    
    await userStore.updateUser(profileForm.userId, updateData)
    
    // 重新获取最新用户信息
    await userStore.fetchCurrentUser()
    
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
    // 如果保存失败，可能是 token 过期，需要重新登录
    if (error.response?.status === 401) {
      authStore.clearAuth()
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const showPasswordDialog = () => {
  passwordDialogVisible.value = true
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const handlePasswordChange = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    
    await userStore.changePassword(profileForm.userId, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (error) {
    console.error('密码修改失败:', error)
    ElMessage.error(error.message || '密码修改失败')
    // 如果密码修改失败，可能是 token 过期，需要重新登录
    if (error.response?.status === 401) {
      authStore.clearAuth()
      router.push('/login')
    }
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.mx-1 {
  margin: 0 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.current-value {
  color: #666;
  margin-right: 8px;
  border-right: 1px solid #dcdfe6;
  padding-right: 8px;
}
</style>