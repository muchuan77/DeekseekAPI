<template>
  <div class="ai-analysis-container">
    <el-card class="analysis-card">
      <template #header>
        <div class="card-header">
          <span>AI 分析</span>
          <el-radio-group
            v-model="analysisType"
            size="small"
          >
            <el-radio-button value="text">
              文本分析
            </el-radio-button>
            <el-radio-button value="image">
              图片分析
            </el-radio-button>
            <el-radio-button value="video">
              视频分析
            </el-radio-button>
            <el-radio-button value="multimodal">
              多模态分析
            </el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 文本分析 -->
      <div
        v-if="analysisType === 'text'"
        class="text-analysis"
      >
        <el-input
          v-model="textContent"
          type="textarea"
          :rows="6"
          placeholder="请输入要分析的文本内容"
        />
        <div class="button-group">
          <el-button
            type="primary"
            :loading="loading"
            @click="analyzeTextContent"
          >
            开始分析
          </el-button>
        </div>
      </div>

      <!-- 图片分析 -->
      <div
        v-if="analysisType === 'image'"
        class="image-analysis"
      >
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleImageChange"
          :show-file-list="false"
        >
          <el-icon class="el-icon--upload">
            <component :is="icons.Upload" />
          </el-icon>
          <div class="el-upload__text">
            拖拽图片到此处或 <em>点击上传</em>
          </div>
        </el-upload>
        <div class="button-group">
          <el-button
            type="primary"
            :loading="loading"
            @click="analyzeImageContent"
          >
            开始分析
          </el-button>
        </div>
      </div>

      <!-- 视频分析 -->
      <div
        v-if="analysisType === 'video'"
        class="video-analysis"
      >
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleVideoChange"
          :show-file-list="false"
        >
          <el-icon class="el-icon--upload">
            <component :is="icons.Upload" />
          </el-icon>
          <div class="el-upload__text">
            拖拽视频到此处或 <em>点击上传</em>
          </div>
        </el-upload>
        <div class="button-group">
          <el-button
            type="primary"
            :loading="loading"
            @click="analyzeVideoContent"
          >
            开始分析
          </el-button>
        </div>
      </div>

      <!-- 多模态分析 -->
      <div
        v-if="analysisType === 'multimodal'"
        class="multimodal-analysis"
      >
        <div class="text-input">
          <el-input
            v-model="textContent"
            type="textarea"
            :rows="4"
            placeholder="请输入要分析的文本内容（可选）"
          />
        </div>
        
        <div class="image-upload">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleImageChange"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
          >
            <el-icon class="el-icon--upload">
              <component :is="icons.Upload" />
            </el-icon>
            <div class="el-upload__text">
              拖拽图片到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg/png 格式，大小不超过 5MB
              </div>
            </template>
          </el-upload>
        </div>
        
        <div class="video-upload">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleVideoChange"
            :show-file-list="false"
            :before-upload="beforeVideoUpload"
          >
            <el-icon class="el-icon--upload">
              <component :is="icons.Upload" />
            </el-icon>
            <div class="el-upload__text">
              拖拽视频到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 mp4 格式，大小不超过 50MB
              </div>
            </template>
          </el-upload>
        </div>
        
        <div class="button-group">
          <el-button
            type="primary"
            :loading="loading"
            @click="analyzeMultiModalContent"
          >
            开始分析
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 分析进度 -->
    <el-dialog
      v-model="showProgress"
      title="分析中..."
      width="30%"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="progress-dialog">
        <el-progress
          :percentage="progressPercentage"
          :indeterminate="true"
          :duration="3"
          :stroke-width="20"
          status="warning"
        />
        <div class="progress-steps">
          <div
            v-for="(step, index) in progressSteps"
            :key="index" 
            :class="['progress-step', { 'active': currentStep >= index }]"
          >
            <el-icon v-if="currentStep > index">
              <component :is="icons.Check" />
            </el-icon>
            <el-icon v-else-if="currentStep === index">
              <component :is="icons.Loading" />
            </el-icon>
            <el-icon v-else>
              <component :is="icons.CircleCheck" />
            </el-icon>
            <span>{{ step }}</span>
          </div>
        </div>
        <div class="progress-message">
          {{ progressMessage }}
        </div>
      </div>
    </el-dialog>

    <!-- 分析结果 -->
    <el-card
      v-if="analysisResult"
      class="result-card"
    >
      <template #header>
        <div class="card-header">
          <span>分析结果</span>
          <div class="header-actions">
            <el-button
              size="small"
              type="success"
              @click="exportResult"
            >
              <el-icon>
                <component :is="icons.Download" />
              </el-icon>
              导出分析结果
            </el-button>
          </div>
        </div>
      </template>
      <div class="result-content">
        <!-- 可信度评分 -->
        <div class="result-item">
          <h3>可信度评分</h3>
          <el-progress
            :percentage="Math.round((analysisResult?.credibilityScore || 0) * 100)"
            :format="(percentage) => percentage ? `${percentage}%` : '0%'"
            :status="getCredibilityStatus(analysisResult?.credibilityScore)"
          />
          <div class="score-description">
            {{ getCredibilityDescription(analysisResult?.credibilityScore) }}
          </div>
        </div>

        <!-- 事实核查要点 -->
        <div class="result-item">
          <h3>事实核查要点</h3>
          <el-timeline v-if="analysisResult?.factCheckingPoints?.length">
            <el-timeline-item
              v-for="(point, index) in analysisResult.factCheckingPoints"
              :key="index"
              type="success"
            >
              {{ point }}
            </el-timeline-item>
          </el-timeline>
          <div
            v-else
            class="empty-text"
          >
            暂无事实核查要点
          </div>
        </div>

        <!-- 虚假信息指标 -->
        <div class="result-item">
          <h3>虚假信息指标</h3>
          <el-alert
            v-for="(indicator, index) in analysisResult?.misinformationIndicators"
            :key="index"
            :title="indicator"
            type="warning"
            :closable="false"
            show-icon
            class="indicator-item"
          />
          <div
            v-if="!analysisResult?.misinformationIndicators?.length"
            class="empty-text"
          >
            暂无虚假信息指标
          </div>
        </div>

        <!-- 验证建议 -->
        <div class="result-item">
          <h3>验证建议</h3>
          <el-card
            shadow="never"
            class="verification-card"
          >
            <template #header>
              <div class="verification-header">
                <el-icon>
                  <component :is="icons.InfoFilled" />
                </el-icon>
                <span>建议采取以下步骤进行验证</span>
              </div>
            </template>
            <p>{{ analysisResult?.verificationRecommendation || '暂无验证建议' }}</p>
          </el-card>
        </div>

        <!-- 来源分析 -->
        <div class="result-item">
          <h3>来源分析</h3>
          <el-card
            shadow="hover"
            class="source-analysis-card"
          >
            <div class="source-reliability">
              <span class="label">可靠性评分:</span>
              <el-progress
                :percentage="Math.round((analysisResult?.sourceAnalysis?.reliability || 0) * 100)"
                :format="(percentage) => percentage ? `${percentage}%` : '0%'"
                :status="getReliabilityStatus(analysisResult?.sourceAnalysis?.reliability)"
              />
            </div>
            <div class="source-reputation">
              <span class="label">声誉:</span>
              <el-tag :type="getReputationType(analysisResult?.sourceAnalysis?.reputation)">
                {{ analysisResult?.sourceAnalysis?.reputation || '未知' }}
              </el-tag>
            </div>
            <div class="source-concerns">
              <h4>关注点:</h4>
              <el-collapse v-if="analysisResult?.sourceAnalysis?.concerns?.length">
                <el-collapse-item title="查看详细关注点">
                  <el-tag
                    v-for="(concern, index) in analysisResult.sourceAnalysis.concerns"
                    :key="index"
                    class="ml-2"
                    type="warning"
                  >
                    {{ concern }}
                  </el-tag>
                </el-collapse-item>
              </el-collapse>
              <div
                v-else
                class="empty-text"
              >
                暂无关注点
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, defineComponent, h } from 'vue'
import { ElMessage } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { analyzeText, analyzeImage, analyzeVideo } from '@/api/ai'

