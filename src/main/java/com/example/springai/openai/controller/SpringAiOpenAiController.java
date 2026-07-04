package com.example.springai.openai.controller;

import com.example.springai.openai.service.SpringAiOpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Spring AI — OpenAI Provider
 *
 * Folder tells you: this uses Spring AI (unified ChatClient API) backed by OpenAI.
 * Compare with /api/langchain4j/openai/* to see the same provider through a different framework.
 *
 * Requires env var: OPENAI_API_KEY
 */
@RestController
@RequestMapping("/api/springai/openai")
@Tag(
        name = "Spring AI — OpenAI",
        description = """
                **Library:** Spring AI 1.0.0  |  **Provider:** OpenAI GPT-4o / GPT-4o-mini

                Uses Spring AI's unified `ChatClient` API backed by `OpenAiChatModel`.
                The ChatClient fluent API is identical to the Anthropic version — only the injected
                model bean changes. Set env var `OPENAI_API_KEY` before calling these endpoints.

                Compare with `/api/langchain4j/openai/*` to see the same provider via LangChain4j.
                """)
public class SpringAiOpenAiController {

    private final SpringAiOpenAiService service;

    public SpringAiOpenAiController(SpringAiOpenAiService service) {
        this.service = service;
    }

    @Operation(
            summary = "Chat with OpenAI via Spring AI ChatClient",
            description = "Spring AI's `chatClient.prompt().user(message).call().content()` with `OpenAiChatModel`. " +
                          "Body: `{\"message\": \"What is GPT-4o-mini?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String response = service.chat(request.get("message"));
        return Map.of("library", "Spring AI 1.0.0", "provider", "OpenAI", "model", "gpt-4o-mini", "response", response);
    }

    @Operation(
            summary = "Streaming chat with OpenAI — Server-Sent Events",
            description = "Uses `chatClient.prompt().stream().content()` returning `Flux<String>` as SSE. " +
                          "Open in browser or use: `curl -N -X POST /api/springai/openai/stream -d '{\"message\":\"Tell a story\"}'`")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        return service.streamChat(request.get("message"));
    }

    @Operation(
            summary = "Chat with per-request OpenAiChatOptions",
            description = "Override `model`, `temperature`, and `maxTokens` per request using `OpenAiChatOptions.builder()`. " +
                          "Body: `{\"message\": \"Write a haiku\", \"temperature\": 0.9, \"model\": \"gpt-4o\", \"maxTokens\": 200}`")
    @PostMapping("/chat/custom")
    public Map<String, String> chatCustom(@RequestBody Map<String, Object> request) {
        String message     = (String) request.get("message");
        Double temperature = request.get("temperature") instanceof Number n ? n.doubleValue() : null;
        String model       = (String) request.get("model");
        Integer maxTokens  = request.get("maxTokens") instanceof Number n ? n.intValue() : null;

        String response = service.chatWithOptions(message, temperature, model, maxTokens);
        return Map.of("library", "Spring AI 1.0.0", "provider", "OpenAI", "response", response);
    }

    @Operation(summary = "Health check — library and provider info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",     "Spring AI 1.0.0",
                "provider",    "OpenAI",
                "models",      "gpt-4o-mini (default), gpt-4o, gpt-3.5-turbo",
                "requiresKey", "OPENAI_API_KEY",
                "endpoints",   "/api/springai/openai/*"
        );
    }
}
