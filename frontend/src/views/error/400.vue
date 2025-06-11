<template>
  <div class="error-container">
    <div class="error-content">
      <el-result
        icon="error"
        title="400 Bad Request"
        sub-title="请求参数错误，请检查您的输入"
      >
        <template #extra>
          <el-button
            type="primary"
            @click="goHome"
          >
            返回首页
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