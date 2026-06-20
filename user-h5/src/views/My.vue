<template>
  <div class="my-page">
    <!-- 用户卡片 -->
    <div class="user-hero">
      <div class="user-hero-bg"></div>
      <div class="user-card-content" v-if="userInfo">
        <div class="user-avatar-wrap">
          <img :src="userInfo?.avatar || 'https://via.placeholder.com/64x64?text=用'" class="user-avatar" />
        </div>
        <div class="user-text">
          <div class="user-name">{{ userInfo.nickname || '用户' }}</div>
          <div class="user-phone">{{ userInfo.phone || '' }}</div>
        </div>
      </div>
      <div class="user-card-content" v-else @click="router.push('/login')">
        <div class="user-avatar-wrap">
          <img src="https://via.placeholder.com/64x64?text=登" class="user-avatar" />
        </div>
        <div class="user-text">
          <div class="user-name" style="color: var(--gold-light);">登录 / 注册</div>
          <div class="user-phone">登录体验更多功能</div>
        </div>
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
          title-class="menu-title"
        />
        <van-cell
          title="收藏的藏品"
          icon="star-o"
          is-link
          @click="showToast('功能开发中')"
          title-class="menu-title"
        />
        <van-cell
          title="浏览历史"
          icon="clock-o"
          is-link
          @click="showToast('功能开发中')"
          title-class="menu-title"
        />
      </van-cell-group>
    </div>

    <div class="menu-section" v-if="userInfo">
      <van-cell-group inset>
        <van-cell
          title="退出登录"
          icon="logout"
          is-link
          @click="handleLogout"
          title-class="menu-title-logout"
        />
      </van-cell-group>
    </div>

    <!-- 退出确认弹窗 -->
    <van-dialog
      v-model:show="showLogoutDialog"
      :show-confirm-button="false"
      :show-cancel-button="false"
      class="logout-dialog"
      close-on-click-overlay
    >
      <div class="logout-dialog__inner">
        <div class="logout-dialog__icon">🚪</div>
        <h3 class="logout-dialog__title">确认退出登录</h3>
        <p class="logout-dialog__desc">退出后需要重新登录<br/>才能使用预约、AI讲解等全部功能</p>
        <div class="logout-dialog__divider"></div>
        <div class="logout-dialog__actions">
          <button class="logout-btn logout-btn--cancel" @click="showLogoutDialog = false">取消</button>
          <button class="logout-btn logout-btn--confirm" @click="confirmLogout">确认退出</button>
        </div>
      </div>
    </van-dialog>

    <div class="bottom-safe"></div>
    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import TabBar from '@/components/TabBar.vue'

const router = useRouter()
const showLogoutDialog = ref(false)

const userInfo = computed(() => {
  const info = localStorage.getItem('userInfo')
  return info ? JSON.parse(info) : null
})

const handleLogout = () => {
  showLogoutDialog.value = true
}

const confirmLogout = () => {
  localStorage.removeItem('userToken')
  localStorage.removeItem('userInfo')
  showLogoutDialog.value = false
  showToast('已退出')
  router.replace('/home')
}
</script>

<style scoped>
.my-page {
  min-height: 100vh;
  background: var(--paper);
}

/* ---- 用户英雄区 ---- */
.user-hero {
  position: relative;
  background: linear-gradient(160deg, #1a1a1a 0%, #2C2C2C 40%, #3B4A4A 100%);
  padding: 36px 20px;
  overflow: hidden;
}
.user-hero-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 80% 50%, rgba(184, 148, 63, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 20% 80%, rgba(184, 148, 63, 0.04) 0%, transparent 50%);
  pointer-events: none;
}
.user-card-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
}
.user-avatar-wrap {
  position: relative;
}
.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: 2px solid var(--gold);
  object-fit: cover;
}
.user-avatar-wrap::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 1px solid rgba(184, 148, 63, 0.3);
}
.user-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
  letter-spacing: 2px;
}
.user-phone {
  font-size: 13px;
  color: rgba(255,255,255,0.6);
  letter-spacing: 1px;
}

/* ---- 菜单 ---- */
.menu-section {
  margin-top: 12px;
  padding: 0 16px;
}
.menu-section :deep(.van-cell-group) {
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--card);
}
.menu-section :deep(.van-cell) {
  background: var(--card);
  padding: 14px 16px;
}
:deep(.menu-title) {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  color: var(--text-primary);
  letter-spacing: 1px;
}
:deep(.menu-title-logout) {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  color: var(--ceramic-red);
  letter-spacing: 1px;
}
.menu-section :deep(.van-icon) {
  color: var(--gold);
  font-size: 18px;
}

.bottom-safe {
  height: 64px;
}

/* =========================================
   退出确认弹窗 — 陕忆唐风
   ========================================= */
.logout-dialog {
  width: 300px !important;
  border-radius: 18px !important;
  overflow: hidden;
  background: var(--card);
}
.logout-dialog :deep(.van-dialog__content) {
  padding: 0;
}
.logout-dialog__inner {
  padding: 36px 28px 24px;
  text-align: center;
}
.logout-dialog__icon {
  font-size: 48px;
  margin-bottom: 14px;
  display: block;
}
.logout-dialog__title {
  font-family: 'Noto Serif SC', serif;
  font-size: 18px;
  font-weight: 700;
  color: var(--ink);
  margin: 0 0 8px;
  letter-spacing: 3px;
}
.logout-dialog__desc {
  font-size: 13px;
  color: var(--text-tertiary);
  margin: 0 0 20px;
  line-height: 1.7;
  letter-spacing: 0.5px;
}
.logout-dialog__divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--paper-dark), transparent);
  margin-bottom: 18px;
}
.logout-dialog__actions {
  display: flex;
  gap: 12px;
}
.logout-btn {
  flex: 1;
  height: 42px;
  border: none;
  border-radius: 21px;
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.2s;
  outline: none;
  -webkit-tap-highlight-color: transparent;
}
.logout-btn--cancel {
  background: var(--paper-dark);
  color: var(--text-secondary);
}
.logout-btn--cancel:active {
  background: #DCD8D0;
}
.logout-btn--confirm {
  background: linear-gradient(135deg, var(--ceramic-red), #C96A64);
  color: #fff;
  box-shadow: 0 4px 14px rgba(184, 96, 90, 0.35);
}
.logout-btn--confirm:active {
  transform: scale(0.97);
  box-shadow: 0 2px 8px rgba(184, 96, 90, 0.25);
}
</style>
