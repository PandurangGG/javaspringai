package com.example.agent.controller;

import com.example.agent.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
@Tag(name = "Agent", description = "Endpoints for interacting with the Spring AI agentic assistant")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Operation(
            summary = "Send a message to the AI agent",
            description = "Submits a user message to the agentic AI assistant. The agent may invoke one or more built-in tools " +
                    "(calculator, notes, base64, temperature converter, etc.) autonomously before returning the final response.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"What is 42 * 7? Save the result as a note called 'math'.\"}"),
                            examples = @ExampleObject(
                                    name = "Tool-calling example",
                                    value = "{\"message\": \"What is 42 * 7? Save the result as a note called 'math'.\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Agent response",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"response\": \"42 × 7 = 294. I have saved 294 as the note 'math'.\"}"))
                    ),
                    @ApiResponse(responseCode = "200", description = "Validation error (missing message field)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"Request body must contain a 'message' field.\"}"))
                    )
            }
    )
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        if (userMessage == null || userMessage.isBlank()) {
            return Map.of("error", "Request body must contain a 'message' field.");
        }
        String response = agentService.chat(userMessage);
        return Map.of("response", response);
    }

    @Operation(
            summary = "Health check",
            description = "Returns a simple status response confirming the agent service is running.",
            responses = @ApiResponse(responseCode = "200", description = "Service is up",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"status\": \"ok\", \"agent\": \"spring-ai-agent\"}"))
            )
    )
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "agent", "spring-ai-agent");
    }
}
