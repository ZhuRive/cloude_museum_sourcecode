<template>
  <div class="dashboard">
    <h2 class="page-title">控制台</h2>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">藏品总数</div>
              <div class="stat-value">{{ stats.collectionCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#4299e1"><Collection /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#48bb78"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">预约总数</div>
              <div class="stat-value">{{ stats.reservationCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#ed8936"><Tickets /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审核预约</div>
              <div class="stat-value">{{ stats.pendingCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#f56565"><WarningFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">快速操作</span>
          </template>
          <el-space wrap :size="16">
            <el-button type="primary" @click="$router.push('/collection/add')">
              <el-icon><Plus /></el-icon>新增藏品
            </el-button>
            <el-button @click="$router.push('/collection')">
              <el-icon><List /></el-icon>管理藏品
            </el-button>
            <el-button @click="$router.push('/category')">
              <el-icon><Menu /></el-icon>管理分类
            </el-button>
            <el-button @click="$router.push('/reservation')">
              <el-icon><Tickets /></el-icon>审核预约
            </el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">系统信息</span>
          </template>
          <div class="sys-info">
            <p>云博物馆管理系统 v1.0.0</p>
            <p>华为杯人工智能比赛项目</p>
            <p>技术栈: Spring Boot + Vue3 + Element Plus</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/reservation'

const stats = ref({
  collectionCount: 0,
  userCount: 0,
  reservationCount: 0,
  pendingCount: 0
})

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (e) {
    console.error('获取统计数据失败:', e)
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
}
.page-title {
  font-size: 20px;
  color: #1a365d;
  margin-bottom: 20px;
}
.stats-row {
  margin-bottom: 0;
}
.stat-card {
  border-radius: 8px;
}
.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-label {
  font-size: 14px;
  color: #718096;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #1a365d;
}
.sys-info p {
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}
</style>
