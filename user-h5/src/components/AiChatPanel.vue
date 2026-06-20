<template>
  <van-popup
    :show="visible"
    position="bottom"
    :style="{ height: '80vh' }"
    round
    closeable
    close-icon="cross"
    @close="handleClose"
  >
    <div class="chat-container">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <span class="ai-avatar">📜</span>
          <div class="header-text">
            <span class="header-title">AI 智能讲解</span>
            <span class="header-subtitle">{{ collectionName }}</span>
          </div>
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role]">
          <div class="msg-avatar" :class="msg.role">
            {{ msg.role === 'ai' ? '📜' : '👤' }}
          </div>
          <div class="msg-content">
            <div class="msg-bubble" :class="msg.role">
              <div class="msg-text">
                {{ msg.content }}<span v-if="msg.streaming" class="cursor">▍</span>
              </div>
              <div v-if="msg.role === 'ai' && msg.content && !msg.streaming" class="msg-actions">
                <span class="action-btn" @click="speak(msg.content)">
                  <van-icon name="volume-o" /> 语音
                </span>
                <span class="action-btn" @click="copyText(msg.content)">
                  <van-icon name="copy-o" /> 复制
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading && messages[messages.length - 1]?.role !== 'ai'" class="msg ai">
          <div class="msg-avatar ai">📜</div>
          <div class="msg-content">
            <div class="msg-bubble ai">
              <div class="typing">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入 -->
      <div class="chat-footer">
        <div class="input-wrap">
          <van-field
            v-model="inputText"
            placeholder="输入你想了解的问题…"
            :disabled="loading"
            @keypress.enter="sendMessage"
            :border="false"
            input-align="left"
            autosize
            type="textarea"
            rows="1"
          />
          <button
            :class="['send-btn', { active: !loading && inputText.trim() }]"
            :disabled="loading || !inputText.trim()"
            @click="sendMessage"
          >
            发送
          </button>
        </div>
        <div class="hint-text">按 Enter 发送</div>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { ref, watch, nextTick, onUnmounted } from 'vue'
import { showToast, showDialog } from 'vant'

const props = defineProps({
  visible: Boolean,
  collectionId: [Number, String],
  collectionName: { type: String, default: '' }
})

const emit = defineEmits(['close'])

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const messagesRef = ref(null)
let abortController = null

watch(() => props.visible, async (val) => {
  if (val) {
    messages.value = []
    await nextTick()
    startGuide()
  } else {
    abort()
  }
})

async function startGuide() {
  loading.value = true
  await streamChat('你好！请为我讲解这件藏品。')
  loading.value = false
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  await nextTick()
  scrollToBottom()

  loading.value = true
  await streamChat(text)
  loading.value = false
}

async function streamChat(message) {
  const token = localStorage.getItem('userToken')
  if (!token) {
    showToast('请先登录')
    return false
  }

  const aiMsg = { role: 'ai', content: '', streaming: true }
  messages.value.push(aiMsg)

  abortController = new AbortController()
  let success = false

  try {
    const response = await fetch('/api/ai-guide/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        collectionId: props.collectionId,
        message: message
      }),
      signal: abortController.signal
    })

    if (!response.ok) {
      aiMsg.streaming = false
      // token 过期 — 清除登录态并引导重新登录
      if (response.status === 401) {
        aiMsg.content = '登录已过期，请重新登录'
        localStorage.removeItem('userToken')
        localStorage.removeItem('userInfo')
        showDialog({
          title: '登录已过期',
          message: '登录信息已过期，请重新登录后使用AI智能讲解',
          confirmButtonText: '去登录'
        }).then(() => {
          window.location.href = '/login'
        })
        return false
      }
      aiMsg.content = '请求失败 (' + response.status + ')，请稍后重试'
      return false
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })

      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmed = line.trim()
        if (!trimmed.startsWith('data:')) continue

        const jsonStr = trimmed.slice(5).trim()
        if (!jsonStr) continue

        try {
          const data = JSON.parse(jsonStr)
          if (data.type === 'token') {
            aiMsg.content += data.content
            aiMsg.streaming = true
            triggerReactivity()
            scrollToBottom()
          } else if (data.type === 'done') {
            aiMsg.streaming = false
            success = true
            triggerReactivity()
            scrollToBottom()
          } else if (data.type === 'error') {
            aiMsg.content = data.content || '服务暂时不可用'
            aiMsg.streaming = false
            triggerReactivity()
          }
        } catch (e) { /* 跳过解析异常行 */ }
      }
    }
  } catch (e) {
    if (e.name !== 'AbortError') {
      aiMsg.content = '网络连接失败，请检查网络后重试'
      aiMsg.streaming = false
      triggerReactivity()
    }
  } finally {
    abortController = null
  }

  return success
}

