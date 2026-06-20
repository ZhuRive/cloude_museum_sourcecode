<template>
  <div class="reservation-page">
    <van-nav-bar title="预 约 参 观" left-arrow @click-left="router.back()" />

    <div class="page-content">
      <!-- 通知卡片 -->
      <div class="notice-card">
        <span class="notice-icon">📋</span>
        <span class="notice-text">请至少提前一天预约，参观时请出示预约记录</span>
      </div>

      <!-- 预约表单 -->
      <van-form @submit="handleSubmit">
        <van-cell-group inset>
          <van-field
            v-model="form.visitorName"
            name="visitorName"
            label="参观人"
            placeholder="请输入姓名"
            :rules="[{ required: true, message: '请输入姓名' }]"
            label-class="form-label"
          />
          <van-field
            v-model="form.visitorPhone"
            name="visitorPhone"
            label="手机号"
            placeholder="请输入手机号"
            type="tel"
            maxlength="11"
            :rules="[{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
            label-class="form-label"
          />
          <van-field
            v-model="form.idCard"
            name="idCard"
            label="身份证"
            placeholder="选填"
            maxlength="18"
            label-class="form-label"
          />
          <van-field
            v-model="form.visitorCount"
            name="visitorCount"
            label="人数"
            placeholder="请输入参观人数"
            type="number"
            :rules="[{ required: true, message: '请输入人数' }]"
            label-class="form-label"
          />
          <van-field
            v-model="form.visitDate"
            is-link
            readonly
            name="visitDate"
            label="日期"
            placeholder="请选择参观日期"
            :rules="[{ required: true, message: '请选择日期' }]"
            @click="showDatePicker = true"
            label-class="form-label"
          />
          <van-field
            v-model="form.visitTimeSlot"
            is-link
            readonly
            name="visitTimeSlot"
            label="时段"
            placeholder="请选择参观时段"
            :rules="[{ required: true, message: '请选择时段' }]"
            @click="showTimePicker = true"
            label-class="form-label"
          />
          <van-field
            v-model="form.notes"
            name="notes"
            label="备注"
            placeholder="选填，特殊需求说明"
            maxlength="500"
            label-class="form-label"
          />
        </van-cell-group>

        <div class="submit-wrap">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="submitting"
            class="submit-btn"
          >
            提交预约
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 日期选择 -->
    <van-popup v-model:show="showDatePicker" position="bottom" round>
      <van-date-picker
        :min-date="minDate"
        :max-date="maxDate"
        title="选择参观日期"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
        :columns-top="' '"
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

const today = new Date()
const minDate = new Date(today.getTime() + 86400000)
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
  background: var(--paper);
}
.page-content {
  padding: 12px 0;
}

/* ---- 通知卡片 ---- */
.notice-card {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--card);
  border: 1px solid var(--paper-dark);
  border-left: 3px solid var(--gold);
  border-radius: var(--radius-sm);
  padding: 12px 14px;
  margin: 0 16px 16px;
  font-size: 12px;
  color: var(--text-secondary);
  letter-spacing: 0.5px;
}
.notice-icon {
  font-size: 16px;
  flex-shrink: 0;
}
.notice-text {
  line-height: 1.5;
}

/* ---- 表单 ---- */
:deep(.van-cell-group) {
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--card);
  margin: 0 16px;
}
:deep(.van-field__label) {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  color: var(--text-primary);
  width: 60px;
}
:deep(.van-field__body input) {
  font-size: 14px;
}
:deep(.van-field__control) {
  color: var(--text-primary);
}

.submit-wrap {
  margin: 24px 16px 16px;
}
.submit-btn {
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
  letter-spacing: 4px;
  height: 44px;
  border: none;
  box-shadow: 0 4px 12px var(--gold-glow);
}

/* ---- 时段选择 ---- */
.time-slots {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.time-slot {
  background: var(--paper);
  border: 2px solid var(--paper-dark);
  border-radius: var(--radius-md);
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.time-slot.active {
  border-color: var(--gold);
  background: var(--gold-glow);
}
.slot-label {
  font-family: 'Noto Serif SC', serif;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  letter-spacing: 1px;
}
.slot-desc {
  font-size: 12px;
  color: var(--text-tertiary);
}

/* ---- Picker 样式覆盖 ---- */
:deep(.van-picker__cancel), :deep(.van-picker__confirm) {
  color: var(--gold);
  font-family: 'Noto Serif SC', serif;
}
</style>
