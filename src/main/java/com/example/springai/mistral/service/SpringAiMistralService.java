package com.example.springai.mistral.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SpringAiMistralService {

    private final ChatClient chatClient;

    public SpringAiMistralService(MistralAiChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public Flux<String> streamChat(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    public <T> T chatStructured(String message, Class<T> responseType) {
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(responseType);
    }

    public String chatWithOptions(String message, Double temperature, String model) {
        MistralAiChatOptions.Builder opts = MistralAiChatOptions.builder();
        if (temperature != null) opts.temperature(temperature);
        if (model != null && !model.isBlank()) opts.model(model);

        return chatClient.prompt()
                .user(message)
                .options(opts.build())
                .call()
                .content();
    }
}
