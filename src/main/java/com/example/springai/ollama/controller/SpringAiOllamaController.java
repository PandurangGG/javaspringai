package com.example.springai.ollama.controller;

import com.example.springai.ollama.service.SpringAiOllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Spring AI — Ollama Provider (local, no API key needed)
 *
 * Folder tells you: this uses Spring AI (unified ChatClient API) backed by Ollama.
 * The ChatClient code is identical to OpenAI/Anthropic — only OllamaChatModel is injected.
 * No cloud API key required — runs fully local via Ollama.
 *
 * Setup: https://ollama.ai — then: ollama pull llama3.2
 */
@RestController
@RequestMapping("/api/springai/ollama")
@Tag(
        name = "Spring AI — Ollama (Local)",
        description = """
                **Library:** Spring AI 1.0.0  |  **Provider:** Ollama (fully local, no API key)

                Uses Spring AI's unified `ChatClient` API backed by `OllamaChatModel`.
                Runs LLMs locally — privacy-friendly and free. Supports llama3.2, mistral, phi3, codellama, and any model you `ollama pull`.

                **Setup:** Install Ollama at https://ollama.ai then run: `ollama pull llama3.2`
                Compare with `/api/langchain4j/ollama/*` for the LangChain4j equivalent.
                """)
public class SpringAiOllamaController {

    private final SpringAiOllamaService service;

    public SpringAiOllamaController(SpringAiOllamaService service) {
        this.service = service;
    }

    @Operation(
            summary = "Chat with local LLM via Spring AI + Ollama",
            description = "Calls `OllamaChatModel` (llama3.2 by default) through Spring AI's `ChatClient`. " +
                          "Runs fully locally — no cloud API call. Body: `{\"message\": \"Explain quantum computing in simple terms.\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String response = service.chat(request.get("message"));
        return Map.of("library", "Spring AI 1.0.0", "provider", "Ollama (local)", "model", "llama3.2", "response", response);
    }

    @Operation(
            summary = "Streaming chat with local Ollama — Server-Sent Events",
            description = "Local LLM tokens stream via SSE. Uses `chatClient.stream().content()` — " +
                          "the same API as cloud providers. No API key needed.")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        return service.streamChat(request.get("message"));
    }

    @Operation(
            summary = "Chat with a custom system prompt",
            description = "Demonstrates injecting a system-level instruction per request. " +
                          "Body: `{\"system\": \"You are a pirate. Speak like one.\", \"message\": \"Tell me about the sea.\"}`")
    @PostMapping("/chat/with-system")
    public Map<String, String> chatWithSystem(@RequestBody Map<String, String> request) {
        String response = service.chatWithSystem(request.get("system"), request.get("message"));
        return Map.of("library", "Spring AI 1.0.0", "provider", "Ollama (local)", "response", response);
    }

    @Operation(summary = "Health check — library, provider, and setup info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",    "Spring AI 1.0.0",
                "provider",   "Ollama (local, no API key)",
                "setup",      "Install: https://ollama.ai  then: ollama pull llama3.2",
                "models",     "llama3.2, mistral, phi3, codellama, gemma2, qwen2.5...",
                "endpoints",  "/api/springai/ollama/*"
        );
    }
}
