<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>用户注册</span>
        </div>
      </template>
  
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item
          label="用户名"
          prop="username"
        >
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item
          label="密码"
          prop="password"
        >
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item
          label="确认密码"
          prop="confirmPassword"
        >
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item
          label="姓名"
          prop="fullName"
        >
          <el-input
            v-model="registerForm.fullName"
            placeholder="请输入姓名"
          />
        </el-form-item>
        <el-form-item
          label="邮箱"
          prop="email"
        >
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item
          label="电话"
          prop="phone"
        >
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入电话号码"
            maxlength="10"
          />
        </el-form-item>
        <el-form-item
          label="管理员密码"
          prop="adminPassword"
        >
          <el-input 
            v-model="registerForm.adminPassword" 
            type="password" 
            placeholder="输入管理员密码以选择角色"
          />
        </el-form-item>
        <el-form-item
          v-if="showRoleSelection"
          label="角色"
        >
          <el-radio-group v-model="registerForm.roles[0]">
            <el-radio label="ADMIN">
              管理员
            </el-radio>
            <el-radio label="MODERATOR">
              审核员
            </el-radio>
            <el-radio label="USER">
              普通用户
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="login-link">
            已有账号？<router-link to="/login">
              立即登录
            </router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
  
  <script setup>
  import { ref, computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { useAuthStore } from '@/stores/auth'
  
  const router = useRouter()
  const authStore = useAuthStore()
  const formRef = ref(null)
  const registerForm = ref({
    username: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    email: '',
    phone: '',
    adminPassword: '',
    roles: ['USER'] // 默认角色为普通用户
  })
  const loading = ref(false)
  
  const ADMIN_PASSWORD = 'admin123' // 管理员密码，实际应用中应该存储在环境变量中
  
  const showRoleSelection = computed(() => {
    return registerForm.value.adminPassword === ADMIN_PASSWORD
  })
  
  const validatePass = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请输入密码'))
    } else {
      if (registerForm.value.confirmPassword !== '') {
        if (registerForm.value.password !== registerForm.value.confirmPassword) {
          callback(new Error('两次输入密码不一致'))
        }
      }
      callback()
    }
  }
  
  const validateConfirmPass = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请再次输入密码'))
    } else if (value !== registerForm.value.password) {
      callback(new Error('两次输入密码不一致'))
    } else {
      callback()
    }
  }
  
  const rules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 100, message: '长度在 6 到 100 个字符', trigger: 'blur' },
      { validator: validatePass, trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请再次输入密码', trigger: 'blur' },
      { validator: validateConfirmPass, trigger: 'blur' }
    ],
    fullName: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
      { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
    ],
    phone: [
      { max: 10, message: '长度不能超过10个字符', trigger: 'blur' }
    ]
  }
  
  const handleRegister = async () => {
    if (!formRef.value) return
    
    try {
      await formRef.value.validate()
      loading.value = true
      
      // 如果没有输入管理员密码或密码错误，强制设置为普通用户
      if (!showRoleSelection.value) {
        registerForm.value.roles = ['USER']
      }
  
      const { confirmPassword, adminPassword, ...registerData } = registerForm.value
      const success = await authStore.register(registerData)
      if (success) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      }
    } catch (error) {
      console.error('注册失败:', error)
      ElMessage.error(error.message || '注册失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }
  </script>
  
  <style scoped>
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f5f7fa;
  }
  
  .register-card {
    width: 400px;
  }
  
  .card-header {
    text-align: center;
    font-size: 20px;
    font-weight: bold;
  }
  
  .login-link {
    text-align: center;
    color: #606266;
  }
  
  .login-link a {
    color: #409eff;
    text-decoration: none;
  }
  
  .login-link a:hover {
    color: #66b1ff;
  }
  </style>