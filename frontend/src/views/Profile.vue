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
        <el-form-item label="用户名">
          <span>{{ profileForm.username }}</span>
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag 
            v-for="role in profileForm.roles" 
            :key="role"
            :type="role === 'ADMIN' ? 'danger' : 'primary'"
            class="mx-1"
          >
            {{ role === 'ADMIN' ? '管理员' : (role === 'MODERATOR' ? '审核员' : '普通用户') }}
          </el-tag>
        </el-form-item>
        
        <el-form-item label="用户ID">
          <span>{{ profileForm.userId }}</span>
        </el-form-item>
        
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="profileForm.fullName" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="修改密码">
          <el-button type="text" @click="showPasswordDialog">修改密码</el-button>
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
import { useAuthStore } from '@/stores/auth'
import { updateUser, changePassword } from '@/api/user'

const authStore = useAuthStore()
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)
const passwordLoading = ref(false)
const passwordDialogVisible = ref(false)

const profileForm = reactive({
  userId: '',
  username: '',
  roles: [],
  fullName: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
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

onMounted(() => {
  try {
    const userInfo = authStore.userInfo
    if (!userInfo) {
      ElMessage.error('获取用户信息失败')
      return
    }
    
    Object.assign(profileForm, {
      userId: userInfo.userId,
      username: userInfo.username,
      roles: userInfo.roles || [],
      fullName: userInfo.fullName || '',
      email: userInfo.email || ''
    })
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
})

const handleSave = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    loading.value = true
    
    const response = await updateUser(profileForm.userId, {
      fullName: profileForm.fullName,
      email: profileForm.email
    })
    
    if (response.code === 200) {
      // 更新 store 中的用户信息
      authStore.setUserInfo({
        ...authStore.userInfo,
        fullName: profileForm.fullName,
        email: profileForm.email
      })
      
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
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
    
    const response = await changePassword(profileForm.userId, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.code === 200) {
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    ElMessage.error(error.message || '密码修改失败')
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
</style>