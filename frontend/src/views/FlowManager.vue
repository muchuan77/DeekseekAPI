<template>
  <div class="flow-manager">
    <el-steps
      :active="currentStep"
      finish-status="success"
      class="flow-steps"
    >
      <el-step
        title="AI分析"
        description="输入并分析内容"
      />
      <el-step
        title="创建谣言"
        description="创建谣言记录"
      />
      <el-step
        title="传播分析"
        description="分析传播路径"
      />
      <el-step
        title="查看结果"
        description="查看完整分析"
      />
    </el-steps>

    <!-- AI分析步骤 -->
    <div
      v-if="currentStep === 0"
      class="step-content"
    >
      <AI @analysis-complete="handleAnalysisComplete" />
    </div>

    <!-- 创建谣言步骤 -->
    <div
      v-if="currentStep === 1"
      class="step-content"
    >
      <el-card class="rumor-card">
        <template #header>
          <div class="card-header">
            <span>创建谣言记录</span>
          </div>
        </template>
        <el-form
          :model="rumorForm"
          label-width="100px"
        >
          <el-form-item label="标题">
            <el-input v-model="rumorForm.title" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input
              v-model="rumorForm.content"
              type="textarea"
              :rows="4"
            />
          </el-form-item>
          <el-form-item label="来源">
            <el-input v-model="rumorForm.source" />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="createRumor"
            >
              创建谣言
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 传播分析步骤 -->
    <div
      v-if="currentStep === 2"
      class="step-content"
    >
      <Propagation
        :rumor-id="currentRumorId"
        @analysis-complete="handlePropagationComplete"
      />
    </div>

    <!-- 查看结果步骤 -->
    <div
      v-if="currentStep === 3"
      class="step-content"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="result-card">
            <template #header>
              <div class="card-header">
                <span>谣言详情</span>
              </div>
            </template>
            <RumorDetail :rumor-id="currentRumorId" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="result-card">
            <template #header>
              <div class="card-header">
                <span>传播路径</span>
              </div>
            </template>
            <Trace :rumor-id="currentRumorId" />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AI from './AI.vue'
import Propagation from './Propagation.vue'
import RumorDetail from './RumorDetail.vue'
import Trace from './Trace.vue'
import { createRumor as createRumorApi } from '@/api/rumor'

const router = useRouter()
const currentStep = ref(0)
const currentRumorId = ref(null)
const analysisResult = ref(null)

const rumorForm = ref({
  title: '',
  content: '',
  source: ''
})

// 处理AI分析完成
const handleAnalysisComplete = (result) => {
  analysisResult.value = result
  rumorForm.value.content = result.analysis
  currentStep.value = 1
}

// 创建谣言
const createRumor = async () => {
  try {
    const response = await createRumorApi({
      title: rumorForm.value.title,
      content: rumorForm.value.content,
      source: rumorForm.value.source,
      status: 'PENDING'
    })

    if (response.code === 200) {
      currentRumorId.value = response.data.id
      ElMessage.success('谣言创建成功')
      currentStep.value = 2
    } else {
      ElMessage.error(response.message || '创建失败')
    }
  } catch (error) {
    console.error('创建谣言失败:', error)
    ElMessage.error('创建谣言失败，请重试')
  }
}

// 处理传播分析完成
const handlePropagationComplete = () => {
  currentStep.value = 3
}
</script>

<style scoped>
.flow-manager {
  padding: 20px;
}

.flow-steps {
  margin-bottom: 40px;
}

.step-content {
  margin-top: 20px;
}

.rumor-card,
.result-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 