<template>
  <div class="reservations-page">
    <van-nav-bar title="我 的 预 约" left-arrow @click-left="router.back()" />

    <!-- 状态标签 -->
    <van-tabs v-model:active="activeTab" @change="fetchData" line-width="24" color="var(--gold)" title-active-color="var(--gold)">
      <van-tab title="全部" name="all" />
      <van-tab title="待审核" name="pending" />
      <van-tab title="已通过" name="approved" />
      <van-tab title="已完成" name="completed" />
    </van-tabs>

    <!-- 预约列表 -->
    <div class="list-wrap">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="—— 已是全部 ——"
          @load="onLoad"
        >
          <div class="reservation-list">
            <div v-for="item in list" :key="item.id" class="reservation-card">
              <!-- 卡片顶部：日期 + 状态 -->
              <div class="card-header">
                <div class="card-date">
                  <span class="date-icon">📅</span>
                  {{ item.visitDate }}
                </div>
                <van-tag :type="statusType(item.status)" size="medium" class="status-tag">
                  {{ statusLabel(item.status) }}
                </van-tag>
              </div>
              <!-- 卡片内容 -->
              <div class="card-body">
                <div class="card-row">
                  <span class="card-label">时段</span>
                  <span class="card-value">{{ item.visitTimeSlot }}</span>
                </div>
                <div class="card-row">
                  <span class="card-label">参观人</span>
                  <span class="card-value">{{ item.visitorName }}</span>
                </div>
                <div class="card-row">
                  <span class="card-label">人数</span>
                  <span class="card-value">{{ item.visitorCount }} 人</span>
                </div>
                <div class="card-row" v-if="item.notes">
                  <span class="card-label">备注</span>
                  <span class="card-value">{{ item.notes }}</span>
                </div>
                <div class="card-row reject-row" v-if="item.rejectReason">
                  <span class="card-label">原因</span>
                  <span class="card-value">{{ item.rejectReason }}</span>
                </div>
              </div>
              <!-- 卡片底部 -->
              <div class="card-footer">
                <span class="create-time">申请 · {{ item.createTime }}</span>
                <van-button
                  v-if="item.status === 'pending'"
                  size="small"
                  plain
                  class="cancel-btn"
                  @click="handleCancel(item)"
                >
                  取消预约
                </van-button>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

    <van-empty v-if="!loading && list.length === 0" description="暂无预约" class="empty-state" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import request from '@/api/index'

const router = useRouter()
const list = ref([])
const activeTab = ref('all')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const statusType = (s) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger', cancelled: 'info', completed: 'primary' }
  return map[s] || 'default'
}
const statusLabel = (s) => {
  const map = { pending: '待审核', approved: '已通过', rejected: '已拒绝', cancelled: '已取消', completed: '已完成' }
  return map[s] || s
}

const fetchData = async () => {
  const token = localStorage.getItem('userToken')
  if (!token) return

  const res = await request.get('/reservation/my')
  if (res.code === 200) {
    let all = res.data
    if (activeTab.value !== 'all') {
      all = all.filter(r => r.status === activeTab.value)
    }
    list.value = all
    finished.value = true
  }
}

const onLoad = () => fetchData()
const onRefresh = () => {
  page.value = 1
  fetchData()
  refreshing.value = false
}

const handleCancel = async (item) => {
  try {
    await showConfirmDialog({ title: '提示', message: '确定取消该预约吗？' })
    const res = await request.post(`/reservation/cancel/${item.id}`)
    if (res.code === 200) {
      showToast('已取消')
      fetchData()
    }
  } catch {
    // 取消
  }
}

onMounted(fetchData)
</script>

<style scoped>
.reservations-page {
  min-height: 100vh;
  background: var(--paper);
}

/* ---- 标签页 ---- */
:deep(.van-tabs__wrap) {
  background: var(--card);
  border-bottom: 1px solid var(--paper-dark);
}
:deep(.van-tab) {
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
}

/* ---- 列表 ---- */
.list-wrap {
  padding: 0 12px;
}
.reservation-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px 0;
}

/* ---- 预约卡片 ---- */
.reservation-card {
  background: var(--card);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border-left: 2px solid transparent;
}
.reservation-card:has(.status-tag.van-tag--warning) {
  border-left-color: var(--gold);
}
.reservation-card:has(.status-tag.van-tag--success) {
  border-left-color: var(--jade-green);
}
.reservation-card:has(.status-tag.van-tag--danger) {
  border-left-color: var(--ceramic-red);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: var(--paper);
  border-bottom: 1px solid var(--paper-dark);
}
.card-date {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}
.date-icon {
  font-size: 14px;
}

.card-body {
  padding: 12px 14px;
}
.card-row {
  display: flex;
  margin-bottom: 6px;
  font-size: 13px;
}
.card-row:last-child {
  margin-bottom: 0;
}
.card-label {
  width: 52px;
  color: var(--text-tertiary);
  flex-shrink: 0;
  font-size: 12px;
  letter-spacing: 1px;
}
.card-value {
  color: var(--text-primary);
  flex: 1;
  font-size: 13px;
}
.reject-row .card-value {
  color: var(--ceramic-red);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-top: 1px solid var(--paper-dark);
}
.create-time {
  font-size: 11px;
  color: var(--text-tertiary);
  letter-spacing: 1px;
}

.cancel-btn {
  font-family: 'Noto Serif SC', serif;
  font-size: 12px;
  color: var(--ceramic-red) !important;
  border-color: var(--ceramic-red) !important;
  padding: 4px 12px;
  border-radius: 4px;
}

/* ---- 空状态 ---- */
:deep(.van-empty__description) {
  font-family: 'Noto Serif SC', serif;
  color: var(--text-tertiary);
  letter-spacing: 2px;
}

/* ---- 完成文字 ---- */
:deep(.van-list__finished-text) {
  color: var(--text-tertiary);
  font-size: 12px;
  font-family: 'Noto Serif SC', serif;
  letter-spacing: 2px;
  padding: 20px 0;
}
</style>
