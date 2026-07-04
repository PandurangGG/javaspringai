package com.example.agent.controller;

import com.example.agent.service.ModelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/model-config")
@Tag(name = "Model Configuration",
        description = "Per-request model parameter tuning via AnthropicChatOptions, " +
                      "automatic prompt/response logging via SimpleLoggerAdvisor, " +
                      "and dynamic AI personas via SystemPromptTemplate.")
public class ModelConfigController {

    private final ModelConfigService service;

    public ModelConfigController(ModelConfigService service) {
        this.service = service;
    }

    @Operation(
            summary = "Chat with custom model options",
            description = "Overrides temperature, maxTokens, and/or model per-request using AnthropicChatOptions. " +
                          "All requests through this controller are logged by SimpleLoggerAdvisor (check application logs). " +
                          "Body: { \"message\": \"...\", \"temperature\": 0.7, \"maxTokens\": 500, \"model\": \"claude-haiku-4-5-20251001\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Explain recursion in one sentence.\", \"temperature\": 0.3, \"maxTokens\": 100}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"response\": \"Recursion is a technique where a function calls itself to solve smaller instances of the same problem.\"}")))
    )
    @PostMapping("/chat")
    public Map<String, String> chatWithOptions(@RequestBody Map<String, Object> request) {
        String message     = (String)  request.get("message");
        Double temperature = request.get("temperature") != null
                ? ((Number) request.get("temperature")).doubleValue() : null;
        Integer maxTokens  = request.get("maxTokens") != null
                ? ((Number) request.get("maxTokens")).intValue() : null;
        String model       = (String)  request.get("model");

        return Map.of("response", service.chatWithOptions(message, temperature, maxTokens, model));
    }

    @Operation(
            summary = "Creative mode (temperature = 0.95)",
            description = "Uses AnthropicChatOptions with temperature=0.95 and maxTokens=1500 for imaginative, varied output. " +
                          "Best for storytelling, brainstorming, and poetry. Body: { \"message\": \"Write a poem about Java.\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Write a short, whimsical story about a Java developer who discovers Spring AI.\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"response\": \"Once upon a time, in a land of curly braces...\"}")))
    )
    @PostMapping("/creative")
    public Map<String, String> chatCreative(@RequestBody Map<String, String> request) {
        return Map.of("response", service.chatCreative(request.get("message")));
    }

    @Operation(
            summary = "Precise mode (temperature = 0.1)",
            description = "Uses AnthropicChatOptions with temperature=0.1 for deterministic, factual, consistent output. " +
                          "Best for technical explanations, code generation, and Q&A. Body: { \"message\": \"What is a Java record?\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"What is the time complexity of binary search and why?\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"response\": \"Binary search has O(log n) time complexity because...\"}")))
    )
    @PostMapping("/precise")
    public Map<String, String> chatPrecise(@RequestBody Map<String, String> request) {
        return Map.of("response", service.chatPrecise(request.get("message")));
    }

    @Operation(
            summary = "Fast mode (Claude Haiku model)",
            description = "Switches to claude-haiku-4-5-20251001 via AnthropicChatOptions for low-latency, " +
                          "cost-efficient responses. Ideal for simple queries and high-throughput use cases.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Give me 5 tips for writing clean Java code.\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"response\": \"1. Use meaningful variable names...\"}")))
    )
    @PostMapping("/fast")
    public Map<String, String> chatFast(@RequestBody Map<String, String> request) {
        return Map.of("response", service.chatFast(request.get("message")));
    }

    @Operation(
            summary = "Chat with a dynamic AI persona (SystemPromptTemplate)",
            description = "Uses Spring AI's SystemPromptTemplate to build a parameterized system prompt at runtime. " +
                          "The {role}, {language}, and {style} placeholders are filled from the request body before the model call. " +
                          "Body: { \"message\": \"...\", \"role\": \"...\", \"language\": \"...\", \"style\": \"...\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Explain microservices architecture.\", \"role\": \"senior software architect\", \"language\": \"English\", \"style\": \"concise and technical\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"response\": \"Microservices decompose a monolith into independently deployable services...\"}")))
    )
    @PostMapping("/with-persona")
    public Map<String, String> chatWithPersona(@RequestBody Map<String, String> request) {
        return Map.of("response", service.chatWithPersona(
                request.get("message"),
                request.getOrDefault("role",     "helpful assistant"),
                request.getOrDefault("language", "English"),
                request.getOrDefault("style",    "clear and friendly")));
    }
}
