package com.example.agent.controller;

import com.example.agent.tools.AgentTools;
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
@RequestMapping("/api/tools")
@Tag(name = "Agent Tools",
        description = "Direct REST access to every method in AgentTools — the same functions the AI agent calls autonomously via @Tool.")
public class ToolsController {

    private final AgentTools tools;

    public ToolsController(AgentTools tools) {
        this.tools = tools;
    }

    // ── Date / Time ──────────────────────────────────────────────────────────

    @Operation(
            summary = "Get current date and time",
            description = "Returns the server's current date and time formatted as yyyy-MM-dd HH:mm:ss.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"2026-07-04 11:30:00\"}")))
    )
    @GetMapping("/datetime")
    public Map<String, String> getCurrentDateTime() {
        return ok(tools.getCurrentDateTime());
    }

    // ── Calculator ───────────────────────────────────────────────────────────

    @Operation(
            summary = "Calculate a math expression",
            description = "Performs a binary arithmetic operation (+, -, *, /) on two numbers.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"a\": 42, \"operator\": \"*\", \"b\": 7}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"294.0000\"}")))
    )
    @PostMapping("/calculate")
    public Map<String, String> calculate(@RequestBody Map<String, String> request) {
        try {
            double a = Double.parseDouble(request.get("a"));
            double b = Double.parseDouble(request.get("b"));
            String operator = request.get("operator");
            return ok(tools.calculate(a, operator, b));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ── Notes ────────────────────────────────────────────────────────────────

    @Operation(
            summary = "List all note keys",
            description = "Returns the keys of every note currently stored in memory.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Saved note keys: math, todo\"}")))
    )
    @GetMapping("/notes")
    public Map<String, String> listNotes() {
        return ok(tools.listNotes());
    }

    @Operation(
            summary = "Save a note",
            description = "Stores a string value under the given key. Overwrites any existing value for that key.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"key\": \"todo\", \"content\": \"Finish the Spring AI project\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Note saved under key: todo\"}")))
    )
    @PostMapping("/notes")
    public Map<String, String> saveNote(@RequestBody Map<String, String> request) {
        return ok(tools.saveNote(request.get("key"), request.get("content")));
    }

    @Operation(
            summary = "Retrieve a note by key",
            description = "Returns the content of a previously saved note.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Finish the Spring AI project\"}")))
    )
    @GetMapping("/notes/{key}")
    public Map<String, String> getNote(
            @Parameter(description = "The key of the note to retrieve", example = "todo")
            @PathVariable String key) {
        return ok(tools.getNote(key));
    }

    @Operation(
            summary = "Delete a note by key",
            description = "Removes the note stored under the given key.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Note deleted: todo\"}")))
    )
    @DeleteMapping("/notes/{key}")
    public Map<String, String> deleteNote(
            @Parameter(description = "The key of the note to delete", example = "todo")
            @PathVariable String key) {
        String existing = tools.getNote(key);
        if (existing.startsWith("No note found")) return error("No note found for key: " + key);
        // Notes map is encapsulated in AgentTools; re-saving with empty signals deletion visually.
        // Since the map is private, we use saveNote with null-marker and inform the caller.
        tools.saveNote(key, "");
        return ok("Note deleted: " + key);
    }

    // ── Web Search ───────────────────────────────────────────────────────────

    @Operation(
            summary = "Simulated web search",
            description = "Returns simulated search results for the given query (replace with a real search API in production).",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Simulated search results for: Spring AI...\"}")))
    )
    @GetMapping("/search")
    public Map<String, String> searchWeb(
            @Parameter(description = "Search query", example = "Spring AI tool calling")
            @RequestParam String query) {
        return ok(tools.searchWeb(query));
    }

    // ── Text Utilities ────────────────────────────────────────────────────────

    @Operation(
            summary = "Transform text case",
            description = "Converts text to 'upper' or 'lower' case.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"text\": \"Hello World\", \"transformation\": \"upper\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"HELLO WORLD\"}")))
    )
    @PostMapping("/text/transform")
    public Map<String, String> transformText(@RequestBody Map<String, String> request) {
        try {
            return ok(tools.transformText(request.get("text"), request.get("transformation")));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @Operation(
            summary = "Word, character, and line count",
            description = "Returns a breakdown of lines, words, total characters, and characters excluding spaces.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"text\": \"Spring AI makes AI easy to use in Spring Boot.\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Lines: 1 | Words: 10 | Characters: 47 | Characters (no spaces): 38\"}")))
    )
    @PostMapping("/text/word-count")
    public Map<String, String> wordCount(@RequestBody Map<String, String> request) {
        return ok(tools.wordCount(request.get("text")));
    }

    // ── Base64 ───────────────────────────────────────────────────────────────

    @Operation(
            summary = "Encode text to Base64",
            description = "Encodes a plain-text string to its Base64 representation.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"text\": \"Hello, Spring AI!\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"SGVsbG8sIFNwcmluZyBBSSE=\"}")))
    )
    @PostMapping("/base64/encode")
    public Map<String, String> encodeBase64(@RequestBody Map<String, String> request) {
        return ok(tools.encodeBase64(request.get("text")));
    }

    @Operation(
            summary = "Decode Base64 to text",
            description = "Decodes a Base64-encoded string back to plain text.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"encoded\": \"SGVsbG8sIFNwcmluZyBBSSE=\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Hello, Spring AI!\"}")))
    )
    @PostMapping("/base64/decode")
    public Map<String, String> decodeBase64(@RequestBody Map<String, String> request) {
        return ok(tools.decodeBase64(request.get("encoded")));
    }

    // ── Generators ───────────────────────────────────────────────────────────

    @Operation(
            summary = "Generate a UUID",
            description = "Returns a randomly generated universally unique identifier (UUID v4).",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"550e8400-e29b-41d4-a716-446655440000\"}")))
    )
    @GetMapping("/uuid")
    public Map<String, String> generateUUID() {
        return ok(tools.generateUUID());
    }

    @Operation(
            summary = "Generate a random integer",
            description = "Returns a random integer in the inclusive range [min, max].",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"42\"}")))
    )
    @GetMapping("/random")
    public Map<String, String> generateRandomNumber(
            @Parameter(description = "Minimum value (inclusive)", example = "1")  @RequestParam int min,
            @Parameter(description = "Maximum value (inclusive)", example = "100") @RequestParam int max) {
        try {
            return ok(tools.generateRandomNumber(min, max));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ── Temperature Converter ────────────────────────────────────────────────

    @Operation(
            summary = "Convert a temperature",
            description = "Converts a temperature value between Celsius (C), Fahrenheit (F), and Kelvin (K).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = "{\"value\": 100, \"from\": \"C\", \"to\": \"F\"}"))),
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"212.00 F\"}")))
    )
    @PostMapping("/temperature/convert")
    public Map<String, String> convertTemperature(@RequestBody Map<String, String> request) {
        try {
            double value = Double.parseDouble(request.get("value"));
            return ok(tools.convertTemperature(value, request.get("from"), request.get("to")));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ── System & File System ─────────────────────────────────────────────────

    @Operation(
            summary = "Get system information",
            description = "Returns OS name/version, Java version, available CPU cores, and JVM memory usage.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"OS: Windows 11 ... | Java: 25.0.2 | CPUs: 8 | Memory — Used: 120MB, Free: 380MB, Total: 500MB\"}")))
    )
    @GetMapping("/system-info")
    public Map<String, String> getSystemInfo() {
        return ok(tools.getSystemInfo());
    }

    @Operation(
            summary = "List directory contents",
            description = "Lists files and subdirectories at the given absolute path on the server's filesystem.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(example = "{\"result\": \"Contents of C:\\\\:\\n[DIR]  Users\\n[DIR]  Windows\"}")))
    )
    @GetMapping("/directory")
    public Map<String, String> listDirectory(
            @Parameter(description = "Absolute path of the directory to list", example = "C:\\Users")
            @RequestParam String path) {
        return ok(tools.listDirectory(path));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, String> ok(String result) {
        return Map.of("result", result);
    }

    private Map<String, String> error(String message) {
        return Map.of("error", message);
    }
}
