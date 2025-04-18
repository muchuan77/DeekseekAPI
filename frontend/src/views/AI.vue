<template>
  <div class="ai-analysis-container">
    <el-card class="analysis-card">
      <template #header>
        <div class="card-header">
          <span>AI 分析</span>
          <el-radio-group v-model="analysisType" size="small">
            <el-radio-button value="text">文本分析</el-radio-button>
            <el-radio-button value="image">图片分析</el-radio-button>
            <el-radio-button value="video">视频分析</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 文本分析 -->
      <div v-if="analysisType === 'text'" class="text-analysis">
        <el-input
          v-model="textContent"
          type="textarea"
          :rows="6"
          placeholder="请输入要分析的文本内容"
        />
        <div class="button-group">
          <el-button type="primary" @click="analyzeTextContent" :loading="loading">
            开始分析
          </el-button>
        </div>
      </div>

      <!-- 图片分析 -->
      <div v-if="analysisType === 'image'" class="image-analysis">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleImageChange"
          :show-file-list="false"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽图片到此处或 <em>点击上传</em>
          </div>
        </el-upload>
        <div class="button-group">
          <el-button type="primary" @click="analyzeImageContent" :loading="loading">
            开始分析
          </el-button>
        </div>
      </div>

      <!-- 视频分析 -->
      <div v-if="analysisType === 'video'" class="video-analysis">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleVideoChange"
          :show-file-list="false"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽视频到此处或 <em>点击上传</em>
          </div>
        </el-upload>
        <div class="button-group">
          <el-button type="primary" @click="analyzeVideoContent" :loading="loading">
            开始分析
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 分析结果 -->
    <el-card v-if="analysisResult" class="result-card">
      <template #header>
        <div class="card-header">
          <span>分析结果</span>
        </div>
      </template>
      <div class="result-content">
        <div class="result-item">
          <h3>可信度评分</h3>
          <el-progress
            :percentage="analysisResult.credibilityScore * 100"
            :format="format"
          />
        </div>
        <div class="result-item">
          <h3>事实核查要点</h3>
          <el-tag
            v-for="(point, index) in analysisResult.factCheckingPoints"
            :key="index"
            class="ml-2"
            type="success"
          >
            {{ point }}
          </el-tag>
        </div>
        <div class="result-item">
          <h3>虚假信息指标</h3>
          <el-tag
            v-for="(indicator, index) in analysisResult.misinformationIndicators"
            :key="index"
            class="ml-2"
            type="danger"
          >
            {{ indicator }}
          </el-tag>
        </div>
        <div class="result-item">
          <h3>验证建议</h3>
          <p>{{ analysisResult.verificationRecommendation }}</p>
        </div>
        <div class="result-item">
          <h3>来源分析</h3>
          <p>可靠性评分: {{ analysisResult.sourceAnalysis.reliability }}</p>
          <p>声誉: {{ analysisResult.sourceAnalysis.reputation }}</p>
          <div>
            <h4>关注点:</h4>
            <el-tag
              v-for="(concern, index) in analysisResult.sourceAnalysis.concerns"
              :key="index"
              class="ml-2"
              type="warning"
            >
              {{ concern }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { analyzeText, analyzeImage, analyzeVideo } from '@/api/ai'

const analysisType = ref('text')
const textContent = ref('')
const imageFile = ref(null)
const videoFile = ref(null)
const loading = ref(false)
const analysisResult = ref(null)

const format = (percentage) => {
  return percentage.toFixed(2) + '%'
}

const handleImageChange = (file) => {
  imageFile.value = file.raw
}

const handleVideoChange = (file) => {
  videoFile.value = file.raw
}

const analyzeTextContent = async () => {
  if (!textContent.value) {
    ElMessage.warning('请输入要分析的文本内容')
    return
  }
  loading.value = true
  try {
    const result = await analyzeText(textContent.value)
    if (result) {
      analysisResult.value = result
    }
  } catch (error) {
    ElMessage.error('分析失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const analyzeImageContent = async () => {
  if (!imageFile.value) {
    ElMessage.warning('请上传要分析的图片')
    return
  }
  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', imageFile.value)
    const result = await analyzeImage(formData)
    if (result) {
      analysisResult.value = result
    }
  } catch (error) {
    ElMessage.error('分析失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const analyzeVideoContent = async () => {
  if (!videoFile.value) {
    ElMessage.warning('请上传要分析的视频')
    return
  }
  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', videoFile.value)
    const result = await analyzeVideo(formData)
    if (result) {
      analysisResult.value = result
    }
  } catch (error) {
    ElMessage.error('分析失败：' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ai-analysis-container {
  padding: 20px;
}

.analysis-card,
.result-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button-group {
  margin-top: 20px;
  text-align: center;
}

.result-content {
  padding: 20px;
}

.result-item {
  margin-bottom: 20px;
}

.result-item h3 {
  margin-bottom: 10px;
}

.result-item h4 {
  margin: 10px 0;
}

.ml-2 {
  margin-left: 8px;
  margin-bottom: 8px;
}

.upload-demo {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}
</style> 