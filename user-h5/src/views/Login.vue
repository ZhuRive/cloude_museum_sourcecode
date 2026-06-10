<template>
  <div class="login-page">
    <div class="login-header">
      <h1 class="login-title">云博物馆</h1>
      <p class="login-subtitle">登录后即可预约参观</p>
    </div>

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
          >
            <template #button>
              <van-button
                size="small"
                :disabled="codeBtnDisabled"
                @click.prevent="handleSendCode"
                color="#4299e1"
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
          />
        </van-cell-group>

        <div style="margin: 20px 16px;">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            color="linear-gradient(135deg, #2d6a9f, #4299e1)"
            :loading="loading"
          >
            登录
          </van-button>
        </div>
      </van-form>

      <p class="login-tip">未注册的手机号将自动创建账号</p>
      <p class="login-dev-tip">开发环境验证码: 123456</p>
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
      // 跳回原页面或首页
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
  background: #f5f7fa;
}
.login-header {
  background: linear-gradient(135deg, #1a365d 0%, #2d6a9f 50%, #4299e1 100%);
  padding: 60px 20px 40px;
  text-align: center;
}
.login-title {
  color: #fff;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}
.login-subtitle {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
}
.login-form {
  margin-top: -20px;
}
.login-tip {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: 12px;
}
.login-dev-tip {
  text-align: center;
  color: #4299e1;
  font-size: 12px;
  margin-top: 8px;
}
</style>
