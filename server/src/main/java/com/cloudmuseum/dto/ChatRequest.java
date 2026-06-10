package com.cloudmuseum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI 智能讲解 - 聊天请求体
 */
@Data
public class ChatRequest {

    /** 当前讲解的藏品 ID */
    @NotNull(message = "藏品ID不能为空")
    private Long collectionId;

    /** 用户发送的消息 */
    @NotBlank(message = "消息不能为空")
    private String message;
}
