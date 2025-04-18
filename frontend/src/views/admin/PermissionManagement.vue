<template>
  <div class="permission-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd">添加权限</el-button>
        </div>
      </template>

      <el-table :data="permissions" style="width: 100%">
        <el-table-column prop="name" label="权限名称" />
        <el-table-column prop="code" label="权限编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑权限对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加权限' : '编辑权限'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getPermissions,
  createPermission,
  updatePermission,
  deletePermission
} from '@/api/permission'

const permissions = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const form = reactive({
  name: '',
  code: '',
  description: '',
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ]
}

const loadPermissions = async () => {
  try {
    const response = await getPermissions()
    if (response.code === 200) {
      permissions.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  }
}

const handleAdd = () => {
  dialogType.value = 'add'
  form.name = ''
  form.code = ''
  form.description = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该权限吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deletePermission(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadPermissions()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = dialogType.value === 'add'
          ? await createPermission(form)
          : await updatePermission(form.id, form)
        
        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
          dialogVisible.value = false
          loadPermissions()
        }
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}

onMounted(() => {
  loadPermissions()
})
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 