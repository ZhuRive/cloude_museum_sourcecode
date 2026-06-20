<template>
  <div class="login-page">
    <!-- 水墨风格头部 -->
    <div class="login-header">
      <div class="header-pattern"></div>
      <div class="header-content">
        <div class="header-emblem">🏛️</div>
        <h1 class="header-title">陕忆 · 云博</h1>
        <p class="header-subtitle">登录后即可预约参观</p>
        <div class="header-line"></div>
      </div>
    </div>

    <!-- 登录表单 -->
    <div class="login-form">
      <van-form @submit="handleLogin">
        <van-cell-group inset>
          <van-field
            v-model="phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
            maxlength="11"
            type="tel"
            label-class="form-label"
          >
            <template #button>
              <van-button
                size="small"
                :disabled="codeBtnDisabled"
                @click.prevent="handleSendCode"
                :color="codeBtnDisabled ? 'var(--paper-dark)' : 'var(--gold)'"
              >
                {{ codeBtnText }}
              </van-button>
            </template>
          </van-field>
          <van-field
            v-model="code"
            name="code"
            label="验证码"
            placeholder="请输入验证码"
            maxlength="6"
            :rules="[{ required: true, message: '请输入验证码' }]"
            label-class="form-label"
          />
        </van-cell-group>

        <div class="submit-wrap">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
            class="login-btn"
          >
            登录
          </van-button>
        </div>
      </van-form>

      <p class="form-tip">未注册的手机号将自动创建账号</p>
      <p class="form-dev-tip">开发环境验证码：123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import request from '@/api/index'

const router = useRouter()
const route = useRoute()
const phone = ref('')
const code = ref('')
const loading = ref(false)
const codeSending = ref(false)
const countdown = ref(0)

const codeBtnDisabled = computed(() => codeSending.value || countdown.value > 0 || !/^1[3-9]\d{9}$/.test(phone.value))
const codeBtnText = computed(() => {
  if (countdown.value > 0) return `${countdown.value}s`
  if (codeSending.value) return '发送中'
  return '获取验证码'
})

const handleSendCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    showToast('请输入正确的手机号')
    return
  }
  codeSending.value = true
  try {
    const res = await request.post('/user/send-code', { phone: phone.value })
    if (res.code === 200) {
      showToast('验证码已发送')
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) clearInterval(timer)
      }, 1000)
    }
  } catch (e) {
    console.error(e)
  } finally {
    codeSending.value = false
  }
}

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await request.post('/user/login', { phone: phone.value, code: code.value })
    if (res.code === 200) {
      localStorage.setItem('userToken', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      showToast('登录成功')
      const redirect = route.query.redirect || '/home'
      router.replace(redirect)
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--paper);
}

/* ---- 水墨头部 ---- */
.login-header {
  position: relative;
  background: linear-gradient(160deg, #1a1a1a 0%, #2C2C2C 40%, #3B4A4A 100%);
  padding: 48px 20px 40px;
  text-align: center;
  overflow: hidden;
}
.header-pattern {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 30% 70%, rgba(184, 148, 63, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 30%, rgba(184, 148, 63, 0.05) 0%, transparent 50%);
  pointer-events: none;
}
.header-content {
  position: relative;
  z-index: 1;
}
.header-emblem {
  font-size: 36px;
  margin-bottom: 8px;
}
.header-title {
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  font-size: 26px;
  color: var(--gold-light);
  letter-spacing: 6px;
  margin-bottom: 8px;
}
.header-subtitle {
  font-size: 13px;
  color: rgba(255,255,255,0.6);
  letter-spacing: 2px;
}
.header-line {
  width: 40px;
  height: 2px;
  background: var(--gold);
  margin: 14px auto 0;
  border-radius: 1px;
}

/* ---- 表单 ---- */
.login-form {
  margin-top: -16px;
  padding: 0 16px;
}
.login-form :deep(.van-cell-group) {
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--card);
}
.login-form :deep(.van-field__label) {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  color: var(--text-primary);
  width: 70px;
}
.login-form :deep(.van-field__body input) {
  font-size: 14px;
}
.submit-wrap {
  margin: 24px 0 16px;
}
.login-btn {
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
  letter-spacing: 4px;
  height: 44px;
  border: none;
  box-shadow: 0 4px 12px var(--gold-glow);
}
.form-tip {
  text-align: center;
  color: var(--text-tertiary);
  font-size: 12px;
  letter-spacing: 1px;
}
.form-dev-tip {
  text-align: center;
  color: var(--text-tertiary);
  font-size: 11px;
  margin-top: 8px;
  opacity: 0.6;
}
</style>