// 定义图标组件映射
const icons = {}
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  icons[key] = component
}

const analysisType = ref('text')
const textContent = ref('')
const imageFile = ref(null)
const videoFile = ref(null)
const loading = ref(false)
const analysisResult = ref(null)

// 进度对话框相关
const showProgress = ref(false)
const progressPercentage = ref(0)
const progressSteps = [
  '准备分析内容',
  '向AI发送请求',
  '等待AI分析',
  '处理分析结果'
]
const currentStep = ref(0)
const progressMessage = ref('正在准备分析...')

// 进度条更新函数
const updateProgress = (step, message) => {
  currentStep.value = step
  progressMessage.value = message
  progressPercentage.value = (step / (progressSteps.length - 1)) * 100
}

// 模拟进度更新
const startProgressSimulation = () => {
  currentStep.value = 0
  progressPercentage.value = 0
  showProgress.value = true
  
  updateProgress(0, '正在准备分析内容...')
  
  // 模拟第二步延迟
  setTimeout(() => {
    updateProgress(1, '正在向AI发送请求...')
    
    // 模拟第三步延迟
    setTimeout(() => {
      updateProgress(2, '正在等待AI分析结果...')
    }, 2000)
  }, 1000)
}

const stopProgressSimulation = () => {
  updateProgress(3, '正在处理分析结果...')
  
  // 显示完成后延迟关闭
  setTimeout(() => {
    showProgress.value = false
  }, 1000)
}

