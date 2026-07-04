package com.example.agent.controller;

import com.example.agent.model.CodeReviewResult;
import com.example.agent.model.EntityExtractionResult;
import com.example.agent.model.SentimentResult;
import com.example.agent.model.SummaryResult;
import com.example.agent.service.StructuredOutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/structured")
@Tag(name = "Structured Output",
        description = "Returns typed Java records via Spring AI's chatClient.call().entity(Class). " +
                      "Spring AI auto-generates a JSON schema, appends output instructions to the prompt, " +
                      "and deserializes the model's response into the target type.")
public class StructuredOutputController {

    private final StructuredOutputService service;

    public StructuredOutputController(StructuredOutputService service) {
        this.service = service;
    }

    @Operation(
            summary = "Sentiment analysis",
            description = "Returns sentiment (POSITIVE/NEGATIVE/NEUTRAL/MIXED), a confidence score 0–1, reasoning, and highlight phrases.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"text\": \"I absolutely loved the new product launch. It exceeded my expectations!\"}"))),
            responses = @ApiResponse(responseCode = "200", description = "SentimentResult",
                    content = @Content(schema = @Schema(implementation = SentimentResult.class)))
    )
    @PostMapping("/sentiment")
    public SentimentResult analyzeSentiment(@RequestBody Map<String, String> request) {
        return service.analyzeSentiment(request.get("text"));
    }

    @Operation(
            summary = "Named entity extraction",
            description = "Extracts and categorizes people, places, organizations, dates, and keywords from free text.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"text\": \"Elon Musk visited Paris last Tuesday to meet European Union officials.\"}"))),
            responses = @ApiResponse(responseCode = "200", description = "EntityExtractionResult",
                    content = @Content(schema = @Schema(implementation = EntityExtractionResult.class)))
    )
    @PostMapping("/extract-entities")
    public EntityExtractionResult extractEntities(@RequestBody Map<String, String> request) {
        return service.extractEntities(request.get("text"));
    }

    @Operation(
            summary = "Text summarization",
            description = "Returns a structured summary with title, brief summary, key points, sentiment, and estimated reading time.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"text\": \"Spring AI is a framework that simplifies AI integration into Spring Boot applications. It supports major LLM providers like Anthropic, OpenAI, and more. Features include tool calling, structured output, RAG, and conversation memory.\"}"))),
            responses = @ApiResponse(responseCode = "200", description = "SummaryResult",
                    content = @Content(schema = @Schema(implementation = SummaryResult.class)))
    )
    @PostMapping("/summarize")
    public SummaryResult summarize(@RequestBody Map<String, String> request) {
        return service.summarize(request.get("text"));
    }

    @Operation(
            summary = "AI code review",
            description = "Reviews code and returns a structured report: quality score (1–10), issues, suggestions, and improved code. " +
                          "Body: { \"code\": \"...\", \"language\": \"java\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"language\": \"java\", \"code\": \"public int sum(int[] arr) { int s=0; for(int i=0;i<arr.length;i++) s+=arr[i]; return s; }\"}"))),
            responses = @ApiResponse(responseCode = "200", description = "CodeReviewResult",
                    content = @Content(schema = @Schema(implementation = CodeReviewResult.class)))
    )
    @PostMapping("/code-review")
    public CodeReviewResult reviewCode(@RequestBody Map<String, String> request) {
        return service.reviewCode(
                request.get("code"),
                request.getOrDefault("language", "java"));
    }
}
