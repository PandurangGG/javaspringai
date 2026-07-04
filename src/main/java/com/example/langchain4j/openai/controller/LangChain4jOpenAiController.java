package com.example.langchain4j.openai.controller;

import com.example.langchain4j.openai.service.LangChain4jOpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * LangChain4j — OpenAI Provider
 *
 * Folder tells you: this uses LangChain4j (alternative AI framework) backed by OpenAI.
 * Compare with /api/springai/openai/* to see the same provider through Spring AI.
 *
 * LangChain4j key concepts shown here:
 *   - ChatLanguageModel.generate()        — raw call, returns String
 *   - StreamingChatLanguageModel          — callback-based streaming
 *   - AiServices + @SystemMessage/@UserMessage — interface proxy
 *   - @V                                  — template variable injection
 *   - @MemoryId                           — per-session memory routing
 *
 * Requires env var: OPENAI_API_KEY
 */
@RestController
@RequestMapping("/api/langchain4j/openai")
@Tag(
        name = "LangChain4j — OpenAI",
        description = """
                **Library:** LangChain4j 0.36.2  |  **Provider:** OpenAI GPT-4o-mini

                Demonstrates LangChain4j's unique abstractions vs Spring AI:
                - `ChatLanguageModel.generate()` — direct, no builder chain required
                - `AiServices` — annotate a Java interface, LangChain4j creates the proxy
                - `@SystemMessage` / `@UserMessage` / `@V` — declarative prompt engineering
                - `@MemoryId` — automatic per-session memory without manual session maps

                Requires env var: `OPENAI_API_KEY`.
                Compare with `/api/springai/openai/*` for the Spring AI equivalent.
                """)
public class LangChain4jOpenAiController {

    private final LangChain4jOpenAiService service;

    public LangChain4jOpenAiController(LangChain4jOpenAiService service) {
        this.service = service;
    }

    @Operation(
            summary = "Direct ChatLanguageModel.generate() — LangChain4j raw API",
            description = "Calls `chatModel.generate(message)` — LangChain4j's simplest chat API, " +
                          "no builder chain needed. Body: `{\"message\": \"What is LangChain4j?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "OpenAI",
                "api",       "ChatLanguageModel.generate()",
                "response",  service.chat(request.get("message")));
    }

    @Operation(
            summary = "Streaming via StreamingChatLanguageModel — SSE",
            description = "LangChain4j streaming uses `StreamingResponseHandler` callbacks, " +
                          "which this endpoint wraps into a `Flux<String>` for SSE delivery. " +
                          "Requires `OPENAI_API_KEY`.")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        return service.streamChat(request.get("message"));
    }

    @Operation(
            summary = "AiServices with @SystemMessage / @UserMessage annotations",
            description = "LangChain4j creates an AI proxy from a plain Java interface. " +
                          "`@SystemMessage` sets the system prompt; `@UserMessage` marks the input param. " +
                          "Body: `{\"message\": \"Explain your role.\"}`")
    @PostMapping("/ai-service")
    public Map<String, String> aiService(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "OpenAI",
                "api",       "AiServices + @SystemMessage",
                "response",  service.aiServiceChat(request.get("message")));
    }

    @Operation(
            summary = "AiServices persona via @V template variable injection",
            description = "LangChain4j `@V` fills `{role}` and `{style}` placeholders in `@SystemMessage` at runtime. " +
                          "Body: `{\"role\": \"senior software architect\", \"style\": \"formal\", \"message\": \"Review microservices vs monolith.\"}`")
    @PostMapping("/ai-service/persona")
    public Map<String, String> aiServiceWithPersona(@RequestBody Map<String, String> request) {
        String response = service.aiServiceWithPersona(
                request.get("role"), request.get("style"), request.get("message"));
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "OpenAI",
                "api",       "AiServices + @V template variables",
                "role",      request.getOrDefault("role", ""),
                "response",  response);
    }

    @Operation(
            summary = "Multi-turn chat with per-session memory via @MemoryId",
            description = "LangChain4j `@MemoryId` automatically routes each `sessionId` to its own " +
                          "`MessageWindowChatMemory` — no manual session map needed. " +
                          "Body: `{\"sessionId\": \"alice\", \"message\": \"My name is Alice. Remember it.\"}`" +
                          " — then ask a follow-up to verify memory.")
    @PostMapping("/memory-chat")
    public Map<String, String> memoryChat(@RequestBody Map<String, String> request) {
        String response = service.chatWithMemory(request.get("sessionId"), request.get("message"));
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "OpenAI",
                "api",       "@MemoryId per-session memory",
                "sessionId", request.getOrDefault("sessionId", ""),
                "response",  response);
    }

    @Operation(summary = "Health check — library, provider, and env key info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",     "LangChain4j 0.36.2",
                "provider",    "OpenAI",
                "models",      "gpt-4o-mini (default), gpt-4o, gpt-3.5-turbo",
                "requiresKey", "OPENAI_API_KEY",
                "endpoints",   "/api/langchain4j/openai/*"
        );
    }
}
