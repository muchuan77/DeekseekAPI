<template>
  <div class="system-settings">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>
      
      <el-form :model="settings" label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="settings.systemName" />
        </el-form-item>
        
        <el-form-item label="系统描述">
          <el-input
            v-model="settings.systemDescription"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="系统Logo">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleLogoSuccess"
          >
            <img v-if="settings.logo" :src="settings.logo" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="系统状态">
          <el-switch
            v-model="settings.systemStatus"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const settings = ref({
  systemName: '谣言追踪系统',
  systemDescription: '基于区块链的谣言追踪与验证系统',
  logo: '',
  systemStatus: true
})

const handleLogoSuccess = (response) => {
  settings.value.logo = response.url
  ElMessage.success('Logo上传成功')
}

const saveSettings = () => {
  // TODO: 调用API保存设置
  ElMessage.success('设置保存成功')
}
</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 