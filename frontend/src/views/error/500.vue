<template>
  <div class="error-container">
    <div class="error-content">
      <el-result
        icon="error"
        title="500 Internal Server Error"
        sub-title="服务器内部错误，请稍后再试"
      >
        <template #extra>
          <el-button
            type="primary"
            @click="goHome"
          >
            返回首页
          </el-button>
          <el-button @click="refresh">
            刷新页面
          </el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const refresh = () => {
  window.location.reload()
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