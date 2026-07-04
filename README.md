# Spring AI Agent

An agentic AI project built with [Spring AI](https://spring.io/projects/spring-ai) and Spring Boot 3.

## What it does

The agent receives natural-language requests and can autonomously:

- Call **tool functions** (calculator, note storage, datetime, web search simulation)
- Chain multiple tool calls together to complete multi-step tasks
- Maintain **conversation memory** across turns (in-memory sliding window)

## Project Structure

```
src/main/java/com/example/agent/
├── SpringAiAgentApplication.java   # Spring Boot entry point
├── tools/
│   └── AgentTools.java             # @Tool-annotated methods the agent can call
├── service/
│   └── AgentService.java           # ChatClient with tools + memory configured
└── controller/
    └── AgentController.java        # REST API endpoints
```

## Prerequisites

- Java 21+
- Maven 3.9+
- Anthropic API key (get one at https://console.anthropic.com)

## Quick Start

### 1. Set your API key

```bash
export SPRING_AI_ANTHROPIC_API_KEY=sk-ant-...
```

Or edit `src/main/resources/application.properties` directly (do **not** commit the key).

### 2. Run

```bash
./mvnw spring-boot:run
```

### 3. Chat with the agent

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What time is it? Then calculate 42 * 7 and save the result as a note called answer."}'
```

## Available Tools

| Tool | Description |
|------|-------------|
| `getCurrentDateTime` | Returns current date/time |
| `calculate` | Evaluates a/op/b arithmetic |
| `saveNote` / `getNote` / `listNotes` | In-memory key-value note storage |
| `searchWeb` | Simulated web search (replace with real API) |
| `transformText` | upper/lowercase conversion |

## Switching to OpenAI

1. Comment out the Anthropic dependency and uncomment the OpenAI one in `pom.xml`
2. In `application.properties`, comment out `spring.ai.anthropic.*` and uncomment `spring.ai.openai.*`
3. In `AgentService.java`, replace `AnthropicChatModel` with `OpenAiChatModel`

## Adding New Tools

Add a method annotated with `@Tool` and `@ToolParam` to `AgentTools.java`:

```java
@Tool(description = "Fetch weather for a city")
public String getWeather(@ToolParam(description = "City name") String city) {
    // call a weather API here
    return "Sunny, 22°C";
}
```

Spring AI automatically registers it — no additional wiring needed.
