<template>
  <div class="reservations-page">
    <van-nav-bar title="我的预约" left-arrow @click-left="router.back()" />

    <!-- 状态标签 -->
    <van-tabs v-model:active="activeTab" @change="fetchData" line-width="20">
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
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div class="reservation-list">
            <div v-for="item in list" :key="item.id" class="reservation-card">
              <div class="card-header">
                <div class="card-date">
                  <van-icon name="calendar-o" />
                  {{ item.visitDate }}
                </div>
                <van-tag :type="statusType(item.status)" size="medium">
                  {{ statusLabel(item.status) }}
                </van-tag>
              </div>
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
                <div class="card-row" v-if="item.rejectReason">
                  <span class="card-label">原因</span>
                  <span class="card-value" style="color:#ee0a24;">{{ item.rejectReason }}</span>
                </div>
              </div>
              <div class="card-footer">
                <span class="create-time">申请时间: {{ item.createTime }}</span>
                <van-button
                  v-if="item.status === 'pending'"
                  size="small"
                  plain
                  type="danger"
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

    <van-empty v-if="!loading && list.length === 0" description="暂无预约" />
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

  const params = { page: page.value, size: 10 }
  const res = await request.get('/reservation/my')
  if (res.code === 200) {
    let all = res.data
    // 前端过滤
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
  background: #f5f7fa;
}
.list-wrap {
  padding: 0 12px;
}
.reservation-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px 0;
}
.reservation-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: #f7f8fa;
  border-bottom: 1px solid #f0f0f0;
}
.card-date {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.card-body {
  padding: 12px 14px;
}
.card-row {
  display: flex;
  margin-bottom: 6px;
  font-size: 13px;
}
.card-label {
  width: 50px;
  color: #999;
  flex-shrink: 0;
}
.card-value {
  color: #333;
  flex: 1;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-top: 1px solid #f0f0f0;
}
.create-time {
  font-size: 11px;
  color: #bbb;
}
</style>
