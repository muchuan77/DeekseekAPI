<template>
    <div class="analysis-container">
      <el-row :gutter="20">
        <!-- 文本分析 -->
        <el-col :span="8">
          <el-card class="analysis-card" v-loading="analysisStore.loading">
            <template #header>
              <div class="card-header">
                <span>文本分析</span>
              </div>
            </template>
            <el-form :model="textForm" :rules="textRules" ref="textFormRef" label-width="80px">
              <el-form-item label="文本内容" prop="content">
                <el-input
                  v-model="textForm.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入要分析的文本内容"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="analyzeText" :loading="analyzing.text">
                  开始分析
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
  
        <!-- 图片分析 -->
        <el-col :span="8">
          <el-card class="analysis-card" v-loading="analysisStore.loading">
            <template #header>
              <div class="card-header">
                <span>图片分析</span>
              </div>
            </template>
            <el-upload
              class="upload-demo"
              :action="analysisStore.uploadUrl"
              :on-success="handleImageSuccess"
              :before-upload="beforeImageUpload"
              :show-file-list="false"
              :headers="analysisStore.headers"
            >
              <el-button type="primary" :loading="analyzing.image">选择图片</el-button>
              <div v-if="imagePreview" class="image-preview">
                <img :src="imagePreview" class="preview-image" />
              </div>
            </el-upload>
          </el-card>
        </el-col>
  
        <!-- 视频分析 -->
        <el-col :span="8">
          <el-card class="analysis-card" v-loading="analysisStore.loading">
            <template #header>
              <div class="card-header">
                <span>视频分析</span>
              </div>
            </template>
            <el-upload
              class="upload-demo"
              :action="analysisStore.uploadUrl"
              :on-success="handleVideoSuccess"
              :before-upload="beforeVideoUpload"
              :show-file-list="false"
              :headers="analysisStore.headers"
            >
              <el-button type="primary" :loading="analyzing.video">选择视频</el-button>
              <div v-if="videoPreview" class="video-preview">
                <video :src="videoPreview" controls class="preview-video"></video>
              </div>
            </el-upload>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 综合分析按钮 -->
      <div class="analysis-actions">
        <el-button type="success" @click="analyzeAll" :disabled="!canAnalyze" :loading="analyzing.all">
          综合分析
        </el-button>
      </div>
  
      <!-- 分析结果 -->
      <el-card class="result-card" v-if="analysisResult" v-loading="analysisStore.loading">
        <template #header>
          <div class="card-header">
            <span>分析结果</span>
          </div>
        </template>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="文本分析" name="text">
            <div v-if="analysisResult.text">
              <p>可信度: {{ analysisResult.text.confidence }}%</p>
              <p>关键词: {{ analysisResult.text.keywords.join(', ') }}</p>
              <p>情感分析: {{ analysisResult.text.sentiment }}</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="图片分析" name="image">
            <div v-if="analysisResult.image">
              <p>可信度: {{ analysisResult.image.confidence }}%</p>
              <p>识别内容: {{ analysisResult.image.content }}</p>
              <p>标签: {{ analysisResult.image.tags.join(', ') }}</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="视频分析" name="video">
            <div v-if="analysisResult.video">
              <p>可信度: {{ analysisResult.video.confidence }}%</p>
              <p>关键帧: {{ analysisResult.video.keyframes }}</p>
              <p>音频分析: {{ analysisResult.video.audio }}</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="综合结果" name="summary">
            <div v-if="analysisResult.summary">
              <p>综合可信度: {{ analysisResult.summary.confidence }}%</p>
              <p>结论: {{ analysisResult.summary.conclusion }}</p>
              <p>建议: {{ analysisResult.summary.suggestion }}</p>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, reactive } from 'vue'
  import { ElMessage } from 'element-plus'
  import { useAnalysisStore } from '@/stores/analysis'
  import { useAiStore } from '@/stores/ai'
  
  const analysisStore = useAnalysisStore()
  const aiStore = useAiStore()
  
  // 表单数据
  const textForm = reactive({
    content: ''
  })
  
  // 表单验证规则
  const textRules = {
    content: [
      { required: true, message: '请输入要分析的文本内容', trigger: 'blur' },
      { min: 10, message: '文本内容不能少于10个字符', trigger: 'blur' }
    ]
  }
  
  const textFormRef = ref(null)
  const imagePreview = ref('')
  const videoPreview = ref('')
  const analysisResult = ref(null)
  const activeTab = ref('text')
  const analyzing = reactive({
    text: false,
    image: false,
    video: false,
    all: false
  })
  
  // 计算属性
  const canAnalyze = computed(() => {
    return textForm.content || imagePreview.value || videoPreview.value
  })
  
  // 方法
  const analyzeText = async () => {
    if (!textFormRef.value) return
    
    try {
      await textFormRef.value.validate()
      analyzing.text = true
      
      const result = await aiStore.analyzeText(textForm.content)
      analysisResult.value = { ...analysisResult.value, text: result }
      ElMessage.success('文本分析完成')
    } catch (error) {
      console.error('文本分析失败:', error)
      ElMessage.error(error.message || '文本分析失败')
    } finally {
      analyzing.text = false
    }
  }
  
  const beforeImageUpload = (file) => {
    const isImage = file.type.startsWith('image/')
    if (!isImage) {
      ElMessage.error('请上传图片文件')
      return false
    }
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isLt2M) {
      ElMessage.error('图片大小不能超过 2MB!')
      return false
    }
    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreview.value = e.target.result
    }
    reader.readAsDataURL(file)
    return true
  }
  
  const handleImageSuccess = async (response) => {
    try {
      analyzing.image = true
      const result = await aiStore.analyzeImage(response.url)
      analysisResult.value = { ...analysisResult.value, image: result }
      ElMessage.success('图片分析完成')
    } catch (error) {
      console.error('图片分析失败:', error)
      ElMessage.error(error.message || '图片分析失败')
    } finally {
      analyzing.image = false
    }
  }
  
  const beforeVideoUpload = (file) => {
    const isVideo = file.type.startsWith('video/')
    if (!isVideo) {
      ElMessage.error('请上传视频文件')
      return false
    }
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isLt10M) {
      ElMessage.error('视频大小不能超过 10MB!')
      return false
    }
    const reader = new FileReader()
    reader.onload = (e) => {
      videoPreview.value = e.target.result
    }
    reader.readAsDataURL(file)
    return true
  }
  
  const handleVideoSuccess = async (response) => {
    try {
      analyzing.video = true
      const result = await aiStore.analyzeVideo(response.url)
      analysisResult.value = { ...analysisResult.value, video: result }
      ElMessage.success('视频分析完成')
    } catch (error) {
      console.error('视频分析失败:', error)
      ElMessage.error(error.message || '视频分析失败')
    } finally {
      analyzing.video = false
    }
  }
  
  // 综合分析
  const analyzeAll = async () => {
    if (!canAnalyze.value) {
      ElMessage.warning('请至少输入一种分析内容')
      return
    }
  
    try {
      analyzing.all = true
      const result = await aiStore.analyzeMultiModal({
        text: textForm.content,
        image: imagePreview.value,
        video: videoPreview.value
      })
      analysisResult.value = { ...analysisResult.value, summary: result }
      activeTab.value = 'summary'
      ElMessage.success('综合分析完成')
    } catch (error) {
      console.error('综合分析失败:', error)
      ElMessage.error(error.message || '综合分析失败')
    } finally {
      analyzing.all = false
    }
  }
  </script>
  
  <style scoped>
  .analysis-container {
    padding: 20px;
  }
  
  .analysis-card {
    margin-bottom: 20px;
  }
  
  .result-card {
    margin-top: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .image-preview,
  .video-preview {
    margin-top: 10px;
    text-align: center;
  }
  
  .preview-image {
    max-width: 100%;
    max-height: 200px;
  }
  
  .preview-video {
    max-width: 100%;
    max-height: 200px;
  }

  .analysis-actions {
    margin: 20px 0;
    text-align: center;
  }
  </style>