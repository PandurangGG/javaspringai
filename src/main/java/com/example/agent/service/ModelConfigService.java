package com.example.agent.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Demonstrates three unused Spring AI features:
 *  1. AnthropicChatOptions  — per-request model parameter overrides
 *  2. SimpleLoggerAdvisor   — automatic prompt/response logging to SLF4J
 *  3. SystemPromptTemplate  — parameterized system prompts
 */
@Service
public class ModelConfigService {

    private final ChatClient chatClient;

    public ModelConfigService(AnthropicChatModel chatModel) {
        // SimpleLoggerAdvisor logs every request and response at DEBUG level
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    // ── AnthropicChatOptions: fully custom per-request config ────────────────

    public String chatWithOptions(String message, Double temperature, Integer maxTokens, String model) {
        AnthropicChatOptions.Builder opts = AnthropicChatOptions.builder();
        if (temperature != null) opts.temperature(temperature);
        if (maxTokens   != null) opts.maxTokens(maxTokens);
        if (model != null && !model.isBlank()) opts.model(model);

        return chatClient.prompt()
                .user(message)
                .options(opts.build())
                .call()
                .content();
    }

    // ── Presets built on top of chatWithOptions ──────────────────────────────

    public String chatCreative(String message) {
        // High temperature → more varied, imaginative output
        return chatWithOptions(message, 0.95, 1500, null);
    }

    public String chatPrecise(String message) {
        // Low temperature → deterministic, factual output
        return chatWithOptions(message, 0.1, 800, null);
    }

    public String chatFast(String message) {
        // Switch to Haiku for low-latency, cost-efficient responses
        return chatWithOptions(message, 0.7, 800, "claude-haiku-4-5-20251001");
    }

    // ── SystemPromptTemplate: parameterized system prompts ───────────────────

    public String chatWithPersona(String message, String role, String language, String style) {
        // SystemPromptTemplate.render() fills {variable} placeholders and returns the rendered String
        String systemContent = new SystemPromptTemplate(
                "You are a {role}. Always respond in {language}. Your tone and style should be {style}.")
                .render(Map.of(
                        "role",     role,
                        "language", language,
                        "style",    style));

        return chatClient.prompt()
                .system(systemContent)
                .user(message)
                .call()
                .content();
    }
}
