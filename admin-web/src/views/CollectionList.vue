<template>
  <div class="collection-list">
    <div class="page-header">
      <h2 class="page-title">藏品管理</h2>
      <el-button type="primary" @click="$router.push('/collection/add')">
        <el-icon><Plus /></el-icon>新增藏品
      </el-button>
    </div>

    <!-- 搜索与筛选 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="query" size="default">
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable style="width:150px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:150px">
            <el-option label="展出中" value="on_display" />
            <el-option label="库房中" value="in_storage" />
            <el-option label="修复中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" style="margin-top:16px;">
      <el-table :data="list" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="封面" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.coverImage || 'https://via.placeholder.com/60x60?text=无'"
              style="width:50px;height:50px;border-radius:4px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="藏品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="dynasty" label="朝代" width="100" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/collection/edit/${row.id}`)">
              编辑
            </el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCollectionPage, deleteCollection } from '@/api/collection'
import { getCategoryList } from '@/api/category'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const categories = ref([])

const query = reactive({
  page: 1,
  size: 10,
  categoryId: null,
  status: null
})

const statusType = (s) => {
  const map = { on_display: 'success', in_storage: 'info', maintenance: 'warning' }
  return map[s] || 'info'
}
const statusLabel = (s) => {
  const map = { on_display: '展出中', in_storage: '库房中', maintenance: '修复中' }
  return map[s] || s
}

const resetQuery = () => {
  query.categoryId = null
  query.status = null
  query.page = 1
  fetchData()
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCollectionPage({
      page: query.page,
      size: query.size,
      categoryId: query.categoryId || undefined,
      status: query.status || undefined
    })
    if (res.code === 200) {
      list.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除藏品「${row.name}」吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteCollection(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
  fetchData()
})
</script>

<style scoped>
.collection-list {
  max-width: 1200px;
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
.search-card {
  border-radius: 8px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
