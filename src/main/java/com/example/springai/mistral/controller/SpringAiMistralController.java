package com.example.springai.mistral.controller;

import com.example.springai.mistral.service.SpringAiMistralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Spring AI — Mistral AI Provider
 *
 * Folder tells you: this uses Spring AI (unified ChatClient API) backed by Mistral AI.
 * Mistral models (mistral-small, mistral-large, mixtral-8x7b) are European-made open-weight LLMs.
 *
 * Requires env var: MISTRAL_API_KEY  (get at: https://console.mistral.ai)
 */
@RestController
@RequestMapping("/api/springai/mistral")
@Tag(
        name = "Spring AI — Mistral AI",
        description = """
                **Library:** Spring AI 1.0.0  |  **Provider:** Mistral AI (mistral-small-latest)

                Uses Spring AI's unified `ChatClient` API backed by `MistralAiChatModel`.
                Mistral offers efficient open-weight models with strong reasoning and code capabilities.
                Available models: `mistral-small-latest`, `mistral-large-latest`, `open-mixtral-8x7b`.

                Requires env var: `MISTRAL_API_KEY` — get at https://console.mistral.ai
                Compare with `/api/langchain4j/mistral/*` for the LangChain4j equivalent.
                """)
public class SpringAiMistralController {

    private final SpringAiMistralService service;

    public SpringAiMistralController(SpringAiMistralService service) {
        this.service = service;
    }

    @Operation(
            summary = "Chat with Mistral AI via Spring AI ChatClient",
            description = "Uses `MistralAiChatModel` through Spring AI's `ChatClient`. " +
                          "Body: `{\"message\": \"What makes Mistral models unique?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String response = service.chat(request.get("message"));
        return Map.of("library", "Spring AI 1.0.0", "provider", "Mistral AI", "model", "mistral-small-latest", "response", response);
    }

    @Operation(
            summary = "Streaming chat with Mistral AI — Server-Sent Events",
            description = "SSE streaming with `chatClient.stream().content()` backed by Mistral. Requires `MISTRAL_API_KEY`.")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        return service.streamChat(request.get("message"));
    }

    @Operation(
            summary = "Chat with per-request MistralAiChatOptions",
            description = "Override `model` and `temperature` per request. " +
                          "Body: `{\"message\": \"Explain transformers\", \"temperature\": 0.3, \"model\": \"mistral-large-latest\"}`")
    @PostMapping("/chat/custom")
    public Map<String, String> chatCustom(@RequestBody Map<String, Object> request) {
        String message     = (String) request.get("message");
        Double temperature = request.get("temperature") instanceof Number n ? n.doubleValue() : null;
        String model       = (String) request.get("model");

        String response = service.chatWithOptions(message, temperature, model);
        return Map.of("library", "Spring AI 1.0.0", "provider", "Mistral AI", "response", response);
    }

    @Operation(summary = "Health check — library and provider info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",     "Spring AI 1.0.0",
                "provider",    "Mistral AI",
                "models",      "mistral-small-latest, mistral-large-latest, open-mixtral-8x7b",
                "requiresKey", "MISTRAL_API_KEY",
                "endpoints",   "/api/springai/mistral/*"
        );
    }
}
