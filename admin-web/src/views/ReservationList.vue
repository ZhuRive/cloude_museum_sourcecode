<template>
  <div class="reservation-list">
    <div class="page-header">
      <h2 class="page-title">预约管理</h2>
    </div>

    <!-- 统计 -->
    <el-row :gutter="16" style="margin-bottom:16px;">
      <el-col :span="4" v-for="s in statusStats" :key="s.key">
        <el-card shadow="hover" :body-style="{ padding: '12px', textAlign: 'center' }">
          <div style="font-size:12px;color:#999;">{{ s.label }}</div>
          <div style="font-size:24px;font-weight:bold;color:#1a365d;">{{ stats[s.key] || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" size="default">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:140px" @change="fetchData">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" style="margin-top:16px;">
      <el-table :data="list" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="visitorName" label="参观人" width="100" />
        <el-table-column prop="visitorPhone" label="手机号" width="130" />
        <el-table-column prop="visitDate" label="参观日期" width="120" />
        <el-table-column prop="visitTimeSlot" label="时段" width="80" align="center" />
        <el-table-column prop="visitorCount" label="人数" width="60" align="center" />
        <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" type="success" @click="handleApprove(row)">通过</el-button>
              <el-button size="small" type="warning" @click="showReject(row)">拒绝</el-button>
            </template>
            <template v-else-if="row.status === 'approved'">
              <el-button size="small" type="primary" @click="handleComplete(row)">完成</el-button>
            </template>
            <el-tag v-else type="info" size="small">无操作</el-tag>
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

    <!-- 拒绝理由弹窗 -->
    <el-dialog v-model="rejectVisible" title="拒绝理由" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" :loading="rejectLoading" @click="confirmReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReservationList, approveReservation, rejectReservation, completeReservation, getReservationStats } from '@/api/reservation'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const stats = ref({ total: 0, pending: 0, approved: 0, completed: 0, cancelled: 0 })
const rejectVisible = ref(false)
const rejectLoading = ref(false)
const rejectReason = ref('')
const currentRejectId = ref(null)

const query = reactive({ page: 1, size: 10, status: null })

const statusStats = [
  { key: 'total', label: '总计' },
  { key: 'pending', label: '待审核' },
  { key: 'approved', label: '已通过' },
  { key: 'completed', label: '已完成' },
  { key: 'cancelled', label: '已取消' }
]

const statusType = (s) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger', cancelled: 'info', completed: 'primary' }
  return map[s] || 'info'
}
const statusLabel = (s) => {
  const map = { pending: '待审核', approved: '已通过', rejected: '已拒绝', cancelled: '已取消', completed: '已完成' }
  return map[s] || s
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getReservationList({
      page: query.page,
      size: query.size,
      status: query.status || undefined
    })
    if (res.code === 200) {
      list.value = res.data.list
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const res = await getReservationStats()
    if (res.code === 200) stats.value = res.data
  } catch (e) { console.error(e) }
}

const handleApprove = async (row) => {
  try {
    const res = await approveReservation(row.id)
    if (res.code === 200) {
      ElMessage.success('已通过')
      fetchData()
      fetchStats()
    }
  } catch (e) { console.error(e) }
}

const showReject = (row) => {
  currentRejectId.value = row.id
  rejectReason.value = ''
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  rejectLoading.value = true
  try {
    const res = await rejectReservation(currentRejectId.value, rejectReason.value)
    if (res.code === 200) {
      ElMessage.success('已拒绝')
      rejectVisible.value = false
      fetchData()
      fetchStats()
    }
  } catch (e) { console.error(e) }
  finally { rejectLoading.value = false }
}

const handleComplete = async (row) => {
  try {
    const res = await completeReservation(row.id)
    if (res.code === 200) {
      ElMessage.success('已完成')
      fetchData()
      fetchStats()
    }
  } catch (e) { console.error(e) }
}

onMounted(() => {
  fetchStats()
  fetchData()
})
</script>

<style scoped>
.reservation-list {
  max-width: 1200px;
}
.page-header {
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
