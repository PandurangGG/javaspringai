package com.example.langchain4j.gemini.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j — Google Gemini bean configuration.
 *
 * Uses the Google AI Studio API (free tier available), not Google Cloud Vertex AI.
 * Get a free API key at: https://aistudio.google.com
 * Set env var: GEMINI_API_KEY
 */
@Configuration
public class LangChain4jGeminiConfig {

    @Value("${langchain4j.gemini.api-key}")
    private String apiKey;

    @Value("${langchain4j.gemini.model-name:gemini-1.5-flash}")
    private String modelName;

    @Bean("lc4jGeminiChatModel")
    public ChatLanguageModel lc4jGeminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }
}