function triggerReactivity() {
  messages.value = [...messages.value]
}

function scrollToBottom() {
  nextTick(() => {
    const el = messagesRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function speak(text) {
  if (!('speechSynthesis' in window)) {
    showToast('当前浏览器不支持语音播报')
    return
  }
  window.speechSynthesis.cancel()
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'zh-CN'
  utterance.rate = 1.0
  utterance.pitch = 1.0
  window.speechSynthesis.speak(utterance)
}

function copyText(text) {
  navigator.clipboard.writeText(text).then(() => {
    showToast('已复制')
  }).catch(() => {
    showToast('复制失败')
  })
}

function abort() {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
}

onUnmounted(() => abort())

function handleClose() {
  abort()
  emit('close')
}
</script>

<style scoped>
/* =========================================
   AI 智能讲解面板 — 陕忆风格
   水墨底色 · 鎏金点缀 · 宋体雅韵
   ========================================= */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--paper);
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

/* ===== 头部 ===== */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 16px 12px;
  background: var(--card);
  border-bottom: 1px solid var(--paper-dark);
  flex-shrink: 0;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.ai-avatar {
  font-size: 26px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gold-glow);
  border-radius: 10px;
}
.header-text {
  display: flex;
  flex-direction: column;
}
.header-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 2px;
}
.header-subtitle {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

/* ===== 消息区域 ===== */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  -webkit-overflow-scrolling: touch;
}
.msg {
  display: flex;
  gap: 10px;
  max-width: 90%;
  animation: fadeIn 0.3s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.msg.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.msg-avatar.ai {
  background: var(--gold-glow);
  border: 1px solid rgba(184, 148, 63, 0.2);
}
.msg-avatar.user {
  background: var(--paper-dark);
  border: 1px solid var(--paper-dark);
}
.msg-content {
  max-width: calc(100% - 46px);
}
.msg-bubble {
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}
.msg-bubble.ai {
  background: var(--card);
  border: 1px solid var(--paper-dark);
  border-bottom-left-radius: 4px;
}
.msg-bubble.user {
  background: linear-gradient(135deg, var(--gold), var(--gold-light));
  color: #fff;
  border-bottom-right-radius: 4px;
}
.msg-text {
  white-space: pre-wrap;
  word-break: break-word;
  letter-spacing: 0.3px;
}
.cursor {
  animation: blink 0.8s infinite;
  color: var(--gold);
  font-weight: bold;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
.msg-actions {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--paper-dark);
}
.action-btn {
  font-size: 12px;
  color: var(--gold);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: opacity 0.2s;
  letter-spacing: 0.5px;
}
.action-btn:active {
  opacity: 0.6;
}

/* ===== 打字指示器 ===== */
.typing {
  display: flex;
  gap: 5px;
  padding: 6px 0;
}
.typing span {
  width: 7px;
  height: 7px;
  background: var(--gold);
  border-radius: 50%;
  opacity: 0.4;
  animation: bounce 1.4s infinite ease-in-out;
}
.typing span:nth-child(2) { animation-delay: 0.16s; }
.typing span:nth-child(3) { animation-delay: 0.32s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); }
  40% { transform: scale(1); }
}

/* ===== 底部输入 ===== */
.chat-footer {
  padding: 8px 12px 16px;
  background: var(--card);
  border-top: 1px solid var(--paper-dark);
  flex-shrink: 0;
}
.input-wrap {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  background: var(--paper);
  border-radius: 24px;
  padding: 4px 4px 4px 16px;
  border: 1px solid var(--paper-dark);
  transition: border-color 0.2s;
}
.input-wrap:focus-within {
  border-color: var(--gold);
}
.input-wrap :deep(.van-field) {
  padding: 8px 0;
}
.input-wrap :deep(.van-field__control) {
  font-size: 14px;
  font-family: 'Noto Serif SC', serif;
}
.send-btn {
  width: 56px;
  height: 34px;
  border: none;
  border-radius: 17px;
  background: var(--paper-dark);
  color: var(--text-tertiary);
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.send-btn.active {
  background: var(--gold);
  color: #fff;
  box-shadow: 0 2px 8px var(--gold-glow);
}
.send-btn:active {
  transform: scale(0.95);
}
.hint-text {
  text-align: right;
  font-size: 11px;
  color: var(--text-tertiary);
  margin-top: 4px;
  padding-right: 4px;
  letter-spacing: 0.5px;
  opacity: 0.6;
}
</style>
