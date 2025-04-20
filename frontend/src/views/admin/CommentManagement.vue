<template>
  <div class="comment-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.rumorId" placeholder="谣言ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="comments" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="评论内容" />
        <el-table-column prop="username" label="评论用户" width="120" />
        <el-table-column prop="rumorTitle" label="相关谣言" width="200" />
        <el-table-column prop="createTime" label="评论时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ row.status === 'ACTIVE' ? '正常' : '已删除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'ACTIVE'"
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleRestore(row)"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCommentStore } from '@/stores/comment'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const authStore = useAuthStore()
const commentStore = useCommentStore()
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  rumorId: '1',
  page: 0,
  size: 10
})

// 计算属性
const comments = computed(() => commentStore.comments)
const total = computed(() => commentStore.totalComments)

const fetchComments = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      rumorId: searchForm.rumorId || '1'
    }
    await commentStore.fetchComments(params)
    if (comments.value.length === 0) {
      ElMessage.info('暂无评论数据')
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    if (error.response?.status === 401) {
      authStore.clearAuth()
      router.push('/login')
    } else {
      ElMessage.error('获取评论列表失败')
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchComments()
}

const resetSearch = () => {
  searchForm.rumorId = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchComments()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchComments()
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
      type: 'warning'
    })
    await commentStore.deleteComment(row.id)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

const handleRestore = async (row) => {
  try {
    await ElMessageBox.confirm('确定要恢复该评论吗？', '提示', {
      type: 'warning'
    })
    await commentStore.updateComment(row.id, { status: 'ACTIVE' })
    ElMessage.success('恢复成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败')
      console.error(error)
    }
  }
}

onMounted(() => {
  // 检查用户是否已登录
  if (!authStore.token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  fetchComments()
})
</script>

<style scoped>
.comment-management {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 