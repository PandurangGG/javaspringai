package com.example.agent.controller;

import com.example.agent.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/stream")
@Tag(name = "Streaming", description = "Server-Sent Events (SSE) streaming — responses arrive token-by-token via Spring AI's stream() API")
public class StreamController {

    private final StreamService streamService;

    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @Operation(
            summary = "Stream a chat response (SSE)",
            description = "Sends a message to the model and streams each token as a Server-Sent Event. " +
                          "Connect with EventSource in a browser or `curl -N` in the terminal. " +
                          "Body: { \"message\": \"Tell me about Spring AI\" }",
            responses = @ApiResponse(responseCode = "200", description = "Token stream",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                            schema = @Schema(type = "string", example = "Spring AI is a framework...")))
    )
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return Flux.just("Error: 'message' field is required.");
        }
        return streamService.stream(message);
    }

    @Operation(
            summary = "Stream with a custom system prompt (SSE)",
            description = "Streams a response using a caller-supplied system prompt. " +
                          "Body: { \"system\": \"You are a pirate.\", \"message\": \"Tell me about the sea.\" }",
            responses = @ApiResponse(responseCode = "200", description = "Token stream",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                            schema = @Schema(type = "string")))
    )
    @PostMapping(value = "/chat/custom", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamWithSystem(@RequestBody Map<String, String> request) {
        String system = request.getOrDefault("system", "You are a helpful assistant.");
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return Flux.just("Error: 'message' field is required.");
        }
        return streamService.streamWithSystemPrompt(system, message);
    }
}
