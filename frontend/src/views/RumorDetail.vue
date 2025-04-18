<template>
  <div class="rumor-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>谣言详情</span>
          <div>
            <el-button link type="primary" @click="router.push('/rumors')">返回列表</el-button>
            <el-button link type="primary" @click="handleEdit">编辑</el-button>
          </div>
        </div>
      </template>

      <div v-if="currentRumor" class="detail-content">
        <div class="detail-item">
          <span class="label">标题：</span>
          <span class="value">{{ currentRumor.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">内容：</span>
          <span class="value">{{ currentRumor.content }}</span>
        </div>
        <div class="detail-item">
          <span class="label">来源：</span>
          <span class="value">{{ currentRumor.source }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(currentRumor.status)">{{ getStatusText(currentRumor.status) }}</el-tag>
        </div>
        <div class="detail-item">
          <span class="label">发布时间：</span>
          <span class="value">{{ formatDate(currentRumor.publishTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">验证时间：</span>
          <span class="value">{{ formatDate(currentRumor.verifyTime) || '未验证' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">验证结果：</span>
          <span class="value">{{ currentRumor.verificationResult || '未验证' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">验证人：</span>
          <span class="value">{{ currentRumor.verifier || '未验证' }}</span>
        </div>
      </div>

      <div class="action-buttons">
        <el-button type="success" @click="handleUpdateStatus('VERIFIED_TRUE')">标记为真</el-button>
        <el-button type="danger" @click="handleUpdateStatus('VERIFIED_FALSE')">标记为假</el-button>
        <el-button type="warning" @click="handleUpdateStatus('PENDING')">标记为待验证</el-button>
        <el-button type="info" @click="handleUpdateStatus('UNDER_INVESTIGATION')">标记为调查中</el-button>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑谣言" width="50%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="form.source" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRumorStore } from '@/stores/rumor'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const rumorStore = useRumorStore()
const { currentRumor, loading, fetchRumor, updateRumor, updateRumorStatus } = rumorStore

const dialogVisible = ref(false)
const form = ref({
  title: '',
  content: '',
  source: ''
})

const getStatusType = (status) => {
  const types = {
    VERIFIED_TRUE: 'success',
    VERIFIED_FALSE: 'danger',
    PENDING: 'warning',
    UNDER_INVESTIGATION: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    VERIFIED_TRUE: '已验证为真',
    VERIFIED_FALSE: '已验证为假',
    PENDING: '待验证',
    UNDER_INVESTIGATION: '调查中'
  }
  return texts[status] || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const handleEdit = () => {
  if (currentRumor.value) {
    form.value = { ...currentRumor.value }
    dialogVisible.value = true
  }
}

const handleSubmit = async () => {
  try {
    await updateRumor(route.params.id, form.value)
    dialogVisible.value = false
    fetchRumor(route.params.id)
  } catch (error) {
    // 错误处理已在 store 中完成
  }
}

const handleUpdateStatus = (status) => {
  const statusText = getStatusText(status)
  ElMessageBox.confirm(`确定要将状态更新为 ${statusText} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateRumorStatus(route.params.id, status)
      fetchRumor(route.params.id)
    } catch (error) {
      // 错误处理已在 store 中完成
    }
  })
}

onMounted(() => {
  fetchRumor(route.params.id)
})
</script>

<style scoped>
.rumor-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.label {
  width: 100px;
  font-weight: bold;
  color: #606266;
}

.value {
  flex: 1;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 