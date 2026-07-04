package com.example.langchain4j.gemini.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LangChain4jGeminiService {

    interface GeminiAssistant {

        @SystemMessage("You are a helpful AI assistant powered by Google Gemini via LangChain4j.")
        String chat(@UserMessage String message);

        @SystemMessage("You are an expert in {domain}. Always provide {format} answers with clear structure.")
        String chatInDomain(
                @V("domain") String domain,
                @V("format") String format,
                @UserMessage String message);
    }

    interface GeminiMemoryAssistant {
        String chat(@MemoryId String sessionId, @UserMessage String message);
    }

    private final ChatLanguageModel chatModel;
    private final GeminiAssistant assistant;
    private final GeminiMemoryAssistant memoryAssistant;

    public LangChain4jGeminiService(@Qualifier("lc4jGeminiChatModel") ChatLanguageModel chatModel) {
        this.chatModel = chatModel;

        this.assistant = AiServices.builder(GeminiAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        this.memoryAssistant = AiServices.builder(GeminiMemoryAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemoryProvider(id -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public String chat(String message) {
        return chatModel.generate(message);
    }

    public String aiServiceChat(String message) {
        return assistant.chat(message);
    }

    public String chatInDomain(String domain, String format, String message) {
        return assistant.chatInDomain(domain, format, message);
    }

    public String chatWithMemory(String sessionId, String message) {
        return memoryAssistant.chat(sessionId, message);
    }
}
