package com.example.agent.controller;

import com.example.agent.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/conversation")
@Tag(name = "Multi-Session Conversations",
        description = "Isolated multi-turn conversations using named session IDs. " +
                      "Each session has its own MessageWindowChatMemory backed by a dedicated " +
                      "InMemoryChatMemoryRepository — sessions never share context.")
public class ConversationController {

    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @Operation(
            summary = "Chat within a named session",
            description = "Sends a message in the context of the given sessionId. " +
                          "The session is created automatically on first use. " +
                          "Subsequent messages in the same session have full memory of prior turns. " +
                          "Different session IDs are completely isolated from each other. " +
                          "Body: { \"message\": \"My name is Pandurang.\" }",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"My favourite programming language is Java.\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"sessionId\": \"alice\", \"response\": \"Got it! I'll remember that you love Java.\"}")))
    )
    @PostMapping("/{sessionId}/chat")
    public Map<String, String> chat(
            @Parameter(description = "Unique session identifier", example = "alice")
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String response = service.chat(sessionId, request.get("message"));
        return Map.of("sessionId", sessionId, "response", response);
    }

    @Operation(
            summary = "List all active sessions",
            description = "Returns the set of session IDs that currently exist in memory.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"sessions\": [\"alice\", \"bob\"], \"count\": 2}")))
    )
    @GetMapping("/sessions")
    public Map<String, Object> listSessions() {
        return Map.of(
                "sessions", service.getActiveSessions(),
                "count",    service.getSessionCount());
    }

    @Operation(
            summary = "Clear a session",
            description = "Removes the session and its entire conversation history from memory. " +
                          "The next message sent under this sessionId starts a fresh conversation.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session cleared",
                            content = @Content(schema = @Schema(example = "{\"sessionId\": \"alice\", \"cleared\": true}"))),
                    @ApiResponse(responseCode = "200", description = "Session not found",
                            content = @Content(schema = @Schema(example = "{\"sessionId\": \"unknown\", \"cleared\": false}")))
            }
    )
    @DeleteMapping("/{sessionId}")
    public Map<String, Object> clearSession(
            @Parameter(description = "Session ID to remove", example = "alice")
            @PathVariable String sessionId) {

        boolean cleared = service.clearSession(sessionId);
        return Map.of("sessionId", sessionId, "cleared", cleared);
    }
}
