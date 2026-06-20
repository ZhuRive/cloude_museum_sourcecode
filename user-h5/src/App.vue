<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* ========================================
   陕忆·云博 — 设计系统
   灵感：陕西历史博物馆 · 新唐风美学
   色彩：宣纸暖白 · 鎏金 · 墨黑 · 黛青
   ======================================== */
:root {
  /* ---- 主色 ---- */
  --ink: #2C2C2C;
  --ink-light: #4A4A4A;
  --gold: #B8943F;
  --gold-light: #D4A84B;
  --gold-dark: #8B7535;
  --gold-glow: rgba(184, 148, 63, 0.15);

  /* ---- 底色 ---- */
  --paper: #F5F3EB;
  --paper-light: #FAF8F2;
  --paper-dark: #E8E5DF;
  --card: #FFFDF7;

  /* ---- 点缀色 ---- */
  --ceramic-red: #B8605A;
  --jade-green: #7A8B6F;
  --ink-wash: #3B4A4A;

  /* ---- 文字 ---- */
  --text-primary: #2C2C2C;
  --text-secondary: #6B6B6B;
  --text-tertiary: #9C9C9C;

  /* ---- 功能 ---- */
  --bg: var(--paper);
  --shadow-sm: 0 1px 3px rgba(44, 44, 44, 0.06);
  --shadow-md: 0 4px 12px rgba(44, 44, 44, 0.08);
  --shadow-lg: 0 8px 24px rgba(44, 44, 44, 0.1);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;

  /* ---- Vant 主题覆盖 ---- */
  --van-primary-color: var(--gold);

  /* Dialog 弹窗 — 适配唐风美学 */
  --van-dialog-background: var(--card);
  --van-dialog-border-radius: 18px;
  --van-dialog-header-font-weight: 700;
  --van-dialog-header-font-size: 17px;
  --van-dialog-header-padding-top: 28px;
  --van-dialog-header-padding-bottom: 6px;
  --van-dialog-header-line-height: 1.5;
  --van-dialog-message-font-size: 14px;
  --van-dialog-message-line-height: 1.7;
  --van-dialog-message-padding: 8px 28px 24px;
  --van-dialog-confirm-button-text-color: var(--gold);
  --van-dialog-confirm-button-font-size: 15px;
  --van-dialog-cancel-button-text-color: var(--text-secondary);
  --van-dialog-cancel-button-font-size: 15px;
  --van-nav-bar-title-text-color: var(--text-primary);
  --van-nav-bar-icon-color: var(--text-secondary);
  --van-nav-bar-background: var(--card);
  --van-tab-active-text-color: var(--gold);
  --van-tabs-bottom-bar-color: var(--gold);
  --van-tab-text-color: var(--text-secondary);
  --van-cell-group-inset-padding: 0;
  --van-cell-background: var(--card);
  --van-cell-vertical-padding: 14px;
  --van-search-background: var(--card);
  --van-button-primary-background: var(--gold);
  --van-button-primary-border-color: var(--gold);
  --van-tag-warning-color: #D4A84B;
  --van-tag-success-color: var(--jade-green);
  --van-tag-danger-color: var(--ceramic-red);
  --van-action-bar-background: var(--card);
  --van-tabbar-background: var(--card);
  --van-tabbar-item-active-color: var(--gold);
  --van-tabbar-item-text-color: var(--text-tertiary);
  --van-popup-background: var(--paper-light);
  --van-date-picker-header-title-color: var(--text-primary);
  --van-field-label-color: var(--text-secondary);
  --van-nav-bar-height: 48px;
}

/* ---- 全局样式 ---- */
body {
  background: var(--bg);
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.6;
  font-weight: 400;
}

/* ---- 金色底部装饰线（全局） ---- */
body::after {
  content: '';
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--gold), transparent);
  z-index: 9999;
}

/* ---- 通用标题（衬线宋体） ---- */
h1, h2, h3, h4 {
  font-family: 'Noto Serif SC', serif;
  font-weight: 700;
}

/* ---- 过渡动画 ---- */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* ---- 水墨装饰（可复用的伪元素背景） ---- */
.ink-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(184, 148, 63, 0.3), transparent);
  margin: 16px 0;
  border: none;
}
</style>
