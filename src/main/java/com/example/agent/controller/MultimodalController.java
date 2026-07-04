package com.example.agent.controller;

import com.example.agent.service.MultimodalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Map;

@RestController
@RequestMapping("/api/multimodal")
@Tag(name = "Multimodal (Vision)",
        description = "Image analysis using Claude's vision capability via Spring AI's media() API. " +
                      "Pass a public image URL; Claude analyzes the image alongside the text prompt.")
public class MultimodalController {

    private final MultimodalService service;

    public MultimodalController(MultimodalService service) {
        this.service = service;
    }

    @Operation(
            summary = "Analyze an image with a custom question",
            description = "Sends an image URL and a question to Claude. The model reasons over the image to answer. " +
                          "Body: { \"imageUrl\": \"https://...\", \"question\": \"What objects are in this image?\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"imageUrl\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/280px-PNG_transparency_demonstration_1.png\", \"question\": \"Describe the main elements in this image.\"}")))
    )
    @PostMapping("/analyze")
    public Map<String, String> analyzeImage(@RequestBody Map<String, String> request) throws MalformedURLException {
        return Map.of("analysis", service.analyzeImage(
                request.get("imageUrl"),
                request.getOrDefault("question", "Describe this image in detail.")));
    }

    @Operation(
            summary = "Generate a detailed description of an image",
            description = "Produces a comprehensive description covering subject, setting, colors, mood, and notable elements. " +
                          "Body: { \"imageUrl\": \"https://...\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"imageUrl\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/280px-PNG_transparency_demonstration_1.png\"}")))
    )
    @PostMapping("/describe")
    public Map<String, String> describeImage(@RequestBody Map<String, String> request) throws MalformedURLException {
        return Map.of("description", service.describeImage(request.get("imageUrl")));
    }

    @Operation(
            summary = "Extract text (OCR) from an image",
            description = "Extracts all visible text from an image, preserving layout where possible. " +
                          "Body: { \"imageUrl\": \"https://...\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"imageUrl\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/280px-PNG_transparency_demonstration_1.png\"}")))
    )
    @PostMapping("/extract-text")
    public Map<String, String> extractText(@RequestBody Map<String, String> request) throws MalformedURLException {
        return Map.of("extractedText", service.extractTextFromImage(request.get("imageUrl")));
    }

    @Operation(
            summary = "Compare two images",
            description = "Sends two image URLs to Claude and returns a structured comparison focused on a given aspect. " +
                          "Body: { \"imageUrl1\": \"https://...\", \"imageUrl2\": \"https://...\", \"aspect\": \"color palette and composition\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"imageUrl1\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/280px-PNG_transparency_demonstration_1.png\", \"imageUrl2\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/280px-PNG_transparency_demonstration_1.png\", \"aspect\": \"visual composition\"}")))
    )
    @PostMapping("/compare")
    public Map<String, String> compareImages(@RequestBody Map<String, String> request) throws MalformedURLException {
        return Map.of("comparison", service.compareImages(
                request.get("imageUrl1"),
                request.get("imageUrl2"),
                request.getOrDefault("aspect", "overall visual composition and content")));
    }
}
