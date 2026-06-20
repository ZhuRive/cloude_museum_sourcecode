<template>
  <van-tabbar
    route
    :border="true"
    active-color="var(--gold)"
    inactive-color="var(--text-tertiary)"
    placeholder
  >
    <van-tabbar-item to="/home" icon="home-o">
      <span class="tab-label">首页</span>
    </van-tabbar-item>
    <van-tabbar-item to="/collections" icon="apps-o">
      <span class="tab-label">藏品</span>
    </van-tabbar-item>
    <van-tabbar-item to="/reservation" icon="calendar-o">
      <span class="tab-label">预约</span>
    </van-tabbar-item>
    <van-tabbar-item to="/my" icon="contact-o">
      <span class="tab-label">我的</span>
    </van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
</script>

<style scoped>
.tab-label {
  font-family: 'Noto Serif SC', serif;
  font-size: 11px;
  letter-spacing: 1px;
}
</style>
