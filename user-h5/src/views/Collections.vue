<template>
  <div class="collections-page">
    <van-nav-bar title="藏 品 纵 览" left-arrow @click-left="router.back()" />

    <!-- 搜索栏 -->
    <div class="search-wrap">
      <van-search
        v-model="keyword"
        shape="round"
        placeholder="搜藏品名称…"
        @search="handleSearch"
        clearable
      />
    </div>

    <!-- 分类标签 -->
    <div class="tabs-wrap">
      <van-tabs v-model:active="activeTab" @change="handleTabChange" line-width="24" color="var(--gold)" title-active-color="var(--gold)">
        <van-tab title="全部" :name="0" />
        <van-tab v-for="cat in categories" :key="cat.id" :title="cat.name" :name="cat.id" />
      </van-tabs>
    </div>

    <!-- 藏品列表 -->
    <div class="list-wrap">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="—— 已是全部 ——"
          @load="onLoad"
        >
          <div class="collection-list">
            <div
              v-for="item in list"
              :key="item.id"
              class="collection-item"
              @click="router.push(`/collection/${item.id}`)"
            >
              <div class="item-image-wrap">
                <img
                  :src="item.coverImage || 'https://via.placeholder.com/120x90?text=藏品'"
                  class="item-image"
                />
                <div class="item-dynasty-tag" v-if="item.dynasty">{{ item.dynasty }}</div>
              </div>
              <div class="item-info">
                <div class="item-name van-multi-ellipsis--l2">{{ item.name }}</div>
                <div class="item-tags">
                  <span class="item-cat-tag">{{ item.categoryName }}</span>
                </div>
                <div class="item-desc van-ellipsis">{{ item.description }}</div>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '@/api/index'

const router = useRouter()
const route = useRoute()

const list = ref([])
const categories = ref([])
const keyword = ref('')
const activeTab = ref(0)
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const handleSearch = () => {
  page.value = 1
  finished.value = false
  list.value = []
  onLoad()
}

const handleTabChange = () => {
  page.value = 1
  finished.value = false
  list.value = []
  onLoad()
}

const loadData = async () => {
  const params = { page: page.value, size: 10 }
  if (activeTab.value !== 0) params.categoryId = activeTab.value
  if (keyword.value) params.keyword = keyword.value
  const res = await request.get('/collection/list', { params })
  return res.data
}

const onLoad = async () => {
  try {
    const data = await loadData()
    list.value = page.value === 1 ? data.list : [...list.value, ...data.list]
    page.value++
    if (list.value.length >= data.total) finished.value = true
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onRefresh = () => {
  page.value = 1
  finished.value = false
  list.value = []
  onLoad()
}

onMounted(async () => {
  const catRes = await request.get('/category/list')
  if (catRes.code === 200) categories.value = catRes.data
  if (route.query.categoryId) {
    activeTab.value = Number(route.query.categoryId)
  }
})
</script>

<style scoped>
.collections-page {
  min-height: 100vh;
  background: var(--paper);
}

/* 搜索栏 */
.search-wrap {
  padding: 0 12px;
  background: var(--card);
}
.search-wrap :deep(.van-search__content) {
  background: var(--paper);
  border: 1px solid var(--paper-dark);
}
.search-wrap :deep(.van-field__control) {
  font-size: 13px;
}

/* 分类标签 */
.tabs-wrap {
  background: var(--card);
  position: sticky;
  top: 0;
  z-index: 10;
}
.tabs-wrap :deep(.van-tabs__wrap) {
  border-bottom: 1px solid var(--paper-dark);
}
.tabs-wrap :deep(.van-tab) {
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
  padding: 0 12px;
}

/* 列表 */
.list-wrap {
  padding: 0 12px 12px;
}
.collection-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 12px;
}
.collection-item {
  display: flex;
  gap: 12px;
  background: var(--card);
  border-radius: var(--radius-md);
  padding: 12px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
}
.collection-item:active {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}
/* 底部金色装饰 */
.collection-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 12px;
  right: 12px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--gold), transparent);
  opacity: 0.2;
}

.item-image-wrap {
  position: relative;
  width: 120px;
  height: 90px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
}
.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.item-dynasty-tag {
  position: absolute;
  top: 4px;
  left: 4px;
  background: rgba(44,44,44,0.65);
  color: var(--gold-light);
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  letter-spacing: 1px;
  font-family: 'Noto Serif SC', serif;
}
.item-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}
.item-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
}
.item-tags {
  display: flex;
  gap: 6px;
}
.item-cat-tag {
  display: inline-block;
  font-size: 11px;
  color: var(--gold-dark);
  background: var(--gold-glow);
  padding: 2px 8px;
  border-radius: 4px;
  letter-spacing: 1px;
  font-family: 'Noto Serif SC', serif;
}
.item-desc {
  font-size: 12px;
  color: var(--text-tertiary);
  line-height: 1.4;
}

/* Vant list 完成文字 */
:deep(.van-list__finished-text) {
  color: var(--text-tertiary);
  font-size: 12px;
  font-family: 'Noto Serif SC', serif;
  letter-spacing: 2px;
  padding: 20px 0;
}
</style>
