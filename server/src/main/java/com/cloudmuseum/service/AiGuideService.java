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
 * 基于 LangChain4j 实现 RAG 式多轮对话：
 * 1. MySQL 藏品档案 → 系统提示词
 * 2. Redis 对话历史持久化
 * 3. DeepSeek API 流式生成
 * 4. SSE 逐字推送
 */
@Service
public class AiGuideService {

    private static final Logger log = LoggerFactory.getLogger(AiGuideService.class);

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

    public void streamChat(Long userId, Long collectionId, String message, SseEmitter emitter) {
        aiExecutor.execute(() -> {
            try {
                Collection collection = collectionRepository.findById(collectionId)
                        .orElseThrow(() -> new RuntimeException("藏品不存在，ID: " + collectionId));

                String sessionId = "user:" + userId + ":collection:" + collectionId;

                List<ChatMessage> messages = new ArrayList<>(memoryStore.getMessages(sessionId));

                boolean isNewSession = messages.isEmpty();
                if (isNewSession) {
                    String systemPrompt = buildSystemPrompt(collection);
                    messages.add(new SystemMessage(systemPrompt));
                    sendEvent(emitter, "session", sessionId);
                }

                messages.add(new UserMessage(message));
                trimMessages(messages);

                StringBuilder fullResponse = new StringBuilder();
                chatModel.generate(messages, new StreamingResponseHandler<AiMessage>() {
                    @Override
                    public void onNext(String token) {
                        fullResponse.append(token);
                        sendEvent(emitter, "token", token);
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
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

    private void trimMessages(List<ChatMessage> messages) {
        if (messages.size() <= MAX_MESSAGES) return;
        ChatMessage system = messages.get(0);
        List<ChatMessage> recent = messages.subList(messages.size() - (MAX_MESSAGES - 1), messages.size());
        messages.clear();
        messages.add(system);
        messages.addAll(recent);
    }

    private String safe(String s) {
        return s == null || s.isBlank() ? "暂无数据" : s;
    }

    private void sendEvent(SseEmitter emitter, String type, String content) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("type", type, "content", content));
            emitter.send(SseEmitter.event().data(json));
        } catch (IOException e) {
            log.debug("SSE send failed (client disconnected?): {}", e.getMessage());
        }
    }
}
