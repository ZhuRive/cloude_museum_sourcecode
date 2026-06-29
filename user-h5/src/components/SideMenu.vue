<template>
  <div class="side-menu-container">
    <!-- 浮动的呼出按钮 -->
    <div
      class="menu-trigger"
      :class="{ 'is-open': visible }"
      @click="open"
    >
      <div class="trigger-bar" :class="{ open: visible }">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>

    <!-- 遮罩层 -->
    <transition name="overlay-fade">
      <div
        v-if="visible"
        class="menu-overlay"
        @click="close"
        @touchmove.prevent
      ></div>
    </transition>

    <!-- 侧边栏面板 -->
    <transition name="panel-slide">
      <div v-if="visible" class="menu-panel">
        <!-- 用户信息 -->
        <div class="panel-header">
          <div class="user-badge" @click="goProfile">
            <div class="user-avatar">
              {{ userInitial }}
            </div>
            <div class="user-meta">
              <span class="user-name">{{ userName }}</span>
              <span class="user-sub">{{ userSub }}</span>
            </div>
          </div>
          <div class="brand-name">陕忆 · 云博</div>
        </div>

        <!-- 导航菜单 -->
        <nav class="panel-nav">
          <div
            v-for="item in navItems"
            :key="item.to"
            :class="['nav-item', { active: activeRoute === item.to }]"
            @click="navigate(item.to)"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="nav-label">{{ item.label }}</span>
            <span class="nav-arrow">›</span>
          </div>
        </nav>

        <!-- 底部装饰 -->
        <div class="panel-footer">
          <div class="footer-ornament"></div>
          <div class="footer-text">足不出户，尽览千年华夏</div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const visible = ref(false)
const activeRoute = computed(() => route.path)

const userInfo = computed(() => {
  try {
    const raw = localStorage.getItem('userInfo')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
})
const userName = computed(() => userInfo.value?.nickname || '游客')
const userSub = computed(() => {
  return userInfo.value ? (userInfo.value.phone || '已登录') : '点击登录'
})
const userInitial = computed(() => {
  const name = userName.value
  return name === '游客' ? '客' : name.charAt(0)
})

const navItems = [
  { to: '/home', icon: '🏛️', label: '首页' },
  { to: '/collections', icon: '🏺', label: '藏品纵览' },
  { to: '/reservation', icon: '📋', label: '预约参观' },
  { to: '/my', icon: '👤', label: '我的' },
  { to: '/my/reservations', icon: '📅', label: '我的预约' },
]

const open = () => { visible.value = true }
const close = () => { visible.value = false }

const navigate = (to) => {
  close()
  router.push(to)
}

const goProfile = () => {
  close()
  if (userInfo.value) {
    router.push('/my')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
/* =========================================
   侧边栏 — 唐风雅韵
   ========================================= */

/* ---- 浮动触发按钮 ---- */
.menu-trigger {
  position: fixed;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
  z-index: 10001;
  width: 36px;
  height: 44px;
  background: linear-gradient(135deg, var(--gold), var(--gold-light));
  border-radius: 0 8px 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 2px 0 8px var(--gold-glow);
  transition: all 0.3s ease;
  border: none;
  outline: none;
  -webkit-tap-highlight-color: transparent;
}
.menu-trigger:active {
  transform: translateY(-50%) scale(0.95);
}
.menu-trigger.is-open {
  left: 260px;
  border-radius: 8px 0 0 8px;
  box-shadow: -2px 0 8px var(--gold-glow);
}

.trigger-bar {
  width: 18px;
  height: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform 0.3s;
}
.trigger-bar span {
  display: block;
  width: 100%;
  height: 2px;
  background: #fff;
  border-radius: 1px;
  transition: all 0.3s ease;
}
.trigger-bar.open span:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}
.trigger-bar.open span:nth-child(2) {
  opacity: 0;
}
.trigger-bar.open span:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

/* ---- 遮罩 ---- */
.menu-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 10002;
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
}
.overlay-fade-enter-active,
.overlay-fade-leave-active {
  transition: opacity 0.3s ease;
}
.overlay-fade-enter-from,
.overlay-fade-leave-to {
  opacity: 0;
}

/* ---- 面板 ---- */
.menu-panel {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  width: 260px;
  background: linear-gradient(180deg, #2C2C2C 0%, #3B4A4A 100%);
  z-index: 10003;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.3);
}
.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: transform 0.3s cubic-bezier(0.22, 0.61, 0.36, 1);
}
.panel-slide-enter-from,
.panel-slide-leave-to {
  transform: translateX(-100%);
}

/* ---- 面板头部 ---- */
.panel-header {
  padding: 48px 20px 20px;
  border-bottom: 1px solid rgba(184, 148, 63, 0.2);
}
.user-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-badge:active {
  background: rgba(255, 255, 255, 0.05);
}
.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--gold), var(--gold-light));
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Noto Serif SC', serif;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}
.user-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.user-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.user-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.5px;
}
.brand-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
  color: var(--gold-light);
  letter-spacing: 4px;
  margin-top: 14px;
  text-align: center;
  opacity: 0.7;
}

/* ---- 导航 ---- */
.panel-nav {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
  -webkit-overflow-scrolling: touch;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  margin: 2px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}
.nav-item:active {
  background: rgba(184, 148, 63, 0.15);
}
.nav-item.active {
  background: rgba(184, 148, 63, 0.12);
  border-left: 3px solid var(--gold);
  margin-left: 9px;
}
.nav-icon {
  font-size: 20px;
  width: 28px;
  text-align: center;
  flex-shrink: 0;
}
.nav-label {
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 2px;
  flex: 1;
}
.nav-arrow {
  font-size: 20px;
  color: var(--gold);
  opacity: 0.5;
  font-family: serif;
}

/* ---- 底部 ---- */
.panel-footer {
  padding: 16px 20px 32px;
  text-align: center;
}
.footer-ornament {
  width: 40px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--gold), transparent);
  margin: 0 auto 10px;
}
.footer-text {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  letter-spacing: 2px;
  font-style: italic;
}
</style>
