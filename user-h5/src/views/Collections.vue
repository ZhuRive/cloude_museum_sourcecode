<template>
  <div class="collections-page">
    <van-nav-bar title="藏品浏览" left-arrow @click-left="router.back()" />

    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
        v-model="keyword"
        shape="round"
        placeholder="搜索藏品名称"
        @search="handleSearch"
      />
    </div>

    <!-- 分类标签 -->
    <div class="tabs-wrap">
      <van-tabs v-model:active="activeTab" @change="handleTabChange" line-width="20">
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
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div class="collection-list">
            <div
              v-for="item in list"
              :key="item.id"
              class="collection-item"
              @click="router.push(`/collection/${item.id}`)"
            >
              <img
                :src="item.coverImage || 'https://via.placeholder.com/120x90?text=藏品'"
                class="item-image"
              />
              <div class="item-info">
                <div class="item-name van-multi-ellipsis--l2">{{ item.name }}</div>
                <div class="item-tags">
                  <van-tag plain color="#4299e1" size="small">{{ item.categoryName }}</van-tag>
                  <van-tag plain color="#ed8936" size="small" v-if="item.dynasty">{{ item.dynasty }}</van-tag>
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
  // 加载分类
  const catRes = await request.get('/category/list')
  if (catRes.code === 200) categories.value = catRes.data

  // 检查是否有分类参数
  if (route.query.categoryId) {
    activeTab.value = Number(route.query.categoryId)
  }
})
</script>

<style scoped>
.collections-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.search-bar {
  padding: 0 12px;
  background: #fff;
}
.tabs-wrap {
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}
.list-wrap {
  padding: 0 12px 12px;
}
.collection-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}
.collection-item {
  display: flex;
  gap: 12px;
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.item-image {
  width: 120px;
  height: 90px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}
.item-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.item-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  line-height: 1.3;
}
.item-tags {
  display: flex;
  gap: 6px;
}
.item-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
}
</style>
