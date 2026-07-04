package com.example.langchain4j.openai.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j — OpenAI bean configuration.
 *
 * Beans use explicit names ("lc4jOpenAi*") so they don't conflict with
 * Spring AI's OpenAiChatModel auto-configuration, which produces a different
 * bean type (org.springframework.ai.openai.OpenAiChatModel) under a different name.
 */
@Configuration
public class LangChain4jOpenAiConfig {

    @Value("${langchain4j.openai.api-key}")
    private String apiKey;

    @Value("${langchain4j.openai.model-name:gpt-4o-mini}")
    private String modelName;

    @Bean("lc4jOpenAiChatModel")
    public ChatLanguageModel lc4jOpenAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    @Bean("lc4jOpenAiStreamingChatModel")
    public StreamingChatLanguageModel lc4jOpenAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }
}
