# 🤖 Spring AI Agent

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20AI-1.0.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-21+-007396?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Claude-Anthropic-191919?style=for-the-badge&logo=anthropic&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
</p>

<p align="center">
  A production-ready agentic AI application built with <strong>Spring AI</strong> and <strong>Spring Boot 3</strong>,
  powered by <strong>Claude (Anthropic)</strong>. Features tool-calling, streaming, structured output,
  prompt templates, vision (multimodal), and direct utility APIs — all documented with Swagger UI.
</p>

---

## 📋 Table of Contents

- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [Swagger UI](#-swagger-ui)
- [API Reference](#-api-reference)
  - [1. Agent — AI Chat with Tool Calling](#1--agent--ai-chat-with-tool-calling)
  - [2. Streaming — Server-Sent Events](#2--streaming--server-sent-events)
  - [3. Structured Output — Typed JSON Responses](#3--structured-output--typed-json-responses)
  - [4. Prompt Templates — Parameterized Prompts](#4--prompt-templates--parameterized-prompts)
  - [5. Multimodal — Vision & Image Analysis](#5--multimodal--vision--image-analysis)
  - [6. Agent Tools — Direct Utility Endpoints](#6--agent-tools--direct-utility-endpoints)
- [Built-in Tools](#-built-in-tools)
- [Project Structure](#-project-structure)
- [Tech Stack](#-tech-stack)

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        HTTP Clients / Swagger UI                    │
└──────────────┬──────────────────────────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────────────────────────┐
│                          REST Controllers                            │
│                                                                      │
│  AgentController   StreamController   StructuredOutputController    │
│  PromptTemplateController   MultimodalController   ToolsController  │
└──────┬──────────────┬────────────────────────────────┬──────────────┘
       │              │                                │
┌──────▼──────┐ ┌─────▼──────┐                ┌───────▼───────┐
│AgentService │ │StreamService│                │  AgentTools   │
│             │ │             │                │  (15 @Tool    │
│ ChatClient  │ │ ChatClient  │                │   methods)    │
│ + Tools     │ │ stream()    │                └───────────────┘
│ + Memory    │ └─────────────┘
└──────┬──────┘
       │
┌──────▼──────────────────────────────────────┐
│         Anthropic API  (Claude claude-opus-4-8)        │
└─────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Anthropic API key → [console.anthropic.com](https://console.anthropic.com)

### 1. Clone & configure

```bash
git clone https://github.com/PandurangGG/javaspringai.git
cd javaspringai
export SPRING_AI_ANTHROPIC_API_KEY=sk-ant-your-key-here
```

### 2. Run

```bash
# Windows
set SPRING_AI_ANTHROPIC_API_KEY=sk-ant-your-key-here
mvn spring-boot:run

# macOS / Linux
SPRING_AI_ANTHROPIC_API_KEY=sk-ant-... mvn spring-boot:run
```

### 3. Verify

```bash
curl http://localhost:8080/api/agent/ping
# {"status":"ok","agent":"spring-ai-agent"}
```

---

## 📖 Swagger UI

Open your browser and navigate to:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON spec:
```
http://localhost:8080/v3/api-docs
```

---

## 📡 API Reference

---

### 1. 🧠 Agent — AI Chat with Tool Calling

> Base URL: `/api/agent`
>
> The agent uses Claude with **tool-calling** and a **sliding-window conversation memory (20 messages)**. It autonomously decides which tools to invoke, in what order, and how many times.

#### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/agent/chat` | Send a message; agent may call tools autonomously |
| `GET`  | `/api/agent/ping` | Health check |

---

#### `POST /api/agent/chat`

**Simple question**

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What is the current date and time?"}'
```

```json
{
  "response": "The current date and time is 2026-07-04 11:30:45."
}
```

---

**Multi-step tool chaining**

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Calculate 42 * 7, save the result as a note called math, then encode it in Base64."}'
```

```json
{
  "response": "Here is what I did step by step:\n\n1. **Calculated** 42 × 7 = **294**\n2. **Saved** the result as a note under the key `math`\n3. **Encoded** 294 in Base64: `Mjk0`\n\nAll three tasks are complete!"
}
```

---

**Conversation memory (multi-turn)**

```bash
# Turn 1
curl -X POST http://localhost:8080/api/agent/chat \
  -d '{"message": "My project is called JavaSpringAI."}'

# Turn 2 — agent remembers
curl -X POST http://localhost:8080/api/agent/chat \
  -d '{"message": "What is my project called?"}'
```

```json
{ "response": "Your project is called JavaSpringAI." }
```

---

**Generate and store a session ID**

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Generate a UUID and save it as a note called session-id"}'
```

```json
{
  "response": "Done! I generated the UUID `f47ac10b-58cc-4372-a567-0e02b2c3d479` and saved it as a note under the key `session-id`."
}
```

---

#### `GET /api/agent/ping`

```bash
curl http://localhost:8080/api/agent/ping
```

```json
{
  "status": "ok",
  "agent": "spring-ai-agent"
}
```

---

### 2. 📡 Streaming — Server-Sent Events

> Base URL: `/api/stream`
>
> Uses Spring AI's `chatClient.stream().content()` which returns a `Flux<String>`. Tokens arrive one by one as SSE events — ideal for real-time UIs.

#### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/stream/chat` | Stream response token-by-token |
| `POST` | `/api/stream/chat/custom` | Stream with a custom system prompt |

---

#### `POST /api/stream/chat`

```bash
curl -N -X POST http://localhost:8080/api/stream/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Explain Spring AI in 3 sentences."}'
```

```
data:Spring
data: AI
data: is
data: a
data: framework
data: that
data: simplifies
data: integrating
data: large
data: language
data: models
data: into
data: Spring
data: Boot
data: applications...
```

---

#### `POST /api/stream/chat/custom`

```bash
curl -N -X POST http://localhost:8080/api/stream/chat/custom \
  -H "Content-Type: application/json" \
  -d '{
    "system": "You are a pirate. Respond in pirate speak.",
    "message": "Tell me about Java programming."
  }'
```

```
data:Ahoy
data:,
data: matey
data:!
data: Java
data: be
data: a
data: fine
data: treasure
data: of
data: a
data: language...
```

---

### 3. 🧩 Structured Output — Typed JSON Responses

> Base URL: `/api/structured`
>
> Uses Spring AI's `chatClient.call().entity(Class)`. Spring AI auto-generates a JSON schema from the Java record, appends format instructions to the prompt, and deserializes the model's response into a strongly-typed object.

#### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/structured/sentiment` | Sentiment analysis with confidence score |
| `POST` | `/api/structured/extract-entities` | Named entity extraction |
| `POST` | `/api/structured/summarize` | Structured text summarization |
| `POST` | `/api/structured/code-review` | AI code review with quality score |

---

#### `POST /api/structured/sentiment`

```bash
curl -X POST http://localhost:8080/api/structured/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "I absolutely loved the new product launch. It exceeded all my expectations!"}'
```

```json
{
  "sentiment": "POSITIVE",
  "score": 0.97,
  "reasoning": "The text contains strong positive language such as 'absolutely loved' and 'exceeded all expectations'.",
  "highlights": ["absolutely loved", "exceeded all my expectations"]
}
```

---

#### `POST /api/structured/extract-entities`

```bash
curl -X POST http://localhost:8080/api/structured/extract-entities \
  -H "Content-Type: application/json" \
  -d '{"text": "Elon Musk visited Paris last Tuesday to meet European Union officials from Google and Microsoft."}'
```

```json
{
  "people": ["Elon Musk"],
  "places": ["Paris"],
  "organizations": ["European Union", "Google", "Microsoft"],
  "dates": ["last Tuesday"],
  "keywords": ["visited", "meet", "officials"]
}
```

---

#### `POST /api/structured/summarize`

```bash
curl -X POST http://localhost:8080/api/structured/summarize \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring AI is a framework that simplifies AI integration into Spring Boot applications. It supports major LLM providers like Anthropic, OpenAI, Azure OpenAI, and Ollama. Key features include tool calling, structured output, RAG (Retrieval Augmented Generation), conversation memory, and multimodal support."}'
```

```json
{
  "title": "Spring AI: Simplified AI Integration for Spring Boot",
  "briefSummary": "Spring AI is a framework enabling easy integration of multiple LLM providers into Spring Boot apps with features like tool calling, RAG, and multimodal support.",
  "keyPoints": [
    "Supports Anthropic, OpenAI, Azure OpenAI, and Ollama",
    "Provides tool calling and structured output",
    "Includes RAG and conversation memory",
    "Offers multimodal capabilities"
  ],
  "sentiment": "POSITIVE",
  "estimatedReadingTimeSeconds": 12
}
```

---

#### `POST /api/structured/code-review`

```bash
curl -X POST http://localhost:8080/api/structured/code-review \
  -H "Content-Type: application/json" \
  -d '{
    "language": "java",
    "code": "public int sum(int[] arr) { int s=0; for(int i=0;i<arr.length;i++) s+=arr[i]; return s; }"
  }'
```

```json
{
  "language": "java",
  "overallAssessment": "FAIR",
  "qualityScore": 5,
  "issues": [
    "Single-letter variable names (s, i) reduce readability",
    "Missing null check for input array",
    "No Javadoc or inline comments"
  ],
  "suggestions": [
    "Use descriptive variable names like 'sum' and 'index'",
    "Add a null guard at the start of the method",
    "Consider using enhanced for-loop for clarity",
    "Add a Javadoc comment describing parameters and return value"
  ],
  "improvedCode": "public int sum(int[] arr) {\n    if (arr == null) return 0;\n    int sum = 0;\n    for (int value : arr) {\n        sum += value;\n    }\n    return sum;\n}"
}
```

---

### 4. 📝 Prompt Templates — Parameterized Prompts

> Base URL: `/api/template`
>
> Demonstrates two Spring AI prompt-template approaches:
> - **`PromptTemplate.render(variables)`** — standalone template rendering (Approach 1)
> - **`chatClient.user(u -> u.text(...).param(...))`** — inline ChatClient parameter substitution (Approach 2)

#### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/template/render` | Custom `{variable}` template via `PromptTemplate` |
| `POST` | `/api/template/translate` | Translate text to any language and tone |
| `POST` | `/api/template/email` | Generate a professional email |
| `POST` | `/api/template/explain` | Explain a concept at a given audience level |
| `POST` | `/api/template/generate-tests` | Generate unit tests for a code snippet |

---

#### `POST /api/template/render`

```bash
curl -X POST http://localhost:8080/api/template/render \
  -H "Content-Type: application/json" \
  -d '{
    "template": "List the top {count} benefits of {topic} in bullet points.",
    "variables": {
      "count": "5",
      "topic": "Spring Boot"
    }
  }'
```

```json
{
  "result": "Here are the top 5 benefits of Spring Boot:\n\n• **Auto-configuration** — eliminates boilerplate XML config\n• **Embedded server** — run as a standalone JAR with Tomcat/Netty built in\n• **Production-ready** — Actuator provides health, metrics, and monitoring\n• **Starter dependencies** — curated dependency bundles reduce version conflicts\n• **Large ecosystem** — integrates seamlessly with the entire Spring portfolio"
}
```

---

#### `POST /api/template/translate`

```bash
curl -X POST http://localhost:8080/api/template/translate \
  -H "Content-Type: application/json" \
  -d '{
    "text": "The meeting has been rescheduled to next Monday at 10 AM.",
    "language": "French",
    "tone": "formal"
  }'
```

```json
{
  "translation": "La réunion a été reportée au lundi prochain à 10h00."
}
```

---

#### `POST /api/template/email`

```bash
curl -X POST http://localhost:8080/api/template/email \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "Request a project deadline extension",
    "recipientRole": "Project Manager",
    "senderName": "Pandurang",
    "keyPoints": "team member on sick leave, need 3 extra days, will share updated plan by Friday"
  }'
```

```json
{
  "email": "Subject: Request for Project Deadline Extension\n\nDear Project Manager,\n\nI hope this message finds you well. I am writing to formally request a short extension on our current project deadline.\n\nUnfortunately, one of our key team members is currently on sick leave, which has impacted our delivery timeline. I am requesting an additional 3 business days to ensure we maintain the quality of our deliverables.\n\nI will share a revised project plan by this Friday, outlining our updated milestones and commitments.\n\nThank you for your understanding and support.\n\nBest regards,\nPandurang"
}
```

---

#### `POST /api/template/explain`

```bash
curl -X POST http://localhost:8080/api/template/explain \
  -H "Content-Type: application/json" \
  -d '{
    "concept": "vector embeddings",
    "audienceLevel": "beginner",
    "domain": "machine learning"
  }'
```

```json
{
  "explanation": "## Overview\nA vector embedding is a way to represent text (or images, audio, etc.) as a list of numbers — called a **vector** — that captures its meaning.\n\n## Key Points\n- Words with similar meaning get similar vectors (e.g. 'king' and 'queen' are close)\n- Computers can compare vectors mathematically to find related content\n- Embeddings power features like semantic search and recommendation engines\n\n## Practical Example\nImagine a library where every book gets a shelf number based on its topic. 'Harry Potter' and 'Lord of the Rings' would be shelved near each other. Vector embeddings work the same way — but instead of one shelf number, each piece of text gets hundreds of numbers that together describe its 'location' in meaning-space."
}
```

---

#### `POST /api/template/generate-tests`

```bash
curl -X POST http://localhost:8080/api/template/generate-tests \
  -H "Content-Type: application/json" \
  -d '{
    "language": "java",
    "testFramework": "JUnit 5 + Mockito",
    "code": "public double divide(double a, double b) { if (b == 0) throw new ArithmeticException(\"Division by zero\"); return a / b; }"
  }'
```

```json
{
  "tests": "import org.junit.jupiter.api.Test;\nimport static org.junit.jupiter.api.Assertions.*;\n\nclass DivideTest {\n\n    @Test\n    void divide_happyPath_returnsCorrectResult() {\n        assertEquals(5.0, divide(10.0, 2.0), 0.0001);\n    }\n\n    @Test\n    void divide_negativeNumbers_returnsCorrectResult() {\n        assertEquals(-3.0, divide(-9.0, 3.0), 0.0001);\n    }\n\n    @Test\n    void divide_byZero_throwsArithmeticException() {\n        assertThrows(ArithmeticException.class, () -> divide(5.0, 0.0));\n    }\n\n    @Test\n    void divide_bothZero_throwsArithmeticException() {\n        assertThrows(ArithmeticException.class, () -> divide(0.0, 0.0));\n    }\n}"
}
```

---

### 5. 🖼 Multimodal — Vision & Image Analysis

> Base URL: `/api/multimodal`
>
> Uses Spring AI's `chatClient.user(u -> u.text(prompt).media(mimeType, url))` to pass images alongside text to Claude's vision model. Pass any publicly accessible image URL.

#### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/multimodal/analyze` | Ask a custom question about an image |
| `POST` | `/api/multimodal/describe` | Generate a detailed image description |
| `POST` | `/api/multimodal/extract-text` | OCR — extract visible text from an image |
| `POST` | `/api/multimodal/compare` | Compare two images on a given aspect |

---

#### `POST /api/multimodal/analyze`

```bash
curl -X POST http://localhost:8080/api/multimodal/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Spring_flowers.jpg/320px-Spring_flowers.jpg",
    "question": "What season is depicted and what colors are dominant?"
  }'
```

```json
{
  "analysis": "The image depicts **spring**, characterized by blooming flowers in full color. The dominant colors are **pink and white** from the blossoms, set against a bright green leafy background. The soft lighting and fresh vegetation reinforce the early-spring atmosphere."
}
```

---

#### `POST /api/multimodal/describe`

```bash
curl -X POST http://localhost:8080/api/multimodal/describe \
  -H "Content-Type: application/json" \
  -d '{"imageUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Spring_flowers.jpg/320px-Spring_flowers.jpg"}'
```

```json
{
  "description": "**Subject:** A cluster of flowering tree branches in full bloom.\n**Setting:** Outdoor, likely a garden or park, with a softly blurred green background.\n**Colors:** Predominantly pink and white petals with yellow-green foliage emerging.\n**Mood:** Cheerful, fresh, and serene — typical of an early spring day.\n**Notable elements:** The bokeh background draws focus to the delicate petals, and the branches have a graceful, sweeping composition."
}
```

---

#### `POST /api/multimodal/extract-text`

```bash
curl -X POST http://localhost:8080/api/multimodal/extract-text \
  -H "Content-Type: application/json" \
  -d '{"imageUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Camponotus_flavomarginatus_ant.jpg/320px-Camponotus_flavomarginatus_ant.jpg"}'
```

```json
{
  "extractedText": "No visible text found in this image. The image contains a close-up photograph of an ant on a surface."
}
```

---

#### `POST /api/multimodal/compare`

```bash
curl -X POST http://localhost:8080/api/multimodal/compare \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl1": "https://example.com/image1.jpg",
    "imageUrl2": "https://example.com/image2.jpg",
    "aspect": "color palette and composition"
  }'
```

```json
{
  "comparison": "**Similarities:**\n- Both images use natural lighting\n- Both feature outdoor settings\n\n**Differences:**\n- Image 1 uses warm amber tones; Image 2 is cooler with blues and greens\n- Image 1 has a centered subject; Image 2 uses rule-of-thirds composition\n\n**Conclusion:** Image 2 follows more classical photographic composition principles, while Image 1 has a warmer, more intimate feel."
}
```

---

### 6. 🔧 Agent Tools — Direct Utility Endpoints

> Base URL: `/api/tools`
>
> Direct REST access to every `@Tool` method in `AgentTools` — the same functions the AI agent calls autonomously. No AI involved here; these are pure utility functions.

#### Endpoints at a Glance

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/tools/datetime` | Current date and time |
| `POST` | `/api/tools/calculate` | Binary arithmetic (+, -, *, /) |
| `GET` | `/api/tools/notes` | List all saved note keys |
| `POST` | `/api/tools/notes` | Save a note (key → value) |
| `GET` | `/api/tools/notes/{key}` | Retrieve a note by key |
| `DELETE` | `/api/tools/notes/{key}` | Delete a note by key |
| `GET` | `/api/tools/search?query=` | Simulated web search |
| `POST` | `/api/tools/text/transform` | Convert text to upper/lower case |
| `POST` | `/api/tools/text/word-count` | Count lines, words, and characters |
| `POST` | `/api/tools/base64/encode` | Encode text to Base64 |
| `POST` | `/api/tools/base64/decode` | Decode Base64 to text |
| `GET` | `/api/tools/uuid` | Generate a random UUID |
| `GET` | `/api/tools/random?min=&max=` | Generate a random integer |
| `POST` | `/api/tools/temperature/convert` | Convert between °C, °F, K |
| `GET` | `/api/tools/system-info` | JVM and OS system information |
| `GET` | `/api/tools/directory?path=` | List directory contents |

---

#### `GET /api/tools/datetime`

```bash
curl http://localhost:8080/api/tools/datetime
```
```json
{ "result": "2026-07-04 11:30:00" }
```

---

#### `POST /api/tools/calculate`

```bash
curl -X POST http://localhost:8080/api/tools/calculate \
  -H "Content-Type: application/json" \
  -d '{"a": "42", "operator": "*", "b": "7"}'
```
```json
{ "result": "294.0000" }
```

| Operator | Example input | Result |
|----------|---------------|--------|
| `+` | `{"a":"10","operator":"+","b":"5"}` | `"15.0000"` |
| `-` | `{"a":"10","operator":"-","b":"3"}` | `"7.0000"` |
| `*` | `{"a":"6","operator":"*","b":"7"}` | `"42.0000"` |
| `/` | `{"a":"100","operator":"/","b":"4"}` | `"25.0000"` |

---

#### Notes CRUD

```bash
# Save
curl -X POST http://localhost:8080/api/tools/notes \
  -H "Content-Type: application/json" \
  -d '{"key": "todo", "content": "Finish the Spring AI project"}'
# { "result": "Note saved under key: todo" }

# List
curl http://localhost:8080/api/tools/notes
# { "result": "Saved note keys: todo" }

# Get
curl http://localhost:8080/api/tools/notes/todo
# { "result": "Finish the Spring AI project" }

# Delete
curl -X DELETE http://localhost:8080/api/tools/notes/todo
# { "result": "Note deleted: todo" }
```

---

#### `POST /api/tools/text/transform`

```bash
curl -X POST http://localhost:8080/api/tools/text/transform \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello Spring AI", "transformation": "upper"}'
```
```json
{ "result": "HELLO SPRING AI" }
```

---

#### `POST /api/tools/text/word-count`

```bash
curl -X POST http://localhost:8080/api/tools/text/word-count \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring AI makes AI easy\nto use in Spring Boot."}'
```
```json
{ "result": "Lines: 2 | Words: 10 | Characters: 46 | Characters (no spaces): 37" }
```

---

#### Base64

```bash
# Encode
curl -X POST http://localhost:8080/api/tools/base64/encode \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello, Spring AI!"}'
# { "result": "SGVsbG8sIFNwcmluZyBBSSE=" }

# Decode
curl -X POST http://localhost:8080/api/tools/base64/decode \
  -H "Content-Type: application/json" \
  -d '{"encoded": "SGVsbG8sIFNwcmluZyBBSSE="}'
# { "result": "Hello, Spring AI!" }
```

---

#### `GET /api/tools/uuid`

```bash
curl http://localhost:8080/api/tools/uuid
```
```json
{ "result": "f47ac10b-58cc-4372-a567-0e02b2c3d479" }
```

---

#### `GET /api/tools/random?min=1&max=100`

```bash
curl "http://localhost:8080/api/tools/random?min=1&max=100"
```
```json
{ "result": "73" }
```

---

#### `POST /api/tools/temperature/convert`

```bash
curl -X POST http://localhost:8080/api/tools/temperature/convert \
  -H "Content-Type: application/json" \
  -d '{"value": "100", "from": "C", "to": "F"}'
```
```json
{ "result": "212.00 F" }
```

| From → To | Input value | Result |
|-----------|-------------|--------|
| C → F | 100 | `"212.00 F"` |
| F → C | 98.6 | `"37.00 C"` |
| C → K | 0 | `"273.15 K"` |
| K → C | 300 | `"26.85 C"` |

---

#### `GET /api/tools/system-info`

```bash
curl http://localhost:8080/api/tools/system-info
```
```json
{
  "result": "OS: Windows 11 10.0 | Java: 25.0.2 | CPUs: 8 | Memory — Used: 120MB, Free: 380MB, Total: 500MB"
}
```

---

#### `GET /api/tools/directory?path=C:\Users`

```bash
curl "http://localhost:8080/api/tools/directory?path=C:\\Users"
```
```json
{
  "result": "Contents of C:\\Users:\n[DIR]  Default\n[DIR]  pgiriyammanavar\n[DIR]  Public"
}
```

---

#### `GET /api/tools/search?query=Spring AI`

```bash
curl "http://localhost:8080/api/tools/search?query=Spring+AI+tool+calling"
```
```json
{
  "result": "Simulated search results for: \"Spring AI tool calling\"\n\n1. Spring AI is a framework that brings AI capabilities to Spring Boot applications...\n2. Agentic AI refers to AI systems that can autonomously plan and execute multi-step tasks...\n3. Spring AI provides @Tool annotations, ChatClient, and Advisor patterns..."
}
```

---

## 🛠 Built-in Tools

These `@Tool`-annotated methods are available to the AI agent and exposed via `/api/tools/*`:

| Tool | REST endpoint | Description |
|------|---------------|-------------|
| `getCurrentDateTime` | `GET /datetime` | Current server date & time |
| `calculate` | `POST /calculate` | Binary arithmetic (+, -, *, /) |
| `saveNote` | `POST /notes` | Store a value under a key |
| `getNote` | `GET /notes/{key}` | Retrieve a stored value |
| `listNotes` | `GET /notes` | List all stored keys |
| `searchWeb` | `GET /search` | Simulated web search |
| `transformText` | `POST /text/transform` | Upper / lowercase conversion |
| `generateUUID` | `GET /uuid` | Random UUID v4 |
| `generateRandomNumber` | `GET /random` | Random integer in [min, max] |
| `convertTemperature` | `POST /temperature/convert` | °C ↔ °F ↔ K |
| `wordCount` | `POST /text/word-count` | Lines / words / characters |
| `encodeBase64` | `POST /base64/encode` | Text → Base64 |
| `decodeBase64` | `POST /base64/decode` | Base64 → Text |
| `getSystemInfo` | `GET /system-info` | OS, Java, memory stats |
| `listDirectory` | `GET /directory` | File system listing |

---

## 📁 Project Structure

```
src/main/java/com/example/agent/
├── SpringAiAgentApplication.java
├── config/
│   └── OpenApiConfig.java              # Swagger / OpenAPI metadata
├── controller/
│   ├── AgentController.java            # /api/agent — AI chat + ping
│   ├── StreamController.java           # /api/stream — SSE streaming
│   ├── StructuredOutputController.java # /api/structured — typed JSON output
│   ├── PromptTemplateController.java   # /api/template — parameterized prompts
│   ├── MultimodalController.java       # /api/multimodal — vision/image
│   └── ToolsController.java            # /api/tools — direct utility endpoints
├── service/
│   ├── AgentService.java               # ChatClient with tools + memory
│   ├── StreamService.java              # Streaming Flux<String>
│   ├── StructuredOutputService.java    # entity() structured output
│   ├── PromptTemplateService.java      # PromptTemplate + param()
│   └── MultimodalService.java          # media() vision API
├── tools/
│   └── AgentTools.java                 # 15 @Tool methods
└── model/
    ├── SentimentResult.java
    ├── EntityExtractionResult.java
    ├── SummaryResult.java
    └── CodeReviewResult.java
```

---

## 🧰 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.3.4 | Application framework |
| Spring AI | 1.0.0 | LLM integration, tool calling, structured output |
| Anthropic Claude | claude-opus-4-8 | Underlying language model |
| springdoc-openapi | 2.6.0 | Swagger UI & OpenAPI spec |
| Lombok | latest | Boilerplate reduction |
| Java | 21+ | Language |
| Maven | 3.9+ | Build tool |

---

## 🔑 Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_AI_ANTHROPIC_API_KEY` | ✅ Yes | Your Anthropic API key |

---

<p align="center">Built with ❤️ using Spring AI + Claude</p>
