---
name: ai-guide-langchain4j
description: 基于 LangChain4j + DeepSeek 的智能讲解对话机器人实现方案
metadata:
  type: project
---

# AI 智能讲解功能 (华为杯核心亮点)

## 架构

```
用户端 H5 (Vue3+Vant) → SSE Stream → Spring Boot (LangChain4j) → DeepSeek API
                                        ↕ Redis (对话记忆持久化)
                                        ↕ MySQL (藏品档案→RAG上下文)
```

## 文件变更清单

### 新增文件 (6个后端 + 1个前端)

| 文件 | 作用 |
|------|------|
| `config/LangChain4jConfig.java` | DeepSeek 模型 Bean + AI 线程池 |
| `config/RedisChatMemoryStore.java` | LangChain4j ChatMemoryStore 的 Redis 实现 |
| `dto/ChatRequest.java` | 聊天请求体 (collectionId, message) |
| `controller/AiGuideController.java` | SSE 流式接口 `POST /api/ai-guide/chat` |
| `service/AiGuideService.java` | 核心服务: 多轮对话 + RAG + 流式输出 |
| `user-h5/src/components/AiChatPanel.vue` | 智能讲解对话面板 |

### 修改文件 (4个)

| 文件 | 改动 |
|------|------|
| `pom.xml` | 添加 `langchain4j-open-ai:0.35.0` |
| `application.yml` | 添加 `deepseek.api-key` 配置 |
| `config/WebMvcConfig.java` | 添加 `/ai-guide/**` 到认证拦截器 |
| `user-h5/src/views/CollectionDetail.vue` | 底部栏增加"AI讲解"按钮 + 集成 AiChatPanel |

## 启动步骤

### 1. 配置 DeepSeek API Key

```bash
# 方式一：环境变量
export DEEPSEEK_API_KEY=sk-your-key-here

# 方式二：直接编辑 application.yml
# deepseek.api-key: sk-your-key-here
```

### 2. 重启后端

```bash
cd server
mvn spring-boot:run
```

### 3. 前端 (通常已在运行，如有必要重启)

```bash
cd user-h5
npm run dev
```

### 4. 使用

1. 登录用户端 → 进入任意藏品详情页
2. 底部栏点击蓝色「AI讲解」按钮
3. 面板自动打开，AI 讲解员"小云"开始讲解
4. 可打字追问、播放语音讲解

## 华为杯答辩要点

**Why:** 这个方案在华为杯有竞争力是因为 LangChain4j 展示了对主流 AI 框架的工程化能力。

**How to apply:** 答辩时强调三点——(1) RAG架构: MySQL藏品档案→系统提示词的检索增强生成；(2) 多轮对话管理: Redis持久化+上下文窗口裁剪；(3) 可切换模型后端: 配置层支持替换为华为云ModelArts。

**扩展点:** 替换为华为云的代码只需改 `baseUrl` 和 `apiKey`。搜索增强 [[collection-rag-search]] 可加入 Embedding 向量检索做跨藏品问答。
