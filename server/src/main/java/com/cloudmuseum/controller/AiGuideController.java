package com.cloudmuseum.controller;

import com.cloudmuseum.dto.ChatRequest;
import com.cloudmuseum.service.AiGuideService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 智能讲解控制器
 *
 * 提供 SSE (Server-Sent Events) 流式接口，
 * 支持多轮对话，对话历史由 Redis 持久化。
 *
 * 接口: POST /api/ai-guide/chat
 * 认证: 需要 JWT Token (在 WebMvcConfig 中配置)
 * 响应: text/event-stream 格式的 SSE 流
 *
 * SSE 事件格式:
 *   data: {"type":"token","content":"你好"}   — AI 输出的文本片段
 *   data: {"type":"session","content":"xxx"}  — 会话 ID
 *   data: {"type":"done"}                     — 响应结束
 *   data: {"type":"error","content":"..."}    — 错误信息
 */
@RestController
@RequestMapping("/ai-guide")
public class AiGuideController {

    @Autowired
    private AiGuideService aiGuideService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody @Valid ChatRequest request,
                           HttpServletRequest servletRequest) {
        // 从 JWT 中获取用户 ID (由 AuthInterceptor 设置)
        Long userId = (Long) servletRequest.getAttribute("userId");
        if (userId == null) userId = 0L; // 兜底

        // 创建 SSE Emitter，超时 5 分钟
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        emitter.onTimeout(emitter::complete);

        // 异步执行 AI 请求，不阻塞 Tomcat 线程
        aiGuideService.streamChat(userId, request.getCollectionId(), request.getMessage(), emitter);

        return emitter;
    }
}
