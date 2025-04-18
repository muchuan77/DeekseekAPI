<template>
  <div class="rumors-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>谣言列表</span>
          <el-button type="primary" @click="handleAdd">添加谣言</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="请输入标题或内容" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px;">
            <el-option label="待验证" value="PENDING" />
            <el-option label="已验证为真" value="VERIFIED_TRUE" />
            <el-option label="已验证为假" value="VERIFIED_FALSE" />
            <el-option label="调查中" value="UNDER_INVESTIGATION" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table
        v-loading="loading"
        :data="paginatedRumors"
        style="width: 100%"
      >
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="source" label="来源" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="verifyTime" label="验证时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row.id)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑谣言对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加谣言' : '编辑谣言'"
      width="50%"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" placeholder="请输入来源" />
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
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRumorStore } from '@/stores/rumor'

const router = useRouter()
const rumorStore = useRumorStore()
const loading = ref(false)
const rumors = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 计算当前页显示的数据
const paginatedRumors = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return rumors.value.slice(start, end)
})

// 搜索表单
const searchForm = ref({
  keyword: '',
  status: ''
})

// 从 store 中获取方法
const { fetchRumors, createRumor, updateRumor, deleteRumor } = rumorStore

// 监听 store 中的 rumors 变化
watch(() => rumorStore.rumors, (newRumors) => {
  rumors.value = newRumors
  total.value = newRumors.length
}, { immediate: true })

const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const form = ref({
  title: '',
  content: '',
  source: ''
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
  source: [
    { required: true, message: '请输入来源', trigger: 'blur' }
  ]
}

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

const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    title: '',
    content: '',
    source: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleView = (id) => {
  router.push(`/rumor/${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条谣言吗？', '提示', {
      type: 'warning'
    })
    await deleteRumor(id)
    ElMessage.success('删除成功')
    fetchRumors()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await createRumor(form.value)
        } else {
          await updateRumor(form.value.id, form.value)
        }
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
        dialogVisible.value = false
        fetchRumors()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}

const handleSearch = () => {
  rumorStore.updateSearchParams(searchForm.value)
}

const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    status: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  rumorStore.pageSize = val
  rumorStore.currentPage = 0
  rumorStore.fetchRumors(rumorStore.searchParams)
}

const handleCurrentChange = (val) => {
  rumorStore.currentPage = val - 1 // 转换为0-based
  rumorStore.fetchRumors(rumorStore.searchParams)
}

onMounted(() => {
  rumorStore.fetchRumors(rumorStore.searchParams)
})
</script>

<style scoped>
.rumors-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 