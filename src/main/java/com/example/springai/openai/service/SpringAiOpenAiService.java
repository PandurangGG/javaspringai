package com.example.springai.openai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SpringAiOpenAiService {

    private final ChatClient chatClient;

    public SpringAiOpenAiService(OpenAiChatModel chatModel) {
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

    public String chatWithOptions(String message, Double temperature, String model, Integer maxTokens) {
        OpenAiChatOptions.Builder opts = OpenAiChatOptions.builder();
        if (temperature != null) opts.temperature(temperature);
        if (model != null && !model.isBlank()) opts.model(model);
        if (maxTokens != null) opts.maxTokens(maxTokens);

        return chatClient.prompt()
                .user(message)
                .options(opts.build())
                .call()
                .content();
    }
}
