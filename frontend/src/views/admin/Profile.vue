<template>
  <div class="admin-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员信息</span>
        </div>
      </template>
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="120px"
      >
        <el-form-item label="用户ID">
          <el-input v-model="profileForm.userId" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag type="danger">管理员</el-tag>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input v-model="profileForm.createdAt" disabled />
        </el-form-item>
        <el-form-item label="最后登录时间">
          <el-input v-model="profileForm.lastLogin" disabled />
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
import { ref, reactive, onMounted } from 'vue'
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

const profileForm = reactive({
  userId: '',
  username: '',
  email: '',
  createdAt: '',
  lastLogin: ''
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
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
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

onMounted(async () => {
  if (!authStore.token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }

  try {
    const currentUser = await userStore.fetchCurrentUser()
    profileForm.userId = currentUser.id
    profileForm.username = currentUser.username
    profileForm.email = currentUser.email
    profileForm.createdAt = new Date(currentUser.createdAt).toLocaleString()
    profileForm.lastLogin = currentUser.lastLogin ? new Date(currentUser.lastLogin).toLocaleString() : '未登录'
  } catch (error) {
    console.error('获取管理员信息失败:', error)
    ElMessage.error(error.message || '获取管理员信息失败')
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
    
    const updateData = {
      username: profileForm.username,
      email: profileForm.email
    }
    
    await userStore.updateUser(profileForm.userId, updateData)
    await userStore.fetchCurrentUser()
    
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
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
.admin-profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 