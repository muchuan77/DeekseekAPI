<template>
  <div class="detection">
    <el-card shadow="hover" v-loading="detecting">
      <template #header>
        <div class="card-header">
          <span>谣言检测</span>
        </div>
      </template>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入需要检测的内容"
          />
        </el-form-item>
        
        <el-form-item label="来源" prop="source">
          <el-select v-model="form.source" placeholder="请选择来源">
            <el-option label="社交媒体" value="social" />
            <el-option label="新闻网站" value="news" />
            <el-option label="论坛" value="forum" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="元数据" prop="metadata">
          <el-input
            v-model="form.metadata"
            type="textarea"
            :rows="3"
            placeholder="请输入相关信息（如作者、发布时间等）"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleDetect" :loading="detecting">
            开始检测
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" class="result-card" v-if="result" v-loading="saving">
      <template #header>
        <div class="card-header">
          <span>检测结果</span>
          <el-tag :type="getResultType(result.veracity)">
            {{ getResultText(result.veracity) }}
          </el-tag>
        </div>
      </template>
      
      <div class="result-content">
        <div class="result-item">
          <span class="label">可信度：</span>
          <el-progress
            :percentage="result.confidence * 100"
            :status="getConfidenceStatus(result.confidence)"
          />
        </div>
        
        <div class="result-item">
          <span class="label">分析：</span>
          <div class="analysis">{{ result.analysis }}</div>
        </div>
        
        <div class="result-item" v-if="result.evidence && result.evidence.length > 0">
          <span class="label">证据：</span>
          <div class="evidence">
            <el-tag
              v-for="(item, index) in result.evidence"
              :key="index"
              class="evidence-tag"
            >
              {{ item }}
            </el-tag>
          </div>
        </div>
        
        <div class="result-actions">
          <el-button type="primary" @click="saveResult" :loading="saving">
            保存结果
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useDetectionStore } from '@/stores/detection'

const router = useRouter()
const detectionStore = useDetectionStore()
const formRef = ref(null)

// 表单数据
const form = reactive({
  content: '',
  source: '',
  metadata: ''
})

// 表单验证规则
const rules = {
  content: [
    { required: true, message: '请输入需要检测的内容', trigger: 'blur' },
    { min: 10, message: '内容长度不能小于10个字符', trigger: 'blur' }
  ],
  source: [
    { required: true, message: '请选择来源', trigger: 'change' }
  ]
}

// 检测结果
const result = ref(null)
const detecting = ref(false)
const saving = ref(false)

// 处理检测
const handleDetect = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    detecting.value = true
    
    const { data } = await detectionStore.detectRumor(form)
    result.value = data.data
    
    ElMessage.success('检测完成')
  } catch (error) {
    console.error('检测失败:', error)
    ElMessage.error(error.message || '检测失败')
  } finally {
    detecting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  result.value = null
}

// 获取结果类型
const getResultType = (veracity) => {
  const types = {
    true: 'success',
    false: 'danger',
    unknown: 'warning'
  }
  return types[veracity] || 'info'
}

// 获取结果文本
const getResultText = (veracity) => {
  const texts = {
    true: '真实',
    false: '虚假',
    unknown: '待验证'
  }
  return texts[veracity] || '未知'
}

// 获取可信度状态
const getConfidenceStatus = (confidence) => {
  if (confidence >= 0.8) return 'success'
  if (confidence >= 0.5) return 'warning'
  return 'exception'
}

// 保存结果
const saveResult = async () => {
  if (!result.value) return
  
  saving.value = true
  try {
    const savedRumor = await detectionStore.saveRumor({
      ...form,
      ...result.value
    })
    
    ElMessage.success('保存成功')
    router.push(`/rumors/${savedRumor.id}`)
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.detection {
  padding: 20px;
}

.result-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-content {
  padding: 20px 0;
}

.result-item {
  margin-bottom: 20px;
}

.result-item:last-child {
  margin-bottom: 0;
}

.label {
  font-weight: bold;
  margin-right: 10px;
}

.analysis {
  margin-top: 10px;
  line-height: 1.6;
}

.evidence {
  margin-top: 10px;
}

.evidence-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

.result-actions {
  margin-top: 20px;
  text-align: right;
}
</style> 