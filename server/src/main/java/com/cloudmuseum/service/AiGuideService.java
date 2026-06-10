package com.cloudmuseum.service;

import com.cloudmuseum.config.RedisChatMemoryStore;
import com.cloudmuseum.entity.Collection;
import com.cloudmuseum.repository.CollectionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * AI 智能讲解核心服务
 *
 * 基于 LangChain4j 实现完整的 RAG 式多轮对话流程：
 * 1. 从 MySQL 获取藏品档案 → 构建系统提示词
 * 2. 从 Redis 读取/写入对话历史 (ChatMemoryStore)
 * 3. 调用 DeepSeek API 进行流式对话生成
 * 4. 通过 SSE 将生成的 Token 逐字推送到前端
 *
 * 华为杯加分设计:
 * - LangChain4j 框架: 主流 LLM 编排框架，体现工程化能力
 * - Redis 记忆持久化: 真实业务场景的对话管理
 * - 流式输出: 极佳的交互体验
 * - 可切换模型后端: 替换为华为云 ModelArts 仅需改配置
 */
@Service
public class AiGuideService {

    private static final Logger log = LoggerFactory.getLogger(AiGuideService.class);

    /** 最大保留消息数 (系统消息 + 多轮对话)，防止超长上下文 */
    private static final int MAX_MESSAGES = 20;

    @Autowired
    private StreamingChatLanguageModel chatModel;

    @Autowired
    private RedisChatMemoryStore memoryStore;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("aiTaskExecutor")
    private Executor aiExecutor;

    /**
     * 流式对话入口
     *
     * @param userId       用户 ID
     * @param collectionId 藏品 ID
     * @param message      用户消息
     * @param emitter      SSE 发射器
     */
    public void streamChat(Long userId, Long collectionId, String message, SseEmitter emitter) {
        aiExecutor.execute(() -> {
            try {
                // 1. 获取藏品信息
                Collection collection = collectionRepository.findById(collectionId)
                        .orElseThrow(() -> new RuntimeException("藏品不存在，ID: " + collectionId));

                // 2. 构建会话 ID: user:{userId}:collection:{collectionId}
                //    同一用户对同一藏品始终共享对话历史
                String sessionId = "user:" + userId + ":collection:" + collectionId;

                // 3. 从 Redis 读取历史消息
                List<ChatMessage> messages = new ArrayList<>(memoryStore.getMessages(sessionId));

                // 4. 如果是新会话，注入系统提示词（含藏品档案）
                boolean isNewSession = messages.isEmpty();
                if (isNewSession) {
                    String systemPrompt = buildSystemPrompt(collection);
                    messages.add(new SystemMessage(systemPrompt));
                    sendEvent(emitter, "session", sessionId);
                }

                // 5. 添加用户消息
                messages.add(new UserMessage(message));

                // 6. 截断历史（保留系统消息 + 最近 N 条）
                trimMessages(messages);

                // 7. 调用 DeepSeek API 流式生成
                StringBuilder fullResponse = new StringBuilder();
                chatModel.generate(messages, new StreamingResponseHandler<AiMessage>() {
                    @Override
                    public void onNext(String token) {
                        fullResponse.append(token);
                        sendEvent(emitter, "token", token);
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
                        // 将 AI 回复加入历史并持久化到 Redis
                        messages.add(response.content());
                        memoryStore.updateMessages(sessionId, messages);

                        sendEvent(emitter, "done", "");
                        emitter.complete();
                        log.info("AI chat completed: session={}, tokens={}",
                                sessionId, fullResponse.length());
                    }

                    @Override
                    public void onError(Throwable error) {
                        log.error("AI streaming error: session={}, error={}",
                                sessionId, error.getMessage());
                        sendEvent(emitter, "error", "AI 服务暂时不可用，请稍后再试");
                        emitter.complete();
                    }
                });

            } catch (Exception e) {
                log.error("Chat error: {}", e.getMessage(), e);
                sendEvent(emitter, "error", e.getMessage());
                emitter.complete();
            }
        });
    }

    /**
     * 构建系统提示词 — 将藏品档案注入 LLM 上下文
     * 这是 RAG 思想的核心体现：将结构化数据转为 LLM 可理解的文本
     */
    private String buildSystemPrompt(Collection c) {
        return """
你是一位专业而亲切的博物馆AI讲解员，名叫"小云"。你正在云博物馆中为一位参观者进行一对一的智能讲解。

## 讲解风格
- 用口语化、热情、有感染力的语言，像朋友面对面聊天一样
- 适当运用比喻和联想（例如把文物和现代生活联系起来）
- 回答结构清晰，每段不要太长
- 可以在讲解中穿插一些小故事或冷知识

## 行为规则
1. 如果是对话的开始，请以热情的语气打招呼并系统性地介绍这件藏品的外观、历史和文化价值
2. 如果用户追问，结合藏品信息和你的知识储备做深入解答
3. 在合适的时候用反问句引导互动（例如"你知道它是在哪里被发现的吗？"）
4. 回答中用"你"而非"您"，保持亲切但不失尊重

## 当前藏品档案
名称: %s
朝代: %s
年代: %s
材质: %s
尺寸: %s
出土地: %s
藏品描述: %s
文化意义: %s
""".formatted(
                safe(c.getName()),
                safe(c.getDynasty()),
                safe(c.getEra()),
                safe(c.getMaterial()),
                safe(c.getSizeDesc()),
                safe(c.getOriginPlace()),
                safe(c.getDescription()),
                safe(c.getCulturalSignificance())
        );
    }

    /** 截断消息列表，保留系统消息 + 最近的 N 条 */
    private void trimMessages(List<ChatMessage> messages) {
        if (messages.size() <= MAX_MESSAGES) return;
        // 保留第一条（系统消息）和最近的 MAX_MESSAGES-1 条
        ChatMessage system = messages.get(0);
        List<ChatMessage> recent = messages.subList(messages.size() - (MAX_MESSAGES - 1), messages.size());
        messages.clear();
        messages.add(system);
        messages.addAll(recent);
    }

    private String safe(String s) {
        return s == null || s.isBlank() ? "暂无数据" : s;
    }

    /** 通过 SSE 发送事件 */
    private void sendEvent(SseEmitter emitter, String type, String content) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("type", type, "content", content));
            emitter.send(SseEmitter.event().data(json));
        } catch (IOException e) {
            // 客户端断开连接是正常情况，只记录 debug 日志
            log.debug("SSE send failed (client disconnected?): {}", e.getMessage());
        }
    }
}
