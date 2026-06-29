<template>
  <div class="home-page">
    <!-- ===== 顶部横幅：水墨 + 鎏金 ===== -->
    <div class="hero">
      <div class="hero-pattern"></div>
      <div class="hero-content">
        <h1 class="hero-sitename">陕忆 · 云博</h1>
        <p class="hero-subtitle">足不出户，尽览千年华夏</p>
        <div class="hero-divider"></div>
        <p class="hero-quote">「 让每一个时代，都有属于自己的色彩 」</p>
      </div>
      <!-- 底部金色装饰线 -->
      <div class="hero-ornament"></div>
    </div>

    <!-- ===== 分类导航：金墨相间 ===== -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">藏品种类</span>
        <span class="section-line"></span>
      </div>
      <div class="category-grid">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-item"
          @click="router.push(`/collections?categoryId=${cat.id}`)"
        >
          <div class="category-ring">
            <span class="category-char">{{ cat.name.charAt(0) }}</span>
          </div>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <!-- ===== 推荐藏品：卷轴卡片 ===== -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">馆藏珍品</span>
        <span class="section-line"></span>
        <span class="section-more" @click="router.push('/collections')">全部 ›</span>
      </div>
      <div class="scroll-wrap">
        <div
          v-for="item in featured"
          :key="item.id"
          class="treasure-card"
          @click="router.push(`/collection/${item.id}`)"
        >
          <div class="treasure-image-wrap">
            <img
              :src="item.coverImage || 'https://via.placeholder.com/200x150?text=藏品'"
              class="treasure-image"
            />
            <div class="treasure-overlay">
              <span class="treasure-dynasty">{{ item.dynasty || '古器' }}</span>
            </div>
          </div>
          <div class="treasure-info">
            <div class="treasure-name">{{ item.name }}</div>
            <div class="treasure-meta">{{ item.categoryName || '未分类' }}</div>
          </div>
          <!-- 底部金色角饰 -->
          <div class="treasure-corner"></div>
        </div>
      </div>
    </div>

    <!-- ===== 预约入口：仿古卷轴 ===== -->
    <div class="section">
      <div class="reservation-scroll" @click="router.push('/reservation')">
        <!-- 卷轴轴头装饰 -->
        <div class="scroll-rod scroll-rod-left"></div>
        <div class="scroll-body">
          <div class="scroll-content">
            <div class="scroll-icon">🏯</div>
            <div class="scroll-title">预约参观</div>
            <div class="scroll-desc">提前预约 · 畅享文化之旅</div>
            <button class="scroll-btn">立即前往</button>
          </div>
        </div>
        <div class="scroll-rod scroll-rod-right"></div>
      </div>
    </div>

    <!-- 底部安全区 -->
    <div class="bottom-safe"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/index'
const router = useRouter()
const categories = ref([])
const featured = ref([])

onMounted(async () => {
  try {
    const [catRes, featRes] = await Promise.all([
      request.get('/category/list'),
      request.get('/collection/featured')
    ])
    if (catRes.code === 200) categories.value = catRes.data
    if (featRes.code === 200) featured.value = featRes.data
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
/* =========================================
   首页 — 陕忆 · 云博
   水墨美学 · 鎏金点缀 · 大唐遗风
   ========================================= */
.home-page {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: 0;
}

/* ---------- 英雄横幅 ---------- */
.hero {
  position: relative;
  background: linear-gradient(160deg, #1a1a1a 0%, #2C2C2C 30%, #3B4A4A 60%, #4A4030 100%);
  padding: 52px 24px 36px;
  text-align: center;
  overflow: hidden;
}
.hero-pattern {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 60%, rgba(184, 148, 63, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 40%, rgba(184, 148, 63, 0.05) 0%, transparent 50%),
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent 40px,
      rgba(255,255,255,0.02) 40px,
      rgba(255,255,255,0.02) 41px
    );
  pointer-events: none;
}
.hero-content {
  position: relative;
  z-index: 1;
}
.hero-sitename {
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  font-size: 32px;
  color: var(--gold-light);
  letter-spacing: 8px;
  margin-bottom: 10px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}
.hero-subtitle {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
  letter-spacing: 4px;
  font-weight: 300;
}
.hero-divider {
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--gold), transparent);
  margin: 14px auto;
}
.hero-quote {
  font-size: 12px;
  color: rgba(212, 168, 75, 0.6);
  letter-spacing: 2px;
  font-style: italic;
  font-weight: 300;
}
.hero-ornament {
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, transparent 5%, var(--gold) 20%, var(--gold-light) 50%, var(--gold) 80%, transparent 95%);
}

