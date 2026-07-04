package com.example.agent.service;

import com.example.agent.tools.AgentTools;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

/**
 * Core agent service.
 *
 * Uses Spring AI's ChatClient with:
 *  - Tool calling (the model can invoke @Tool methods autonomously)
 *  - In-memory conversation history (multi-turn memory)
 *  - A system prompt that instructs the model to behave as an agent
 */
@Service
public class AgentService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            You are a helpful AI agent with access to a set of tools.
            When answering user requests:
            - Think step by step before acting.
            - Use available tools whenever they are relevant to the task.
            - You can call multiple tools in sequence to complete complex tasks.
            - Always confirm what you did and summarize the final result clearly.
            """;

    public AgentService(AnthropicChatModel chatModel, AgentTools agentTools) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                // Register all @Tool methods from AgentTools
                .defaultTools(agentTools)
                // Keep a sliding window of last 20 messages in memory
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                        .chatMemoryRepository(new InMemoryChatMemoryRepository())
                        .maxMessages(20)
                        .build()).build())
                .build();
    }

    /**
     * Process a user message and return the agent's response.
     * The model may call tools one or more times before producing a final answer.
     */
    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
