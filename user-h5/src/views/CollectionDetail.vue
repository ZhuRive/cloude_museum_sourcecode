<template>
  <div class="detail-page">
    <van-nav-bar title="藏品详情" left-arrow @click-left="router.back()" />

    <div v-if="loading" style="padding: 40px; text-align: center;">
      <van-loading>加载中...</van-loading>
    </div>

    <div v-else-if="item" class="detail-content">
      <!-- 封面图 -->
      <div class="cover-wrap">
        <img :src="item.coverImage || 'https://via.placeholder.com/400x300?text=藏品'" class="cover-image" />
        <div class="cover-overlay">
          <span class="dynasty-tag">{{ item.dynasty || '年代不详' }}</span>
          <span class="category-tag">{{ item.categoryName }}</span>
        </div>
      </div>

      <div class="detail-body">
        <h1 class="detail-name">{{ item.name }}</h1>

        <!-- 基本信息 -->
        <div class="info-section">
          <h3 class="section-label">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">朝代</span>
              <span class="info-value">{{ item.dynasty || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">年代</span>
              <span class="info-value">{{ item.era || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">材质</span>
              <span class="info-value">{{ item.material || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">尺寸</span>
              <span class="info-value">{{ item.sizeDesc || '-' }}</span>
            </div>
            <div class="info-item" v-if="item.originPlace">
              <span class="info-label">出土地</span>
              <span class="info-value">{{ item.originPlace }}</span>
            </div>
          </div>
        </div>

        <!-- 藏品描述 -->
        <div class="info-section" v-if="item.description">
          <h3 class="section-label">藏品描述</h3>
          <p class="text-content">{{ item.description }}</p>
        </div>

        <!-- 文化意义 -->
        <div class="info-section" v-if="item.culturalSignificance">
          <h3 class="section-label">文化意义</h3>
          <p class="text-content">{{ item.culturalSignificance }}</p>
        </div>

        <!-- 浏览统计 -->
        <div class="view-count">
          <van-icon name="eye-o" /> {{ item.viewCount || 0 }} 次浏览
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <van-empty description="藏品不存在" />
    </div>

    <!-- 底部操作 -->
    <div class="bottom-actions">
      <van-action-bar>
        <van-action-bar-icon icon="home-o" text="首页" @click="router.push('/home')" />
        <van-action-bar-icon icon="apps-o" text="更多藏品" @click="router.push('/collections')" />
        <van-action-bar-icon icon="chat-o" text="AI讲解" color="#409eff" @click="showAiChat = true" />
        <van-action-bar-button type="primary" text="预约参观" @click="router.push('/reservation')" />
      </van-action-bar>
    </div>

    <!-- AI 智能讲解面板 -->
    <AiChatPanel
      v-if="item"
      :visible="showAiChat"
      :collection-id="item.id"
      :collection-name="item.name"
      @close="showAiChat = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/index'
import AiChatPanel from '@/components/AiChatPanel.vue'

const route = useRoute()
const router = useRouter()
const item = ref(null)
const loading = ref(true)
const showAiChat = ref(false)

onMounted(async () => {
  try {
    const res = await request.get(`/collection/detail/${route.params.id}`)
    if (res.code === 200) item.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60px;
}
.cover-wrap {
  position: relative;
  width: 100%;
  height: 300px;
}
.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-overlay {
  position: absolute;
  bottom: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
}
.dynasty-tag, .category-tag {
  background: rgba(0,0,0,0.55);
  color: #fff;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
}
.detail-body {
  padding: 16px;
}
.detail-name {
  font-size: 20px;
  font-weight: 600;
  color: #1a365d;
  margin-bottom: 16px;
  line-height: 1.3;
}
.info-section {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
}
.section-label {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #4299e1;
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-label {
  font-size: 12px;
  color: #999;
}
.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.text-content {
  font-size: 14px;
  color: #555;
  line-height: 1.8;
}
.view-count {
  text-align: center;
  color: #999;
  font-size: 12px;
  padding: 8px;
}
.empty-state {
  padding: 40px 0;
}
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
</style>
