<template>
  <div class="ai-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>AI 分析管理</span>
          <el-button type="primary" @click="handleConfig">配置</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="文本分析" name="text">
          <el-form :model="textForm" label-width="120px">
            <el-form-item label="分析文本">
              <el-input
                v-model="textForm.text"
                type="textarea"
                :rows="4"
                placeholder="请输入要分析的文本"
              />
            </el-form-item>
            <el-form-item label="分析类型">
              <el-select v-model="textForm.type" placeholder="请选择分析类型">
                <el-option label="情感分析" value="SENTIMENT" />
                <el-option label="可信度分析" value="CREDIBILITY" />
                <el-option label="关键词提取" value="KEYWORDS" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTextAnalysis">开始分析</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="图像分析" name="image">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleImageChange"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽图片到此处或 <em>点击上传</em>
            </div>
          </el-upload>
          <el-form :model="imageForm" label-width="120px" style="margin-top: 20px">
            <el-form-item label="分析类型">
              <el-select v-model="imageForm.type" placeholder="请选择分析类型">
                <el-option label="内容分析" value="CONTENT" />
                <el-option label="篡改检测" value="MANIPULATION" />
                <el-option label="OCR识别" value="OCR" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleImageAnalysis">开始分析</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="视频分析" name="video">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleVideoChange"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽视频到此处或 <em>点击上传</em>
            </div>
          </el-upload>
          <el-form :model="videoForm" label-width="120px" style="margin-top: 20px">
            <el-form-item label="分析类型">
              <el-select v-model="videoForm.type" placeholder="请选择分析类型">
                <el-option label="内容分析" value="CONTENT" />
                <el-option label="关键帧提取" value="KEYFRAMES" />
                <el-option label="音频分析" value="AUDIO" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleVideoAnalysis">开始分析</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="configDialogVisible" title="AI 分析配置" width="50%">
        <el-form :model="configForm" label-width="120px">
          <el-form-item label="模型选择">
            <el-select v-model="configForm.model" placeholder="请选择模型">
              <el-option label="模型A" value="modelA" />
              <el-option label="模型B" value="modelB" />
            </el-select>
          </el-form-item>
          <el-form-item label="置信度阈值">
            <el-slider v-model="configForm.confidence" :min="0" :max="1" :step="0.1" />
          </el-form-item>
          <el-form-item label="分析超时时间">
            <el-input-number v-model="configForm.timeout" :min="1" :max="60" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="configDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleConfigSubmit">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { analyzeText, analyzeImage, analyzeVideo, getAnalysisConfig, updateAnalysisConfig } from '@/api/ai'

const activeTab = ref('text')
const configDialogVisible = ref(false)

const textForm = reactive({
  text: '',
  type: 'SENTIMENT'
})

const imageForm = reactive({
  file: null,
  type: 'CONTENT'
})

const videoForm = reactive({
  file: null,
  type: 'CONTENT'
})

const configForm = reactive({
  model: 'modelA',
  confidence: 0.8,
  timeout: 30
})

const handleTextAnalysis = async () => {
  try {
    const response = await analyzeText(textForm)
    ElMessage.success('分析成功')
    console.log(response)
  } catch (error) {
    ElMessage.error('分析失败')
  }
}

const handleImageChange = (file) => {
  imageForm.file = file.raw
}

const handleImageAnalysis = async () => {
  try {
    const formData = new FormData()
    formData.append('image', imageForm.file)
    formData.append('type', imageForm.type)
    const response = await analyzeImage(formData)
    ElMessage.success('分析成功')
    console.log(response)
  } catch (error) {
    ElMessage.error('分析失败')
  }
}

const handleVideoChange = (file) => {
  videoForm.file = file.raw
}

const handleVideoAnalysis = async () => {
  try {
    const formData = new FormData()
    formData.append('video', videoForm.file)
    formData.append('type', videoForm.type)
    const response = await analyzeVideo(formData)
    ElMessage.success('分析成功')
    console.log(response)
  } catch (error) {
    ElMessage.error('分析失败')
  }
}

const handleConfig = async () => {
  try {
    const response = await getAnalysisConfig()
    Object.assign(configForm, response.data)
    configDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取配置失败')
  }
}

const handleConfigSubmit = async () => {
  try {
    await updateAnalysisConfig(configForm)
    ElMessage.success('配置更新成功')
    configDialogVisible.value = false
  } catch (error) {
    ElMessage.error('配置更新失败')
  }
}
</script>

<style scoped>
.ai-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-demo {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}
</style> 