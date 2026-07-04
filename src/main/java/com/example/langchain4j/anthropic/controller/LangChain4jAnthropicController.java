package com.example.langchain4j.anthropic.controller;

import com.example.langchain4j.anthropic.service.LangChain4jAnthropicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * LangChain4j — Anthropic Claude Provider
 *
 * Folder tells you: this uses LangChain4j backed by Anthropic Claude.
 * The service code is identical to LangChain4jOpenAiService — only the config bean differs.
 * This proves LangChain4j's ChatLanguageModel is truly provider-agnostic.
 *
 * Reuses the same SPRING_AI_ANTHROPIC_API_KEY env var — no extra key needed.
 */
@RestController
@RequestMapping("/api/langchain4j/anthropic")
@Tag(
        name = "LangChain4j — Anthropic Claude",
        description = """
                **Library:** LangChain4j 0.36.2  |  **Provider:** Anthropic claude-3-haiku-20240307

                Demonstrates that LangChain4j's provider-agnostic `ChatLanguageModel` interface
                lets you swap providers by changing only the config bean — service code stays identical.
                Reuses `SPRING_AI_ANTHROPIC_API_KEY` (same key as the Spring AI Anthropic setup).

                Compare with `/api/langchain4j/openai/*` — identical code structure, different model.
                Compare with `/api/agent/*` — same Anthropic key, but Spring AI framework instead.
                """)
public class LangChain4jAnthropicController {

    private final LangChain4jAnthropicService service;

    public LangChain4jAnthropicController(LangChain4jAnthropicService service) {
        this.service = service;
    }

    @Operation(
            summary = "Direct ChatLanguageModel.generate() with Anthropic Claude",
            description = "LangChain4j calls Claude-3-Haiku via `chatModel.generate(message)`. " +
                          "Body: `{\"message\": \"What makes Claude different from GPT?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Anthropic Claude",
                "model",    "claude-3-haiku-20240307",
                "api",      "ChatLanguageModel.generate()",
                "response", service.chat(request.get("message")));
    }

    @Operation(
            summary = "AiServices with @SystemMessage — standard assistant role",
            description = "LangChain4j AiServices proxy injects a system prompt via `@SystemMessage`. " +
                          "Body: `{\"message\": \"What are the benefits of Spring AI?\"}`")
    @PostMapping("/ai-service")
    public Map<String, String> aiService(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Anthropic",
                "api",      "AiServices + @SystemMessage",
                "response", service.aiServiceChat(request.get("message")));
    }

    @Operation(
            summary = "Domain expert via @V template variables in @SystemMessage",
            description = "Fills `{expert}` and `{depth}` in the system prompt using `@V` annotations. " +
                          "Body: `{\"expert\": \"quantum physics\", \"depth\": \"beginner-friendly\", \"message\": \"Explain entanglement.\"}`")
    @PostMapping("/ai-service/expert")
    public Map<String, String> chatAsExpert(@RequestBody Map<String, String> request) {
        String response = service.chatAsExpert(
                request.get("expert"), request.get("depth"), request.get("message"));
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Anthropic",
                "api",      "AiServices + @V template variables",
                "expert",   request.getOrDefault("expert", ""),
                "response", response);
    }

    @Operation(
            summary = "Multi-turn memory chat with @MemoryId",
            description = "Each unique `sessionId` maintains its own `MessageWindowChatMemory`. " +
                          "Body: `{\"sessionId\": \"bob\", \"message\": \"My favourite language is Java.\"}` " +
                          "— follow up with another message in the same session to verify recall.")
    @PostMapping("/memory-chat")
    public Map<String, String> memoryChat(@RequestBody Map<String, String> request) {
        String response = service.chatWithMemory(request.get("sessionId"), request.get("message"));
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "Anthropic",
                "api",       "@MemoryId per-session memory",
                "sessionId", request.getOrDefault("sessionId", ""),
                "response",  response);
    }

    @Operation(summary = "Health check")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",     "LangChain4j 0.36.2",
                "provider",    "Anthropic Claude",
                "model",       "claude-3-haiku-20240307",
                "requiresKey", "SPRING_AI_ANTHROPIC_API_KEY (same as /api/agent/*)",
                "endpoints",   "/api/langchain4j/anthropic/*"
        );
    }
}
