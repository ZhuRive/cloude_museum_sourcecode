package com.cloudmuseum.config;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * LangChain4j 配置类
 *
 * - 使用 DeepSeek API（兼容 OpenAI API 格式）
 * - 配置 StreamingChatLanguageModel 用于流式输出
 * - 配置独立线程池处理 AI 请求，避免阻塞 Tomcat 线程
 */
@Configuration
public class LangChain4jConfig {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com/v1}")
    private String baseUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String modelName;

    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .maxTokens(2048)
                .build();
    }

    @Bean("aiTaskExecutor")
    public Executor aiTaskExecutor() {
        return Executors.newCachedThreadPool();
    }
}
