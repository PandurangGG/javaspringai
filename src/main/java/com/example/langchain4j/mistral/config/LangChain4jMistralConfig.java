package com.example.langchain4j.mistral.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j — Mistral AI bean configuration.
 * Reuses the MISTRAL_API_KEY env var shared with Spring AI Mistral.
 */
@Configuration
public class LangChain4jMistralConfig {

    @Value("${langchain4j.mistral.api-key}")
    private String apiKey;

    @Value("${langchain4j.mistral.model-name:mistral-small-latest}")
    private String modelName;

    @Bean("lc4jMistralChatModel")
    public ChatLanguageModel lc4jMistralChatModel() {
        return MistralAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    @Bean("lc4jMistralStreamingChatModel")
    public StreamingChatLanguageModel lc4jMistralStreamingChatModel() {
        return MistralAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
