package com.example.langchain4j.mistral.controller;

import com.example.langchain4j.mistral.service.LangChain4jMistralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * LangChain4j — Mistral AI Provider
 *
 * Folder tells you: this uses LangChain4j backed by Mistral AI.
 * Compare with /api/springai/mistral/* for the Spring AI equivalent.
 *
 * Requires env var: MISTRAL_API_KEY  (shared with Spring AI Mistral)
 */
@RestController
@RequestMapping("/api/langchain4j/mistral")
@Tag(
        name = "LangChain4j — Mistral AI",
        description = """
                **Library:** LangChain4j 0.36.2  |  **Provider:** Mistral AI (mistral-small-latest)

                Calls Mistral AI via `MistralAiChatModel` and `MistralAiStreamingChatModel` from LangChain4j.
                Available models: `mistral-small-latest`, `mistral-large-latest`, `open-mixtral-8x7b`.

                Requires env var: `MISTRAL_API_KEY` — shared with `/api/springai/mistral/*`.
                Compare with `/api/springai/mistral/*` — same provider, Spring AI framework.
                """)
public class LangChain4jMistralController {

    private final LangChain4jMistralService service;

    public LangChain4jMistralController(LangChain4jMistralService service) {
        this.service = service;
    }

    @Operation(
            summary = "Direct ChatLanguageModel.generate() with Mistral AI",
            description = "LangChain4j's `MistralAiChatModel.generate()` called directly. " +
                          "Body: `{\"message\": \"What makes Mistral models unique?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Mistral AI",
                "model",    "mistral-small-latest",
                "api",      "ChatLanguageModel.generate()",
                "response", service.chat(request.get("message")));
    }

    @Operation(
            summary = "Streaming via MistralAiStreamingChatModel — SSE",
            description = "LangChain4j Mistral streaming via `StreamingResponseHandler` callbacks, " +
                          "wrapped into `Flux<String>` for SSE. Requires `MISTRAL_API_KEY`.")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        return service.streamChat(request.get("message"));
    }

    @Operation(
            summary = "AiServices with @SystemMessage backed by Mistral",
            description = "LangChain4j AiServices proxy with a system role, powered by Mistral. " +
                          "Body: `{\"message\": \"Compare open-source and proprietary LLMs.\"}`")
    @PostMapping("/ai-service")
    public Map<String, String> aiService(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Mistral AI",
                "api",      "AiServices + @SystemMessage",
                "response", service.aiServiceChat(request.get("message")));
    }

    @Operation(summary = "Health check — library, provider, and env key info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",     "LangChain4j 0.36.2",
                "provider",    "Mistral AI",
                "models",      "mistral-small-latest, mistral-large-latest, open-mixtral-8x7b",
                "requiresKey", "MISTRAL_API_KEY",
                "endpoints",   "/api/langchain4j/mistral/*"
        );
    }
}
