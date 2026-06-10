<template>
  <div class="reservation-page">
    <van-nav-bar title="预约参观" left-arrow @click-left="router.back()" />

    <div class="page-content">
      <!-- 提示卡片 -->
      <div class="notice-card">
        <van-icon name="info-o" color="#4299e1" size="18" />
        <span>请至少提前一天预约，参观时请出示预约记录</span>
      </div>

      <van-form @submit="handleSubmit">
        <van-cell-group inset>
          <van-field
            v-model="form.visitorName"
            name="visitorName"
            label="参观人"
            placeholder="请输入姓名"
            :rules="[{ required: true, message: '请输入姓名' }]"
          />
          <van-field
            v-model="form.visitorPhone"
            name="visitorPhone"
            label="手机号"
            placeholder="请输入手机号"
            type="tel"
            maxlength="11"
            :rules="[{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
          />
          <van-field
            v-model="form.idCard"
            name="idCard"
            label="身份证号"
            placeholder="选填"
            maxlength="18"
          />
          <van-field
            v-model="form.visitorCount"
            name="visitorCount"
            label="参观人数"
            placeholder="请输入人数"
            type="number"
            :rules="[{ required: true, message: '请输入人数' }]"
          />
          <van-field
            v-model="form.visitDate"
            is-link
            readonly
            name="visitDate"
            label="参观日期"
            placeholder="请选择日期"
            :rules="[{ required: true, message: '请选择日期' }]"
            @click="showDatePicker = true"
          />
          <van-field
            v-model="form.visitTimeSlot"
            is-link
            readonly
            name="visitTimeSlot"
            label="参观时段"
            placeholder="请选择时段"
            :rules="[{ required: true, message: '请选择时段' }]"
            @click="showTimePicker = true"
          />
          <van-field
            v-model="form.notes"
            name="notes"
            label="备注"
            placeholder="选填，特殊需求说明"
            maxlength="500"
          />
        </van-cell-group>

        <div style="margin: 20px 16px;">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            color="linear-gradient(135deg, #2d6a9f, #4299e1)"
            :loading="submitting"
          >
            提交预约
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 日期选择 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        :min-date="minDate"
        :max-date="maxDate"
        title="选择参观日期"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 时段选择 -->
    <van-action-sheet v-model:show="showTimePicker" title="选择参观时段">
      <div class="time-slots">
        <div
          v-for="slot in timeSlots"
          :key="slot.value"
          :class="['time-slot', { active: form.visitTimeSlot === slot.value }]"
          @click="form.visitTimeSlot = slot.value; showTimePicker = false"
        >
          <div class="slot-label">{{ slot.label }}</div>
          <div class="slot-desc">{{ slot.desc }}</div>
        </div>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import request from '@/api/index'

const router = useRouter()
const submitting = ref(false)
const showDatePicker = ref(false)
const showTimePicker = ref(false)

// 计算日期范围：今天到30天后
const today = new Date()
const minDate = new Date(today.getTime() + 86400000) // 明天
const maxDate = new Date(today.getTime() + 30 * 86400000)

const timeSlots = [
  { value: '上午', label: '上午 (9:00-12:00)', desc: '适合早起参观' },
  { value: '下午', label: '下午 (13:00-17:00)', desc: '适合午后参观' }
]

const form = reactive({
  visitorName: '',
  visitorPhone: '',
  idCard: '',
  visitorCount: 1,
  visitDate: '',
  visitTimeSlot: '',
  notes: ''
})

const onDateConfirm = ({ selectedValues }) => {
  form.visitDate = selectedValues.join('-')
  showDatePicker.value = false
}

const handleSubmit = async () => {
  // 检查登录
  const token = localStorage.getItem('userToken')
  if (!token) {
    showToast('请先登录')
    router.push('/login?redirect=/reservation')
    return
  }

  submitting.value = true
  try {
    const res = await request.post('/reservation/create', {
      ...form,
      visitorCount: Number(form.visitorCount)
    })
    if (res.code === 200) {
      showSuccessToast('预约提交成功')
      setTimeout(() => router.push('/my/reservations'), 1500)
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.reservation-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.page-content {
  padding: 12px 0;
}
.notice-card {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ebf8ff;
  border: 1px solid #bee3f8;
  border-radius: 8px;
  padding: 10px 14px;
  margin: 0 16px 16px;
  font-size: 12px;
  color: #2b6cb0;
}
.time-slots {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.time-slot {
  background: #f7f8fa;
  border: 2px solid #f7f8fa;
  border-radius: 10px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.time-slot.active {
  border-color: #4299e1;
  background: #ebf8ff;
}
.slot-label {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}
.slot-desc {
  font-size: 12px;
  color: #999;
}
</style>
