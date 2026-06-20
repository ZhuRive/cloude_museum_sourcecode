<template>
  <div class="model-viewer-wrap">
    <!-- 有模型 URL → 展示 3D -->
    <template v-if="modelUrl">
      <div
        ref="containerRef"
        class="model-container"
        :class="{ 'model-loaded': loaded }"
      >
        <!-- model-viewer web component -->
        <model-viewer
          v-if="showViewer"
          :src="modelUrl"
          :alt="`${name} 3D模型`"
          auto-rotate
          camera-controls
          interaction-prompt="auto"
          shadow-intensity="1"
          exposure="0.8"
          style="width: 100%; height: 100%;"
          @load="onLoad"
          @error="onError"
        >
          <!-- 加载指示器 (model-viewer 内置) -->
          <div slot="progress-bar" class="model-progress"></div>
        </model-viewer>

        <!-- 加载覆盖层（model-viewer 就绪前显示） -->
        <div v-if="!loaded && !loadError" class="model-loading">
          <van-loading type="spinner" color="#4299e1" />
          <p class="model-loading-text">正在加载 3D 模型…</p>
        </div>

        <!-- 加载失败 -->
        <div v-if="loadError" class="model-loading model-error">
          <van-icon name="warning-o" size="40" color="#e6a23c" />
          <p class="model-loading-text">3D 模型加载失败</p>
          <van-button size="small" round plain @click="retry">重试</van-button>
        </div>
      </div>

      <!-- 操作提示 -->
      <div class="model-hint" v-if="loaded">
        <span>🖱️ 拖拽旋转</span>
        <span>🔍 滚轮缩放</span>
        <span v-if="isARSupported">📱 支持 AR</span>
      </div>
    </template>

    <!-- 无模型 → 空状态 -->
    <div v-else class="model-empty">
      <div class="model-empty-icon">
        <van-icon name="cube" size="36" color="#d0d7de" />
      </div>
      <p class="model-empty-text">暂无 3D 模型数据</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import '@google/model-viewer'  // 注册 <model-viewer> Web Component

const props = defineProps({
  /** 3D 模型 URL (.glb 格式) */
  modelUrl: { type: String, default: '' },
  /** 藏品名称 */
  name: { type: String, default: '藏品' }
})

const showViewer = ref(false)
const loaded = ref(false)
const loadError = ref(false)
const isARSupported = ref(false)
const containerRef = ref(null)

let loadTimer = null

function onLoad() {
  loaded.value = true
  clearTimeout(loadTimer)
}

function onError(e) {
  console.error('3D模型加载失败:', e)
  loadError.value = true
  clearTimeout(loadTimer)
}

function retry() {
  loadError.value = false
  loaded.value = false
  // 重新挂载 model-viewer
  showViewer.value = false
  nextTick(() => {
    showViewer.value = true
  })
}

onMounted(async () => {
  // 检查 AR 支持
  isARSupported.value = 'xr' in navigator

  // 延迟挂载 model-viewer，确保 Web Component 就绪
  await nextTick()

  // 检查 model-viewer 自定义元素是否已注册
  if (!customElements.get('model-viewer')) {
    console.warn('model-viewer web component 未加载')
    loadError.value = true
    return
  }

  showViewer.value = true

  // 超时保护：30秒后仍未加载完成则报错
  loadTimer = setTimeout(() => {
    if (!loaded.value && !loadError.value) {
      console.warn('3D模型加载超时')
      loadError.value = true
    }
  }, 30000)
})

onBeforeUnmount(() => {
  clearTimeout(loadTimer)
})
</script>

<style scoped>
.model-viewer-wrap {
  margin-bottom: 12px;
}

/* ===== 模型容器 ===== */
.model-container {
  position: relative;
  width: 100%;
  height: 320px;
  background: #f0f2f5;
  border-radius: 10px;
  overflow: hidden;
}
.model-container.model-loaded {
  background: #eef2f7;
}

/* ===== 加载 / 错误覆盖层 ===== */
.model-loading {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: rgba(240, 242, 245, 0.9);
  z-index: 2;
}
.model-loading-text {
  font-size: 13px;
  color: #999;
}
.model-error .model-loading-text {
  color: #b45309;
  margin-bottom: 4px;
}

/* ===== model-viewer 进度条 ===== */
:deep(.model-progress) {
  display: block;
  width: 0%;
  height: 3px;
  background: linear-gradient(90deg, #4299e1, #63b3ed);
  position: absolute;
  bottom: 0;
  transition: width 0.3s;
}

/* ===== 操作提示 ===== */
.model-hint {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 8px 0 4px;
  font-size: 12px;
  color: #999;
}

/* ===== 空状态 ===== */
.model-empty {
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 10px;
  border: 1px dashed #d0d7de;
  gap: 8px;
}
.model-empty-icon {
  opacity: 0.6;
}
.model-empty-text {
  font-size: 13px;
  color: #bbb;
}
</style>
