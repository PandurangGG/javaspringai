package com.example.agent.controller;

import com.example.agent.service.PromptTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/template")
@Tag(name = "Prompt Templates",
        description = "Parameterized prompt templates using Spring AI's PromptTemplate.render() (approach 1) " +
                      "and ChatClient .user(u -> u.text(...).param(...)) (approach 2).")
public class PromptTemplateController {

    private final PromptTemplateService service;

    public PromptTemplateController(PromptTemplateService service) {
        this.service = service;
    }

    @Operation(
            summary = "Render a custom prompt template",
            description = "Fills {variable} placeholders in your template using Spring AI's PromptTemplate.render(), " +
                          "then sends the rendered prompt to the model. " +
                          "Body: { \"template\": \"Summarize {topic} in {words} words.\", \"variables\": { \"topic\": \"quantum computing\", \"words\": \"50\" } }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"template\": \"List the top {count} benefits of {topic}.\", \"variables\": {\"count\": \"5\", \"topic\": \"Spring Boot\"}}")))
    )
    @PostMapping("/render")
    public Map<String, String> renderTemplate(@RequestBody Map<String, Object> request) {
        String template = (String) request.get("template");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.getOrDefault("variables", Map.of());
        return Map.of("result", service.renderTemplate(template, variables));
    }

    @Operation(
            summary = "Translate text",
            description = "Translates text into any language using a parameterized ChatClient prompt. " +
                          "Body: { \"text\": \"Hello, how are you?\", \"language\": \"French\", \"tone\": \"formal\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"text\": \"The meeting has been rescheduled to next Monday.\", \"language\": \"Spanish\", \"tone\": \"professional\"}")))
    )
    @PostMapping("/translate")
    public Map<String, String> translate(@RequestBody Map<String, String> request) {
        return Map.of("translation", service.translateText(
                request.get("text"),
                request.getOrDefault("language", "English"),
                request.getOrDefault("tone", "neutral")));
    }

    @Operation(
            summary = "Generate a professional email",
            description = "Composes a complete professional email from structured inputs using a parameterized prompt. " +
                          "Body: { \"purpose\": \"...\", \"recipientRole\": \"...\", \"senderName\": \"...\", \"keyPoints\": \"...\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"purpose\": \"Request project deadline extension\", \"recipientRole\": \"Project Manager\", \"senderName\": \"Pandurang\", \"keyPoints\": \"team member sick, need 3 extra days, will share updated plan\"}")))
    )
    @PostMapping("/email")
    public Map<String, String> generateEmail(@RequestBody Map<String, String> request) {
        return Map.of("email", service.generateEmail(
                request.get("purpose"),
                request.getOrDefault("recipientRole", "Colleague"),
                request.getOrDefault("senderName", "The Sender"),
                request.getOrDefault("keyPoints", "")));
    }

    @Operation(
            summary = "Explain a concept at a given level",
            description = "Explains any concept tailored to audience level and domain using a structured prompt template. " +
                          "Body: { \"concept\": \"recursion\", \"audienceLevel\": \"beginner\", \"domain\": \"programming\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"concept\": \"vector embeddings\", \"audienceLevel\": \"intermediate\", \"domain\": \"machine learning\"}")))
    )
    @PostMapping("/explain")
    public Map<String, String> explainConcept(@RequestBody Map<String, String> request) {
        return Map.of("explanation", service.explainConcept(
                request.get("concept"),
                request.getOrDefault("audienceLevel", "intermediate"),
                request.getOrDefault("domain", "general")));
    }

    @Operation(
            summary = "Generate unit tests for code",
            description = "Generates comprehensive unit tests for the provided code snippet using the specified test framework. " +
                          "Body: { \"code\": \"...\", \"language\": \"java\", \"testFramework\": \"JUnit 5\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"language\": \"java\", \"testFramework\": \"JUnit 5 + Mockito\", \"code\": \"public double divide(double a, double b) { if (b == 0) throw new ArithmeticException(); return a / b; }\"}")))
    )
    @PostMapping("/generate-tests")
    public Map<String, String> generateTests(@RequestBody Map<String, String> request) {
        return Map.of("tests", service.generateUnitTests(
                request.get("code"),
                request.getOrDefault("language", "java"),
                request.getOrDefault("testFramework", "JUnit 5")));
    }
}
