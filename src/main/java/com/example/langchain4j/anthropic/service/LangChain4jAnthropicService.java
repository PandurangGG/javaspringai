package com.example.langchain4j.anthropic.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * LangChain4j Anthropic service — same framework (LangChain4j) as the OpenAI variant,
 * but backed by Anthropic Claude-3-Haiku instead of GPT-4o-mini.
 *
 * This demonstrates that LangChain4j's ChatLanguageModel interface is provider-agnostic:
 * switching from OpenAI to Anthropic only changes the config bean, not the service code.
 */
@Service
public class LangChain4jAnthropicService {

    interface AnthropicAssistant {

        @SystemMessage("You are a helpful AI assistant powered by Anthropic Claude via LangChain4j.")
        String chat(@UserMessage String message);

        @SystemMessage("You are a {expert} expert. Provide {depth} level explanations.")
        String chatAsExpert(
                @V("expert") String expert,
                @V("depth") String depth,
                @UserMessage String message);
    }

    interface AnthropicMemoryAssistant {
        String chat(@MemoryId String sessionId, @UserMessage String message);
    }

    private final ChatLanguageModel chatModel;
    private final AnthropicAssistant assistant;
    private final AnthropicMemoryAssistant memoryAssistant;

    public LangChain4jAnthropicService(@Qualifier("lc4jAnthropicChatModel") ChatLanguageModel chatModel) {
        this.chatModel = chatModel;

        this.assistant = AiServices.builder(AnthropicAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        this.memoryAssistant = AiServices.builder(AnthropicMemoryAssistant.class)
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

    public String chatAsExpert(String expert, String depth, String message) {
        return assistant.chatAsExpert(expert, depth, message);
    }

    public String chatWithMemory(String sessionId, String message) {
        return memoryAssistant.chat(sessionId, message);
    }
}
