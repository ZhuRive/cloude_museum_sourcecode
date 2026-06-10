<template>
  <div class="my-page">
    <!-- 用户信息 -->
    <div class="user-card">
      <div class="user-avatar">
        <img :src="userInfo?.avatar || 'https://via.placeholder.com/64x64?text=用户'" />
      </div>
      <div class="user-info-text" v-if="userInfo">
        <div class="user-name">{{ userInfo.nickname || '用户' }}</div>
        <div class="user-phone">{{ userInfo.phone || '' }}</div>
      </div>
      <div v-else class="user-info-text" @click="router.push('/login')">
        <div class="user-name" style="color: #fff;">点击登录</div>
        <div class="user-phone" style="color: rgba(255,255,255,0.7);">登录体验更多功能</div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <van-cell-group inset>
        <van-cell
          title="我的预约"
          icon="records-o"
          is-link
          @click="router.push('/my/reservations')"
        />
        <van-cell
          title="收藏的藏品"
          icon="star-o"
          is-link
          @click="showToast('功能开发中')"
        />
        <van-cell
          title="浏览历史"
          icon="clock-o"
          is-link
          @click="showToast('功能开发中')"
        />
      </van-cell-group>
    </div>

    <div class="menu-section" v-if="userInfo">
      <van-cell-group inset>
        <van-cell title="退出登录" icon="logout" is-link @click="handleLogout" />
      </van-cell-group>
    </div>

    <!-- 底部导航占位 -->
    <div style="height: 60px;"></div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()

const userInfo = computed(() => {
  const info = localStorage.getItem('userInfo')
  return info ? JSON.parse(info) : null
})

const handleLogout = async () => {
  try {
    await showConfirmDialog({ title: '提示', message: '确定退出登录吗？' })
    localStorage.removeItem('userToken')
    localStorage.removeItem('userInfo')
    showToast('已退出')
    router.replace('/home')
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.my-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.user-card {
  background: linear-gradient(135deg, #1a365d 0%, #2d6a9f 50%, #4299e1 100%);
  padding: 30px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-avatar img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: 3px solid rgba(255,255,255,0.4);
}
.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
}
.user-phone {
  font-size: 13px;
  color: rgba(255,255,255,0.8);
}
.menu-section {
  margin-top: 12px;
}
</style>
