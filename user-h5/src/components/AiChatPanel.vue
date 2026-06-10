<template>
  <van-popup
    :show="visible"
    position="bottom"
    :style="{ height: '80vh' }"
    round
    closeable
    @close="handleClose"
  >
    <div class="chat-container">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <span class="ai-icon">🤖</span>
          <div class="header-text">
            <span class="header-title">AI 智能讲解</span>
            <span class="header-subtitle">{{ collectionName }}</span>
          </div>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role]">
          <div class="msg-avatar">{{ msg.role === 'ai' ? '🤖' : '👤' }}</div>
          <div class="msg-content">
            <div class="msg-bubble">
              <div class="msg-text">{{ msg.content }}<span v-if="msg.streaming" class="cursor">▍</span></div>
              <div v-if="msg.role === 'ai' && msg.content && !msg.streaming" class="msg-actions">
                <span class="action-btn" @click="speak(msg.content)">
                  <van-icon name="volume-o" /> 语音讲解
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
          <div class="msg-avatar">🤖</div>
          <div class="msg-content">
            <div class="msg-bubble">
              <div class="typing">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入框 -->
      <div class="chat-footer">
        <div class="input-wrap">
          <van-field
            v-model="inputText"
            placeholder="输入你想了解的问题..."
            :disabled="loading"
            @keypress.enter="sendMessage"
            :border="false"
            input-align="left"
            autosize
            type="textarea"
            rows="1"
          />
          <van-button
            :icon="loading ? 'loading' : 'chat-o'"
            type="primary"
            size="small"
            :disabled="loading || !inputText.trim()"
            @click="sendMessage"
            round
          />
        </div>
        <div class="hint-text">按 Enter 发送</div>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { ref, watch, nextTick, onUnmounted } from 'vue'
import { showToast } from 'vant'

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

// ========== 面板打开时自动开始讲解 ==========
watch(() => props.visible, async (val) => {
  if (val) {
    messages.value = []
    await nextTick()
    startGuide()
  } else {
    abort()
  }
})

/** 自动触发首轮讲解 */
async function startGuide() {
  loading.value = true
  await streamChat('你好！请为我讲解这件藏品。')
  loading.value = false
}

/** 发送用户消息 */
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

/** 核心：通过 SSE 流式获取 AI 回复 */
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
      aiMsg.content = '请求失败 (' + response.status + ')，请稍后重试'
      aiMsg.streaming = false
      return false
    }

    // 使用 ReadableStream API 逐行读取 SSE 数据
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })

      // 解析 SSE 格式: 每行以 "data:" 开头
      const lines = buffer.split('\n')
      buffer = lines.pop() || '' // 保留可能不完整的最后一行

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
          } else if (data.type === 'session') {
            // 会话 ID — 暂不需要前端处理
          } else if (data.type === 'error') {
            aiMsg.content = data.content || '服务暂时不可用'
            aiMsg.streaming = false
            triggerReactivity()
          }
        } catch (e) {
          // 跳过解析异常行
        }
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

/** 触发 Vue 响应式更新（用于流式追加内容） */
function triggerReactivity() {
  messages.value = [...messages.value]
}

/** 滚动到底部 */
function scrollToBottom() {
  nextTick(() => {
    const el = messagesRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

/** TTS 语音播报 — 使用浏览器 Web Speech API，无需额外费用 */
function speak(text) {
  if (!('speechSynthesis' in window)) {
    showToast('当前浏览器不支持语音播报')
    return
  }
  // 清除当前正在播放的语音
  window.speechSynthesis.cancel()

  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'zh-CN'
  utterance.rate = 1.0
  utterance.pitch = 1.0
  utterance.volume = 1.0
  window.speechSynthesis.speak(utterance)
}

/** 复制文本 */
function copyText(text) {
  navigator.clipboard.writeText(text).then(() => {
    showToast('已复制')
  }).catch(() => {
    showToast('复制失败')
  })
}

/** 中断请求 */
function abort() {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
}

onUnmounted(() => abort())

/** 关闭面板 */
function handleClose() {
  abort()
  emit('close')
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f7fa;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

/* ===== 头部 ===== */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 16px 12px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.ai-icon {
  font-size: 28px;
}
.header-text {
  display: flex;
  flex-direction: column;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a365d;
}
.header-subtitle {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  max-width: 88%;
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
  font-size: 20px;
  background: #f0f0f0;
  flex-shrink: 0;
}
.msg.user .msg-avatar {
  background: #e3f2fd;
}
.msg-content {
  max-width: calc(100% - 46px);
}
.msg-bubble {
  background: #fff;
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.7;
  color: #333;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.msg.user .msg-bubble {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.msg.ai .msg-bubble {
  border-bottom-left-radius: 4px;
}
.msg-text {
  white-space: pre-wrap;
  word-break: break-word;
}
.cursor {
  animation: blink 0.8s infinite;
  color: #409eff;
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
  border-top: 1px solid #f0f0f0;
}
.action-btn {
  font-size: 12px;
  color: #409eff;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: opacity 0.2s;
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
  background: #bbb;
  border-radius: 50%;
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
  padding: 8px 12px 12px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}
.input-wrap {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  background: #f5f7fa;
  border-radius: 24px;
  padding: 4px 4px 4px 16px;
}
.input-wrap :deep(.van-field) {
  padding: 8px 0;
}
.input-wrap :deep(.van-field__control) {
  font-size: 14px;
}
.hint-text {
  text-align: right;
  font-size: 11px;
  color: #ccc;
  margin-top: 4px;
  padding-right: 4px;
}
</style>
