<template>
  <div class="error-container">
    <div class="error-content">
      <el-result
        icon="warning"
        title="401 Unauthorized"
        sub-title="您尚未登录或登录已过期，请重新登录"
      >
        <template #extra>
          <el-button
            type="primary"
            @click="goLogin"
          >
            去登录
          </el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.error-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.error-content {
  text-align: center;
  padding: 20px;
}
</style> 