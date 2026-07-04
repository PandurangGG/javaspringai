package com.example.agent.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamService {

    private final ChatClient chatClient;

    public StreamService(AnthropicChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public Flux<String> stream(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }

    public Flux<String> streamWithSystemPrompt(String systemPrompt, String userMessage) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .stream()
                .content();
    }
}