/* ---------- 通用区块 ---------- */
.section {
  padding: 20px 16px 8px;
}
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.section-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 17px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 2px;
  white-space: nowrap;
}
.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, var(--gold), transparent);
  opacity: 0.4;
}
.section-more {
  font-size: 13px;
  color: var(--gold);
  white-space: nowrap;
  font-weight: 500;
  letter-spacing: 1px;
}

/* ---------- 分类网格 ---------- */
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 8px;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 0;
}
.category-ring {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--card), var(--paper-light));
  border: 2px solid var(--gold);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px var(--gold-glow);
  transition: transform 0.2s, box-shadow 0.2s;
}
.category-item:active .category-ring {
  transform: scale(0.92);
  box-shadow: 0 1px 4px var(--gold-glow);
}
.category-char {
  font-family: 'Noto Serif SC', serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--gold-dark);
}
.category-name {
  font-size: 12px;
  color: var(--text-secondary);
  letter-spacing: 1px;
}

/* ---------- 横向滚动藏品 ---------- */
.scroll-wrap {
  display: flex;
  gap: 14px;
  overflow-x: auto;
  padding: 4px 2px 8px;
  scrollbar-width: none;
}
.scroll-wrap::-webkit-scrollbar {
  display: none;
}
.treasure-card {
  flex-shrink: 0;
  width: 170px;
  background: var(--card);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  position: relative;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.treasure-card:active {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
.treasure-image-wrap {
  position: relative;
  width: 100%;
  height: 120px;
  overflow: hidden;
}
.treasure-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.treasure-card:active .treasure-image {
  transform: scale(1.05);
}
.treasure-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 6px 10px;
  background: linear-gradient(transparent, rgba(44,44,44,0.6));
}
.treasure-dynasty {
  font-size: 11px;
  color: rgba(255,255,255,0.9);
  letter-spacing: 1px;
  font-weight: 300;
}
.treasure-info {
  padding: 10px 12px 12px;
}
.treasure-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}
.treasure-meta {
  font-size: 11px;
  color: var(--text-tertiary);
  letter-spacing: 1px;
}
.treasure-corner {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 20px;
  height: 20px;
  border-right: 2px solid var(--gold);
  border-bottom: 2px solid var(--gold);
  opacity: 0.3;
  border-radius: 0 0 var(--radius-md) 0;
}

/* ---------- 预约入口：仿古卷轴 ---------- */
.reservation-scroll {
  display: flex;
  align-items: stretch;
  cursor: pointer;
  margin-top: 4px;
}
.scroll-rod {
  width: 12px;
  border-radius: 6px;
  background: linear-gradient(180deg, var(--gold-light), var(--gold-dark), var(--gold-light));
  box-shadow: 1px 0 4px rgba(0,0,0,0.15);
  flex-shrink: 0;
}
.scroll-rod-left {
  margin-right: -2px;
}
.scroll-rod-right {
  margin-left: -2px;
}
.scroll-body {
  flex: 1;
  background: linear-gradient(135deg, #3B4A4A 0%, #2C2C2C 100%);
  padding: 24px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.scroll-body::before {
  content: '';
  position: absolute;
  inset: 6px;
  border: 1px solid rgba(184, 148, 63, 0.2);
  pointer-events: none;
}
.scroll-body::after {
  content: '';
  position: absolute;
  inset: 0;
  background:
    repeating-linear-gradient(
      90deg,
      transparent,
      transparent 30px,
      rgba(184, 148, 63, 0.03) 30px,
      rgba(184, 148, 63, 0.03) 31px
    );
  pointer-events: none;
}
.scroll-content {
  position: relative;
  z-index: 1;
}
.scroll-icon {
  font-size: 32px;
  margin-bottom: 8px;
}
.scroll-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 19px;
  font-weight: 700;
  color: var(--gold-light);
  letter-spacing: 4px;
  margin-bottom: 6px;
}
.scroll-desc {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
  letter-spacing: 2px;
  margin-bottom: 14px;
}
.scroll-btn {
  display: inline-block;
  padding: 8px 28px;
  border: 1px solid var(--gold);
  background: transparent;
  color: var(--gold-light);
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
  letter-spacing: 2px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.25s;
}
.scroll-btn:active {
  background: var(--gold);
  color: #fff;
}

.bottom-safe {
  height: 64px;
}
</style>