// 获取可信度状态
const getCredibilityStatus = (score) => {
  if (!score) return ''
  if (score >= 0.7) return 'success'
  if (score >= 0.4) return 'warning'
  return 'exception'
}

// 获取可信度描述
const getCredibilityDescription = (score) => {
  if (score === null || score === undefined) return '暂无评分'
  if (score >= 0.7) return '该信息可信度较高'
  if (score >= 0.4) return '该信息可信度一般，建议进一步核实'
  if (score > 0) return '该信息可信度较低，存在虚假信息风险'
  return '该信息完全不可信，存在严重虚假信息风险'
}

// 获取可靠性状态
const getReliabilityStatus = (score) => {
  if (!score) return ''
  if (score >= 0.7) return 'success'
  if (score >= 0.4) return 'warning'
  return 'exception'
}

// 获取声誉类型
const getReputationType = (reputation) => {
  if (!reputation || reputation === '未知') return 'info'
  if (reputation.includes('官方') || reputation.includes('权威')) return 'success'
  if (reputation.includes('不可靠') || reputation.includes('传闻')) return 'warning'
  return 'info'
}

// 导出分析结果
const exportResult = () => {
  if (!analysisResult.value) {
    ElMessage.warning('没有可导出的分析结果')
    return
  }
  
  try {
    // 创建要导出的内容
    const content = `
谣言分析报告
==============================

分析内容: ${textContent.value.substring(0, 100)}${textContent.value.length > 100 ? '...' : ''}

可信度评分: ${(analysisResult.value.credibilityScore * 100).toFixed(1)}%
可信度评估: ${getCredibilityDescription(analysisResult.value.credibilityScore)}

事实核查要点:
${analysisResult.value.factCheckingPoints.map(point => `- ${point}`).join('\n') || '暂无事实核查要点'}

虚假信息指标:
${analysisResult.value.misinformationIndicators.map(indicator => `- ${indicator}`).join('\n') || '暂无虚假信息指标'}

验证建议:
${analysisResult.value.verificationRecommendation || '暂无验证建议'}

来源分析:
- 可靠性评分: ${(analysisResult.value.sourceAnalysis?.reliability * 100).toFixed(1)}%
- 声誉: ${analysisResult.value.sourceAnalysis?.reputation || '未知'}
- 关注点:
${analysisResult.value.sourceAnalysis?.concerns.map(concern => `  * ${concern}`).join('\n') || '  暂无关注点'}

==============================
分析时间: ${new Date().toLocaleString()}
`
    
    // 创建Blob对象
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
    
    // 创建临时下载链接并点击
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `谣言分析报告_${new Date().toISOString().slice(0,10)}.txt`
    link.click()
    
    ElMessage.success('分析报告已导出')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
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
  startProgressSimulation()
  
  try {
    const result = await analyzeText({
      content: textContent.value,
      type: 'TEXT',
      source: 'user_input',
      title: '文本分析'
    })
    
    if (result && result.code === 200) {
      // 直接使用返回的数据，因为格式已经匹配
      analysisResult.value = result.data
      handleAnalysisComplete(result.data)
      ElMessage.success('分析完成')
    } else {
      ElMessage.error(result?.message || '分析失败')
    }
  } catch (error) {
    console.error('分析失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '分析失败')
  } finally {
    stopProgressSimulation()
    loading.value = false
  }
}

const analyzeImageContent = async () => {
  if (!imageFile.value) {
    ElMessage.warning('请上传要分析的图片')
    return
  }
  
  loading.value = true
  startProgressSimulation()
  
  try {
    const formData = new FormData()
    formData.append('file', imageFile.value)
    formData.append('type', 'IMAGE')
    formData.append('source', 'user_input')
    formData.append('title', '图片分析')
    
    const result = await analyzeImage(formData)
    if (result && result.code === 200) {
      analysisResult.value = result.data
      handleAnalysisComplete(result.data)
      ElMessage.success('分析完成')
    } else {
      ElMessage.error(result?.message || '分析失败')
    }
  } catch (error) {
    console.error('分析失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '分析失败')
  } finally {
    stopProgressSimulation()
    loading.value = false
  }
}

const analyzeVideoContent = async () => {
  if (!videoFile.value) {
    ElMessage.warning('请上传要分析的视频')
    return
  }
  
  loading.value = true
  startProgressSimulation()
  
  try {
    const formData = new FormData()
    formData.append('file', videoFile.value)
    formData.append('type', 'VIDEO')
    formData.append('source', 'user_input')
    formData.append('title', '视频分析')
    
    const result = await analyzeVideo(formData)
    if (result && result.code === 200) {
      analysisResult.value = result.data
      handleAnalysisComplete(result.data)
      ElMessage.success('分析完成')
    } else {
      ElMessage.error(result?.message || '分析失败')
    }
  } catch (error) {
    console.error('分析失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '分析失败')
  } finally {
    stopProgressSimulation()
    loading.value = false
  }
}

// 文件上传前的验证
const beforeImageUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const beforeVideoUpload = (file) => {
  const isVideo = file.type === 'video/mp4'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isVideo) {
    ElMessage.error('只能上传 MP4 格式的视频!')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('视频大小不能超过 50MB!')
    return false
  }
  return true
}

// 多模态分析
const analyzeMultiModalContent = async () => {
  if (!textContent.value && !imageFile.value && !videoFile.value) {
    ElMessage.warning('请至少提供一种分析内容')
    return
  }
  
  loading.value = true
  startProgressSimulation()
  
  try {
    const formData = new FormData()
    
    // 添加文本内容
    if (textContent.value) {
      formData.append('content', textContent.value)
    }
    
    // 添加图片文件
    if (imageFile.value) {
      formData.append('image', imageFile.value)
    }
    
    // 添加视频文件
    if (videoFile.value) {
      formData.append('video', videoFile.value)
    }
    
    formData.append('type', 'MULTIMODAL')
    formData.append('source', 'user_input')
    formData.append('title', '多模态分析')
    
    const result = await analyzeMultiModal(formData)
    if (result && result.code === 200) {
      analysisResult.value = result.data
      handleAnalysisComplete(result.data)
      ElMessage.success('分析完成')
    } else {
      ElMessage.error(result?.message || '分析失败')
    }
  } catch (error) {
    console.error('分析失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '分析失败')
  } finally {
    stopProgressSimulation()
    loading.value = false
  }
}

// 在分析完成后添加事件触发
const handleAnalysisComplete = (result) => {
  emit('analysis-complete', {
    analysis: result.analysis,
    confidence: result.confidence,
    details: result.details
  })
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

.header-actions {
  display: flex;
  gap: 10px;
}

.button-group {
  margin-top: 20px;
  text-align: center;
}

.result-content {
  padding: 20px;
}

.result-item {
  margin-bottom: 30px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.result-item h3 {
  margin-bottom: 16px;
  color: #303133;
  font-weight: 600;
}

.result-item h4 {
  margin: 12px 0;
  color: #606266;
}

.score-description {
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.indicator-item {
  margin-bottom: 10px;
}

.verification-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.source-analysis-card {
  background: #f8f9fa;
}

.source-reliability,
.source-reputation {
  margin-bottom: 16px;
}

.label {
  display: inline-block;
  width: 100px;
  color: #606266;
}

.ml-2 {
  margin: 4px;
}

.empty-text {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
}

.verification-card {
  border: 1px solid #e4e7ed;
}

.source-concerns {
  margin-top: 16px;
}

/* 进度对话框样式 */
.progress-dialog {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-steps {
  margin-top: 30px;
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
  font-size: 12px;
  width: 25%;
  text-align: center;
}

.progress-step.active {
  color: #409EFF;
  font-weight: bold;
}

.progress-step .el-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.progress-message {
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

:deep(.el-timeline-item__content) {
  color: #303133;
}

:deep(.el-collapse-item__header) {
  font-size: 14px;
  color: #606266;
}

:deep(.el-progress-bar__inner) {
  transition: all 0.3s ease;
}

:deep(.el-card__header) {
  padding: 12px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.multimodal-analysis {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.text-input {
  margin-bottom: 20px;
}

.image-upload,
.video-upload {
  margin-bottom: 20px;
}

.upload-demo {
  width: 100%;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 7px;
}

.el-upload-dragger {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.el-upload__text {
  margin-top: 10px;
  color: #606266;
}

.el-upload__text em {
  color: #409EFF;
  font-style: normal;
}
</style> 