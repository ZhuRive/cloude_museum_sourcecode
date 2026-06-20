<template>
  <div class="detail-page">
    <van-nav-bar title="藏 品 详 情" left-arrow @click-left="router.back()" />

    <!-- 加载态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-ink"></div>
      <p class="loading-text">展卷中…</p>
    </div>

    <!-- 藏品内容 -->
    <div v-else-if="item" class="detail-content">
      <!-- 封面图 -->
      <div class="cover-wrap">
        <img :src="item.coverImage || 'https://via.placeholder.com/400x300?text=藏品'" class="cover-image" />
        <div class="cover-ornament"></div>
        <div class="cover-tags">
          <span class="tag-dynasty">{{ item.dynasty || '年代不详' }}</span>
          <span class="tag-category">{{ item.categoryName }}</span>
        </div>
      </div>

      <!-- 3D 模型展示 -->
      <div class="body-section" style="padding-bottom: 0;">
        <ModelViewer3D
          v-if="item.modelUrl"
          :model-url="item.modelUrl"
          :name="item.name"
        />
      </div>

      <!-- 藏品名称 -->
      <div class="body-section">
        <h1 class="detail-name">{{ item.name }}</h1>
        <div class="name-underline"></div>

        <!-- 基本信息 -->
        <div class="info-card">
          <h3 class="info-heading">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">朝代</span>
              <span class="info-value">{{ item.dynasty || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">年代</span>
              <span class="info-value">{{ item.era || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">材质</span>
              <span class="info-value">{{ item.material || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">尺寸</span>
              <span class="info-value">{{ item.sizeDesc || '—' }}</span>
            </div>
            <div class="info-item full-width" v-if="item.originPlace">
              <span class="info-label">出土地</span>
              <span class="info-value">{{ item.originPlace }}</span>
            </div>
          </div>
        </div>

        <!-- 藏品描述 -->
        <div class="info-card" v-if="item.description">
          <h3 class="info-heading">藏品描述</h3>
          <p class="text-content">{{ item.description }}</p>
        </div>

        <!-- 文化意义 -->
        <div class="info-card" v-if="item.culturalSignificance">
          <h3 class="info-heading">文化意义</h3>
          <p class="text-content">{{ item.culturalSignificance }}</p>
        </div>

        <!-- 浏览计数 -->
        <div class="view-count">
          <span class="view-dot"></span>
          {{ item.viewCount || 0 }} 次浏览
        </div>
      </div>
    </div>

    <!-- 空态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">📜</div>
      <p class="empty-text">此藏品已失传</p>
    </div>

    <!-- AI 智能讲解面板 -->
    <AiChatPanel
      v-if="item"
      :visible="showAiChat"
      :collection-id="item.id"
      :collection-name="item.name"
      @close="showAiChat = false"
    />

    <!-- 底部操作 -->
    <div class="bottom-actions">
      <van-action-bar>
        <van-action-bar-icon icon="home-o" text="首页" @click="router.push('/home')" />
        <van-action-bar-icon icon="apps-o" text="更多藏品" @click="router.push('/collections')" />
        <van-action-bar-icon icon="chat-o" text="AI讲解" @click="showAiChat = true" />
        <van-action-bar-button color="var(--gold)" text="预约参观" @click="router.push('/reservation')" />
      </van-action-bar>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/index'
import ModelViewer3D from '@/components/ModelViewer3D.vue'
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
  background: var(--paper);
  padding-bottom: 56px;
}

/* ---- 加载态 ---- */
.loading-state {
  padding: 80px 0;
  text-align: center;
}
.loading-ink {
  width: 40px;
  height: 40px;
  margin: 0 auto 16px;
  border: 2px solid var(--paper-dark);
  border-top-color: var(--gold);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.loading-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  color: var(--text-tertiary);
  letter-spacing: 3px;
}

/* ---- 封面 ---- */
.cover-wrap {
  position: relative;
  width: 100%;
  height: 320px;
  overflow: hidden;
}
.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-ornament {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  background: linear-gradient(transparent, var(--paper));
}
.cover-tags {
  position: absolute;
  bottom: 20px;
  left: 16px;
  display: flex;
  gap: 10px;
}
.tag-dynasty, .tag-category {
  font-family: 'Noto Serif SC', serif;
  font-size: 12px;
  padding: 4px 14px;
  border-radius: 4px;
  backdrop-filter: blur(4px);
  letter-spacing: 1px;
}
.tag-dynasty {
  background: rgba(44,44,44,0.6);
  color: var(--gold-light);
  border: 1px solid rgba(184,148,63,0.3);
}
.tag-category {
  background: rgba(184,148,63,0.15);
  color: var(--gold-dark);
  border: 1px solid rgba(184,148,63,0.2);
}

/* ---- 主体 ---- */
.body-section {
  padding: 16px;
}

.detail-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--ink);
  line-height: 1.35;
  text-align: center;
  letter-spacing: 2px;
}
.name-underline {
  width: 40px;
  height: 2px;
  background: var(--gold);
  margin: 12px auto 20px;
  border-radius: 1px;
}

/* ---- 信息卡片 ---- */
.info-card {
  background: var(--card);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
  position: relative;
  border-left: 2px solid var(--gold);
}
.info-heading {
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 12px;
  letter-spacing: 2px;
  position: relative;
}
.info-heading::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 24px;
  height: 1px;
  background: var(--gold);
  opacity: 0.4;
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.info-item.full-width {
  grid-column: 1 / -1;
}
.info-label {
  font-size: 11px;
  color: var(--text-tertiary);
  letter-spacing: 1px;
}
.info-value {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}
.text-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.9;
  text-indent: 2em;
  letter-spacing: 0.5px;
}
.view-count {
  text-align: center;
  color: var(--text-tertiary);
  font-size: 12px;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}
.view-dot {
  width: 3px;
  height: 3px;
  background: var(--text-tertiary);
  border-radius: 50%;
  display: inline-block;
}

/* ---- 空态 ---- */
.empty-state {
  padding: 80px 0;
  text-align: center;
}
.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.empty-text {
  font-family: 'Noto Serif SC', serif;
  color: var(--text-tertiary);
  letter-spacing: 2px;
}

/* ---- 底部操作栏 ---- */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
.bottom-actions :deep(.van-action-bar) {
  border-top: 1px solid var(--paper-dark);
}
</style>
