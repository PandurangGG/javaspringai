package com.example.agent.controller;

import com.example.agent.service.OutputConverterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/output")
@Tag(name = "Output Converters",
        description = "Demonstrates Spring AI's three output converters that are NOT used elsewhere in the project: " +
                      "MapOutputConverter (unstructured key-value), " +
                      "ListOutputConverter (comma-separated list), and " +
                      "explicit BeanOutputConverter (typed bean with raw JSON inspection). " +
                      "All three work by appending format instructions to the prompt via getFormat(), " +
                      "then parsing the model's raw text response via convert().")
public class OutputConverterController {

    private final OutputConverterService service;

    public OutputConverterController(OutputConverterService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get AI response as Map<String, Object> (MapOutputConverter)",
            description = "Spring AI's MapOutputConverter appends JSON format instructions to the prompt. " +
                          "The model returns a JSON object which is parsed into a Map<String, Object>. " +
                          "Ideal for dynamic, schema-free structured output. " +
                          "Body: { \"prompt\": \"Return facts about Java as a JSON map.\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"prompt\": \"Return a JSON map with facts about the Spring Boot framework: year created, creator, latest version, primary use case.\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"year_created\": \"2014\", \"creator\": \"Pivotal\", \"latest_version\": \"3.3.4\", \"primary_use_case\": \"Rapid Java application development\"}")))
    )
    @PostMapping("/map")
    public Map<String, Object> toMap(@RequestBody Map<String, String> request) {
        return service.toMap(request.get("prompt"));
    }

    @Operation(
            summary = "Get AI response as List<String> (ListOutputConverter)",
            description = "Spring AI's ListOutputConverter appends comma-separated-value format instructions. " +
                          "The model returns a CSV string which is parsed into a List<String>. " +
                          "Ideal for enumerations, bullet-point lists, and option sets. " +
                          "Body: { \"prompt\": \"List the top 5 Java frameworks.\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"prompt\": \"List the top 7 Spring AI features in order of importance.\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "[\"Tool Calling\", \"Structured Output\", \"Streaming\", \"RAG\", \"Multimodal\", \"Prompt Templates\", \"Conversation Memory\"]")))
    )
    @PostMapping("/list")
    public List<String> toList(@RequestBody Map<String, String> request) {
        return service.toList(request.get("prompt"));
    }

    @Operation(
            summary = "Explicit BeanOutputConverter with raw JSON inspection",
            description = "Uses BeanOutputConverter<SentimentResult> directly (unlike /api/structured/sentiment " +
                          "which uses chatClient.entity() implicitly). Returns BOTH the raw JSON string from the model " +
                          "AND the parsed Java record, so you can inspect the conversion. " +
                          "Body: { \"text\": \"I am really excited about Spring AI!\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"text\": \"I am really excited about building AI apps with Spring AI!\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"raw\": \"{\\\"sentiment\\\": \\\"POSITIVE\\\", \\\"score\\\": 0.95, ...}\", \"parsed\": {\"sentiment\": \"POSITIVE\", \"score\": 0.95, \"reasoning\": \"...\", \"highlights\": [...]}}")))
    )
    @PostMapping("/bean")
    public Map<String, Object> toBeanWithRaw(@RequestBody Map<String, String> request) {
        return service.toBeanWithRaw(request.get("text"));
    }
}
