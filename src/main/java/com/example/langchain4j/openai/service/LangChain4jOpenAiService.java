package com.example.langchain4j.openai.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * LangChain4j OpenAI service — demonstrates three key LangChain4j features:
 *
 *  1. ChatLanguageModel.generate()  — direct model call (raw API)
 *  2. AiServices + @SystemMessage   — interface-based AI proxy with annotations
 *  3. @MemoryId                     — automatic per-session conversation memory
 */
@Service
public class LangChain4jOpenAiService {

    // ── LangChain4j AiServices interface — annotations drive prompt assembly ──
    interface OpenAiAssistant {

        @SystemMessage("You are a helpful AI assistant powered by OpenAI via LangChain4j framework.")
        String chat(@UserMessage String message);

        @SystemMessage("You are a {role}. Always respond in a {style} tone.")
        String chatWithPersona(@V("role") String role, @V("style") String style, @UserMessage String message);
    }

    // ── Per-session memory: each unique sessionId gets its own chat history ──
    interface MemoryAssistant {
        String chat(@MemoryId String sessionId, @UserMessage String message);
    }

    private final ChatLanguageModel chatModel;
    private final StreamingChatLanguageModel streamingChatModel;
    private final OpenAiAssistant assistant;
    private final MemoryAssistant memoryAssistant;

    public LangChain4jOpenAiService(
            @Qualifier("lc4jOpenAiChatModel") ChatLanguageModel chatModel,
            @Qualifier("lc4jOpenAiStreamingChatModel") StreamingChatLanguageModel streamingChatModel) {

        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;

        ChatMemory sharedMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.assistant = AiServices.builder(OpenAiAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(sharedMemory)
                .build();

        this.memoryAssistant = AiServices.builder(MemoryAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemoryProvider(id -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    /** Raw ChatLanguageModel.generate() — simplest LangChain4j call */
    public String chat(String message) {
        return chatModel.generate(message);
    }

    /** StreamingChatLanguageModel via callback handler, wrapped into Flux for SSE */
    public Flux<String> streamChat(String message) {
        return Flux.create(sink ->
                streamingChatModel.generate(message, new StreamingResponseHandler<AiMessage>() {
                    @Override public void onNext(String token) { sink.next(token); }
                    @Override public void onComplete(Response<AiMessage> response) { sink.complete(); }
                    @Override public void onError(Throwable error) { sink.error(error); }
                }));
    }

    /** AiServices proxy — @SystemMessage sets the system role automatically */
    public String aiServiceChat(String message) {
        return assistant.chat(message);
    }

    /** AiServices with @V template variables filling {role} and {style} in @SystemMessage */
    public String aiServiceWithPersona(String role, String style, String message) {
        return assistant.chatWithPersona(role, style, message);
    }

    /** @MemoryId routes to separate MessageWindowChatMemory per sessionId */
    public String chatWithMemory(String sessionId, String message) {
        return memoryAssistant.chat(sessionId, message);
    }
}
