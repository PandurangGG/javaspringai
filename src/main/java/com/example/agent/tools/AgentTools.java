package com.example.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Tool definitions available to the AI agent.
 * Each @Tool method is automatically discovered and made available for the model to call.
 */
@Component
public class AgentTools {

    // Simple in-memory "database" for demo purposes
    private final Map<String, String> notes = new HashMap<>();

    @Tool(description = "Get the current date and time")
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Tool(description = "Calculate the result of a simple math expression. Supports +, -, *, /")
    public String calculate(
            @ToolParam(description = "The first number") double a,
            @ToolParam(description = "The operator: +, -, *, /") String operator,
            @ToolParam(description = "The second number") double b) {
        double result = switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new IllegalArgumentException("Division by zero");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
        return String.format("%.4f", result);
    }

    @Tool(description = "Save a note with a given key for later retrieval")
    public String saveNote(
            @ToolParam(description = "The key to store the note under") String key,
            @ToolParam(description = "The note content to save") String content) {
        notes.put(key, content);
        return "Note saved under key: " + key;
    }

    @Tool(description = "Retrieve a previously saved note by key")
    public String getNote(
            @ToolParam(description = "The key of the note to retrieve") String key) {
        String value = notes.get(key);
        return value != null ? value : "No note found for key: " + key;
    }

    @Tool(description = "List all saved note keys")
    public String listNotes() {
        if (notes.isEmpty()) return "No notes saved yet.";
        return "Saved note keys: " + String.join(", ", notes.keySet());
    }

    @Tool(description = "Search for information about a topic (simulated web search)")
    public String searchWeb(
            @ToolParam(description = "The search query") String query) {
        // Simulated search — replace with real API call (e.g. SerpAPI, Brave Search) in production
        return """
                Simulated search results for: "%s"

                1. Spring AI is a framework that brings AI capabilities to Spring Boot applications.
                   It supports OpenAI, Anthropic, Azure OpenAI, Ollama, and more.
                2. Agentic AI refers to AI systems that can autonomously plan and execute multi-step tasks
                   using tools and function calling.
                3. Spring AI provides @Tool annotations, ChatClient, and Advisor patterns
                   to build production-grade AI agents.
                """.formatted(query);
    }

    @Tool(description = "Convert text to uppercase or lowercase")
    public String transformText(
            @ToolParam(description = "The text to transform") String text,
            @ToolParam(description = "The transformation: 'upper' or 'lower'") String transformation) {
        return switch (transformation.toLowerCase()) {
            case "upper" -> text.toUpperCase();
            case "lower" -> text.toLowerCase();
            default -> throw new IllegalArgumentException("Unknown transformation: " + transformation);
        };
    }

    @Tool(description = "Generate a random UUID (universally unique identifier)")
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Tool(description = "Generate a random integer between min and max (inclusive)")
    public String generateRandomNumber(
            @ToolParam(description = "Minimum value (inclusive)") int min,
            @ToolParam(description = "Maximum value (inclusive)") int max) {
        if (min > max) throw new IllegalArgumentException("min must be <= max");
        int result = new Random().nextInt((max - min) + 1) + min;
        return String.valueOf(result);
    }

    @Tool(description = "Convert a temperature between Celsius, Fahrenheit, and Kelvin")
    public String convertTemperature(
            @ToolParam(description = "The temperature value to convert") double value,
            @ToolParam(description = "The source unit: 'C', 'F', or 'K'") String from,
            @ToolParam(description = "The target unit: 'C', 'F', or 'K'") String to) {
        double celsius = switch (from.toUpperCase()) {
            case "C" -> value;
            case "F" -> (value - 32) * 5 / 9;
            case "K" -> value - 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + from);
        };
        double result = switch (to.toUpperCase()) {
            case "C" -> celsius;
            case "F" -> (celsius * 9 / 5) + 32;
            case "K" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + to);
        };
        return String.format("%.2f %s", result, to.toUpperCase());
    }

    @Tool(description = "Count the number of words, characters, and lines in a given text")
    public String wordCount(
            @ToolParam(description = "The text to analyze") String text) {
        if (text == null || text.isEmpty()) return "Empty text.";
        int lines = text.split("\n", -1).length;
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int chars = text.length();
        int charsNoSpaces = text.replaceAll("\\s", "").length();
        return String.format("Lines: %d | Words: %d | Characters: %d | Characters (no spaces): %d",
                lines, words, chars, charsNoSpaces);
    }

    @Tool(description = "Encode a plain text string to Base64")
    public String encodeBase64(
            @ToolParam(description = "The plain text to encode") String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    @Tool(description = "Decode a Base64-encoded string back to plain text")
    public String decodeBase64(
            @ToolParam(description = "The Base64-encoded string to decode") String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded));
        } catch (IllegalArgumentException e) {
            return "Invalid Base64 input: " + e.getMessage();
        }
    }

    @Tool(description = "Get system information: OS name, Java version, and available memory")
    public String getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemoryMB = runtime.totalMemory() / (1024 * 1024);
        long freeMemoryMB = runtime.freeMemory() / (1024 * 1024);
        long usedMemoryMB = totalMemoryMB - freeMemoryMB;
        return String.format(
                "OS: %s %s | Java: %s | CPUs: %d | Memory — Used: %dMB, Free: %dMB, Total: %dMB",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("java.version"),
                runtime.availableProcessors(),
                usedMemoryMB, freeMemoryMB, totalMemoryMB);
    }

    @Tool(description = "List files and folders in a given directory path")
    public String listDirectory(
            @ToolParam(description = "The absolute directory path to list") String path) {
        File dir = new File(path);
        if (!dir.exists()) return "Path does not exist: " + path;
        if (!dir.isDirectory()) return "Path is not a directory: " + path;
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return "Directory is empty.";
        StringBuilder sb = new StringBuilder("Contents of " + path + ":\n");
        for (File f : files) {
            sb.append(f.isDirectory() ? "[DIR]  " : "[FILE] ")
              .append(f.getName()).append("\n");
        }
        return sb.toString().trim();
    }
}
