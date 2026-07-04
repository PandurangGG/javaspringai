package com.example.langchain4j.anthropic.config;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j — Anthropic bean configuration.
 *
 * Reuses the same API key as the Spring AI Anthropic configuration
 * (SPRING_AI_ANTHROPIC_API_KEY env var) so no additional key is needed.
 *
 * Bean name "lc4jAnthropicChatModel" avoids any conflict with Spring AI's
 * AnthropicChatModel bean which is of a different class
 * (org.springframework.ai.anthropic.AnthropicChatModel).
 */
@Configuration
public class LangChain4jAnthropicConfig {

    @Value("${langchain4j.anthropic.api-key}")
    private String apiKey;

    @Value("${langchain4j.anthropic.model-name:claude-3-haiku-20240307}")
    private String modelName;

    @Bean("lc4jAnthropicChatModel")
    public ChatLanguageModel lc4jAnthropicChatModel() {
        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }
}
