<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <img src="https://via.placeholder.com/80x80?text=云博" class="logo" />
        <h2>云博物馆管理端</h2>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="login-tip">默认账号: admin / 密码: admin123</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/admin/login', form)
    if (res.code === 200) {
      localStorage.setItem('adminToken', res.data.token)
      localStorage.setItem('adminInfo', JSON.stringify(res.data.userInfo))
      ElMessage.success('登录成功')
      router.push('/dashboard')
    }
  } catch (e) {
    console.error('登录失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a365d 0%, #2d6a9f 50%, #4299e1 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.login-header .logo {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  margin-bottom: 12px;
}
.login-header h2 {
  color: #1a365d;
  font-size: 24px;
}
.login-form {
  margin-top: 20px;
}
.login-btn {
  width: 100%;
  font-size: 16px;
  height: 44px;
  background: linear-gradient(135deg, #2d6a9f, #4299e1);
  border: none;
}
.login-btn:hover {
  background: linear-gradient(135deg, #1a365d, #2d6a9f);
}
.login-tip {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: 16px;
}
</style>
