package com.example.langchain4j.gemini.controller;

import com.example.langchain4j.gemini.service.LangChain4jGeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * LangChain4j — Google Gemini Provider
 *
 * Folder tells you: this uses LangChain4j backed by Google Gemini 1.5 Flash.
 * Gemini 1.5 Flash is fast, multimodal, and has a generous free tier.
 *
 * Free API key: https://aistudio.google.com
 * Set env var: GEMINI_API_KEY
 */
@RestController
@RequestMapping("/api/langchain4j/gemini")
@Tag(
        name = "LangChain4j — Google Gemini",
        description = """
                **Library:** LangChain4j 0.36.2  |  **Provider:** Google Gemini 1.5 Flash

                Calls Google's Gemini model via `GoogleAiGeminiChatModel` from LangChain4j.
                Gemini 1.5 Flash is fast and multimodal with a generous free tier.
                Available models: `gemini-1.5-flash` (default), `gemini-1.5-pro`, `gemini-pro`.

                Free API key at: https://aistudio.google.com — Set env var: `GEMINI_API_KEY`
                """)
public class LangChain4jGeminiController {

    private final LangChain4jGeminiService service;

    public LangChain4jGeminiController(LangChain4jGeminiService service) {
        this.service = service;
    }

    @Operation(
            summary = "Chat with Google Gemini via LangChain4j",
            description = "Calls `GoogleAiGeminiChatModel.generate()` through LangChain4j's `ChatLanguageModel` interface. " +
                          "Body: `{\"message\": \"What are Gemini's multimodal capabilities?\"}`")
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Google Gemini",
                "model",    "gemini-1.5-flash",
                "api",      "ChatLanguageModel.generate()",
                "response", service.chat(request.get("message")));
    }

    @Operation(
            summary = "AiServices with @SystemMessage — Gemini as standard assistant",
            description = "LangChain4j AiServices proxy with a system prompt, backed by Gemini. " +
                          "Body: `{\"message\": \"Explain how Gemini handles long-context inputs.\"}`")
    @PostMapping("/ai-service")
    public Map<String, String> aiService(@RequestBody Map<String, String> request) {
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Google Gemini",
                "api",      "AiServices + @SystemMessage",
                "response", service.aiServiceChat(request.get("message")));
    }

    @Operation(
            summary = "Domain-expert via @V template variables — fills {domain} and {format}",
            description = "LangChain4j `@V` fills `{domain}` and `{format}` in the system prompt at runtime. " +
                          "Body: `{\"domain\": \"machine learning\", \"format\": \"bullet-point\", \"message\": \"Explain gradient descent.\"}`")
    @PostMapping("/ai-service/domain")
    public Map<String, String> chatInDomain(@RequestBody Map<String, String> request) {
        String response = service.chatInDomain(
                request.get("domain"), request.get("format"), request.get("message"));
        return Map.of(
                "library",  "LangChain4j 0.36.2",
                "provider", "Google Gemini",
                "api",      "AiServices + @V template variables",
                "domain",   request.getOrDefault("domain", ""),
                "response", response);
    }

    @Operation(
            summary = "Multi-turn memory chat with @MemoryId",
            description = "Per-session conversation memory via LangChain4j `@MemoryId`. " +
                          "Body: `{\"sessionId\": \"carol\", \"message\": \"I am learning about neural networks.\"}` " +
                          "— send a follow-up to verify recall.")
    @PostMapping("/memory-chat")
    public Map<String, String> memoryChat(@RequestBody Map<String, String> request) {
        String response = service.chatWithMemory(request.get("sessionId"), request.get("message"));
        return Map.of(
                "library",   "LangChain4j 0.36.2",
                "provider",  "Google Gemini",
                "api",       "@MemoryId per-session memory",
                "sessionId", request.getOrDefault("sessionId", ""),
                "response",  response);
    }

    @Operation(summary = "Health check — library, provider, and key info")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "library",     "LangChain4j 0.36.2",
                "provider",    "Google Gemini",
                "models",      "gemini-1.5-flash (default), gemini-1.5-pro, gemini-pro",
                "requiresKey", "GEMINI_API_KEY",
                "freeKey",     "https://aistudio.google.com",
                "endpoints",   "/api/langchain4j/gemini/*"
        );
    }
}
