<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo-area">
        <img src="https://via.placeholder.com/36x36?text=云博" class="sidebar-logo" />
        <span v-show="!isCollapse" class="sidebar-title">云博物馆管理</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#1a365d"
        text-color="#bfdbfe"
        active-text-color="#fff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>控制台</span>
        </el-menu-item>

        <el-sub-menu index="collection">
          <template #title>
            <el-icon><Collection /></el-icon>
            <span>藏品管理</span>
          </template>
          <el-menu-item index="/collection">藏品列表</el-menu-item>
          <el-menu-item index="/collection/add">新增藏品</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>

        <el-menu-item index="/reservation">
          <el-icon><Tickets /></el-icon>
          <span>预约管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            @click="isCollapse = !isCollapse"
            size="20"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ adminInfo?.realName || adminInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const adminInfo = computed(() => {
  const info = localStorage.getItem('adminInfo')
  return info ? JSON.parse(info) : {}
})

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示').then(() => {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #1a365d;
  overflow-y: auto;
  transition: width 0.3s;
}
.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.sidebar-logo {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  flex-shrink: 0;
}
.sidebar-title {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
}
.header {
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
  color: #666;
}
.collapse-btn:hover {
  color: #1a365d;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}
.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
.el-menu {
  border-right: none;
}
</style>
