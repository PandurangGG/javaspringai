package com.example.agent.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates multi-session conversation memory management.
 *
 * Each session gets its own isolated ChatClient backed by a dedicated
 * InMemoryChatMemoryRepository, guaranteeing that conversations across
 * different sessions never bleed into each other.
 *
 * Spring AI features showcased:
 *  - MessageWindowChatMemory with per-session InMemoryChatMemoryRepository
 *  - MessageChatMemoryAdvisor wired to session-scoped memory
 *  - Runtime session lifecycle: create, chat, list, clear
 */
@Service
public class ConversationService {

    private final AnthropicChatModel chatModel;

    // One ChatClient per named session — created lazily on first use
    private final Map<String, ChatClient> sessions = new ConcurrentHashMap<>();

    public ConversationService(AnthropicChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String sessionId, String message) {
        ChatClient client = sessions.computeIfAbsent(sessionId, this::createSessionClient);
        return client.prompt()
                .user(message)
                .call()
                .content();
    }

    public Set<String> getActiveSessions() {
        return Collections.unmodifiableSet(sessions.keySet());
    }

    public boolean clearSession(String sessionId) {
        return sessions.remove(sessionId) != null;
    }

    public int getSessionCount() {
        return sessions.size();
    }

    private ChatClient createSessionClient(String sessionId) {
        // Each session gets its own repository → complete memory isolation
        InMemoryChatMemoryRepository repo = new InMemoryChatMemoryRepository();
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repo)
                .maxMessages(20)
                .build();
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a helpful assistant. Remember details from the conversation history.")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }
}
