package com.example.langchain4j.ollama.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jOllamaConfig {

    @Value("${langchain4j.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${langchain4j.ollama.model-name:llama3.2}")
    private String modelName;

    @Bean("lc4jOllamaChatModel")
    public ChatLanguageModel lc4jOllamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    @Bean("lc4jOllamaStreamingChatModel")
    public StreamingChatLanguageModel lc4jOllamaStreamingChatModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }
}
