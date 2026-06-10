<template>
  <div class="home-page">
    <!-- 顶部横幅 -->
    <div class="banner">
      <div class="banner-content">
        <h1 class="banner-title">云博物馆</h1>
        <p class="banner-subtitle">足不出户，畅游千年文明</p>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">分类浏览</span>
      </div>
      <div class="category-grid">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-item"
          @click="router.push(`/collections?categoryId=${cat.id}`)"
        >
          <div class="category-icon">{{ cat.name.charAt(0) }}</div>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <!-- 推荐藏品 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">推荐藏品</span>
        <span class="section-more" @click="router.push('/collections')">更多 ›</span>
      </div>
      <div class="collection-scroll">
        <div
          v-for="item in featured"
          :key="item.id"
          class="collection-card"
          @click="router.push(`/collection/${item.id}`)"
        >
          <img
            :src="item.coverImage || 'https://via.placeholder.com/200x150?text=藏品'"
            class="card-image"
          />
          <div class="card-info">
            <div class="card-name">{{ item.name }}</div>
            <div class="card-dynasty">{{ item.dynasty }} · {{ item.categoryName }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速预约入口 -->
    <div class="section">
      <div class="reservation-card" @click="router.push('/reservation')">
        <div class="reservation-text">
          <div class="reservation-title">预约参观</div>
          <div class="reservation-desc">提前预约，畅享文化之旅</div>
        </div>
        <div class="reservation-btn">立即预约</div>
      </div>
    </div>

    <!-- 底部导航占位 -->
    <div style="height: 60px;"></div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/index'
import TabBar from '@/components/TabBar.vue'

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
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 0;
}
.banner {
  background: linear-gradient(135deg, #1a365d 0%, #2d6a9f 50%, #4299e1 100%);
  padding: 40px 20px 30px;
  text-align: center;
}
.banner-title {
  color: #fff;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}
.banner-subtitle {
  color: rgba(255,255,255,0.85);
  font-size: 14px;
}
.section {
  padding: 16px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.section-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a365d;
}
.section-more {
  font-size: 13px;
  color: #4299e1;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}
.category-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ebf4ff, #dbeafe);
  color: #2d6a9f;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
}
.category-name {
  font-size: 12px;
  color: #666;
}
.collection-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: none;
}
.collection-scroll::-webkit-scrollbar {
  display: none;
}
.collection-card {
  flex-shrink: 0;
  width: 160px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.card-image {
  width: 100%;
  height: 110px;
  object-fit: cover;
}
.card-info {
  padding: 8px 10px;
}
.card-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-dynasty {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.reservation-card {
  background: linear-gradient(135deg, #2d6a9f, #4299e1);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}
.reservation-title {
  font-size: 17px;
  font-weight: 600;
  color: #fff;
}
.reservation-desc {
  font-size: 13px;
  color: rgba(255,255,255,0.8);
  margin-top: 4px;
}
.reservation-btn {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.4);
  color: #fff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  white-space: nowrap;
}
</style>
