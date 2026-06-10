<template>
  <div class="category-list">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <el-button type="primary" @click="showDialog(null)">
        <el-icon><Plus /></el-icon>新增分类
      </el-button>
    </div>

    <el-card shadow="hover">
      <el-table :data="list" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogForm.id ? '编辑分类' : '新增分类'"
      width="500px"
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dialogForm.name" placeholder="请输入分类名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="dialogForm.description" type="textarea" :rows="3" placeholder="请输入分类描述" maxlength="500" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="dialogForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitDialog">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/category'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogFormRef = ref(null)

const dialogForm = reactive({
  id: null,
  name: '',
  description: '',
  sortOrder: 0
})

const dialogRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    if (res.code === 200) list.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const showDialog = (row) => {
  if (row) {
    Object.assign(dialogForm, { id: row.id, name: row.name, description: row.description, sortOrder: row.sortOrder })
  } else {
    Object.assign(dialogForm, { id: null, name: '', description: '', sortOrder: 0 })
  }
  dialogVisible.value = true
}

const submitDialog = async () => {
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return

  dialogLoading.value = true
  try {
    let res
    if (dialogForm.id) {
      res = await updateCategory({ ...dialogForm })
    } else {
      res = await createCategory({ ...dialogForm })
    }
    if (res.code === 200) {
      ElMessage.success(dialogForm.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (e) {
    console.error(e)
  } finally {
    dialogLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteCategory(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  }).catch(() => {})
}

onMounted(fetchData)
</script>

<style scoped>
.category-list {
  max-width: 900px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-title {
  font-size: 20px;
  color: #1a365d;
}
</style>
