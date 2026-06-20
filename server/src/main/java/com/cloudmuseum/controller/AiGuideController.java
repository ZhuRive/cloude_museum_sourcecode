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
 * SSE 流式接口: POST /api/ai-guide/chat
 * 认证: 需要 JWT Token
 * 响应: text/event-stream
 */
@RestController
@RequestMapping("/ai-guide")
public class AiGuideController {

    @Autowired
    private AiGuideService aiGuideService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody @Valid ChatRequest request,
                           HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        if (userId == null) userId = 0L;

        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        emitter.onTimeout(emitter::complete);

        aiGuideService.streamChat(userId, request.getCollectionId(), request.getMessage(), emitter);
        return emitter;
    }
}
