package com.example.langchain4j.ollama.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LangChain4jOllamaService {

    interface OllamaAssistant {
        @SystemMessage("You are a helpful local AI assistant running offline via Ollama. Be concise.")
        String chat(@UserMessage String message);
    }

    private final ChatLanguageModel chatModel;
    private final StreamingChatLanguageModel streamingChatModel;
    private final OllamaAssistant assistant;

    public LangChain4jOllamaService(
            @Qualifier("lc4jOllamaChatModel") ChatLanguageModel chatModel,
            @Qualifier("lc4jOllamaStreamingChatModel") StreamingChatLanguageModel streamingChatModel) {
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
        this.assistant = AiServices.builder(OllamaAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public String chat(String message) {
        return chatModel.generate(message);
    }

    public Flux<String> streamChat(String message) {
        return Flux.create(sink ->
                streamingChatModel.generate(message, new StreamingResponseHandler<AiMessage>() {
                    @Override public void onNext(String token) { sink.next(token); }
                    @Override public void onComplete(Response<AiMessage> response) { sink.complete(); }
                    @Override public void onError(Throwable error) { sink.error(error); }
                }));
    }

    public String aiServiceChat(String message) {
        return assistant.chat(message);
    }
}
