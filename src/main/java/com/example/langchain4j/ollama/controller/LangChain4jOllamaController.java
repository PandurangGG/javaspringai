package com.example.langchain4j.ollama.controller;

import com.example.langchain4j.ollama.service.LangChain4jOllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * LangChain4j — Ollama Provider (local, no API key)
 *
 * Folder tells you: this uses LangChain4j backed by Ollama.
 * Combine this with /api/springai/ollama/* to compare frameworks on the same local model.
 */
@RestController
@RequestMapping("/api/langchain4j/ollama")
@Tag(
        name = "LangChain4j — Ollama (Local)",
        description = """
                **Library:** LangChain4j 0.36.2  |  **Provider:** Ollama (fully local, no API key)

                Runs LLMs locally via Ollama. No API key, no cloud, no cost.
                Demonstrates LangChain4j's `OllamaChatModel` and `OllamaStreamingChatModel`,
                plus `AiServices` with a local model backend.

                **Setup:** Install Ollama at https://ollama.ai then run: `ollama pull llama3.2`
                Compare with `/api/springai/ollama/*` — same local model, Spring AI framework.
                """)
public class LangChain4jOllamaController {

    private final LangChain4jOllamaService service;

    public LangChain4jOllamaController(LangChain4jOllamaService service) {
        this.service = service;
    }

    @Operation(
            summary = "Direct ChatLanguageModel.generate() with local Ollama",
            description = "LangChain4j's `OllamaChatModel.generate()` calls the local llama3.2 model. " +
                          "Body: `{\"message\": \"What are the advantages of running LLMs locally?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Ollama (local)",
                "model",    "llama3.2",
                "api",      "ChatLanguageModel.generate()",
                "response", service.chat(request.get("message")));
    }

    @Operation(
            summary = "Streaming chat via OllamaStreamingChatModel — SSE",
            description = "LangChain4j Ollama streaming via `StreamingResponseHandler` callbacks, " +
                          "wrapped to `Flux<String>` for SSE delivery. No API key needed.")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        return service.streamChat(request.get("message"));
    }

    @Operation(
            summary = "AiServices with @SystemMessage backed by local Ollama",
            description = "LangChain4j AiServices works with any `ChatLanguageModel` including Ollama. " +
                          "Body: `{\"message\": \"Introduce yourself briefly.\"}`")
    @PostMapping("/ai-service")
    public Map<String, String> aiService(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Ollama (local)",
                "api",      "AiServices + @SystemMessage",
                "response", service.aiServiceChat(request.get("message")));
    }

    @Operation(summary = "Health check — library, provider, and setup info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "Ollama (local, no API key)",
                "setup",     "Install: https://ollama.ai  then: ollama pull llama3.2",
                "models",    "llama3.2, mistral, phi3, gemma2, codellama, qwen2.5...",
                "endpoints", "/api/langchain4j/ollama/*"
        );
    }
}
