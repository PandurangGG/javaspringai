# Java AI Frameworks — Multi-Provider Demo

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20AI-1.0.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/LangChain4j-0.36.2-FF6B35?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
</p>

<p align="center">
  A comprehensive reference project demonstrating <strong>every major Java AI library and provider</strong> in one Spring Boot application.<br/>
  Each package folder is named to tell you exactly <strong>which library and which provider</strong> it uses.
</p>

---

## Project Architecture

```
com.example
│
├── agent/                          ← Spring AI 1.0.0 + Anthropic Claude
│   ├── controller/                 ← 9 controllers: Chat, Stream, Structured, Templates,
│   │                                  Multimodal, Tools, ModelConfig, Conversation, Output
│   ├── service/                    ← ChatClient, Streaming, Structured Output, Memory…
│   ├── model/                      ← Typed records: SentimentResult, SummaryResult…
│   ├── tools/                      ← 15 @Tool methods for agent tool-calling
│   └── config/                     ← OpenAPI / Swagger configuration
│
├── springai/                       ← Spring AI 1.0.0 — unified API, swapped providers
│   ├── openai/                     ← Spring AI + OpenAI GPT-4o-mini
│   │   ├── service/SpringAiOpenAiService.java
│   │   └── controller/SpringAiOpenAiController.java
│   ├── ollama/                     ← Spring AI + Ollama (local, FREE, no API key)
│   │   ├── service/SpringAiOllamaService.java
│   │   └── controller/SpringAiOllamaController.java
│   └── mistral/                    ← Spring AI + Mistral AI
│       ├── service/SpringAiMistralService.java
│       └── controller/SpringAiMistralController.java
│
└── langchain4j/                    ← LangChain4j 0.36.2 — alternative Java AI framework
    ├── openai/                     ← LangChain4j + OpenAI
    │   ├── config/LangChain4jOpenAiConfig.java
    │   ├── service/LangChain4jOpenAiService.java
    │   └── controller/LangChain4jOpenAiController.java
    ├── anthropic/                  ← LangChain4j + Anthropic (same key as agent/)
    │   ├── config/LangChain4jAnthropicConfig.java
    │   ├── service/LangChain4jAnthropicService.java
    │   └── controller/LangChain4jAnthropicController.java
    ├── ollama/                     ← LangChain4j + Ollama (local, FREE)
    │   ├── config/LangChain4jOllamaConfig.java
    │   ├── service/LangChain4jOllamaService.java
    │   └── controller/LangChain4jOllamaController.java
    ├── gemini/                     ← LangChain4j + Google Gemini (free tier!)
    │   ├── config/LangChain4jGeminiConfig.java
    │   ├── service/LangChain4jGeminiService.java
    │   └── controller/LangChain4jGeminiController.java
    └── mistral/                    ← LangChain4j + Mistral AI
        ├── config/LangChain4jMistralConfig.java
        ├── service/LangChain4jMistralService.java
        └── controller/LangChain4jMistralController.java
```

---

## Libraries Integrated

| Library | Version | Type | Providers |
|---|---|---|---|
| **Spring AI** | 1.0.0 | Spring-native unified API | Anthropic, OpenAI, Ollama, Mistral AI |
| **LangChain4j** | 0.36.2 | Provider-agnostic AI framework | OpenAI, Anthropic, Ollama, Google Gemini, Mistral AI |

---

## Framework Comparison: Spring AI vs LangChain4j

| Feature | Spring AI | LangChain4j |
|---|---|---|
| **Chat API** | `chatClient.prompt().user().call().content()` | `chatModel.generate(message)` |
| **Streaming** | `chatClient.stream().content()` → `Flux<String>` | `StreamingResponseHandler` callbacks |
| **Structured Output** | `chatClient.call().entity(MyClass.class)` | AiServices with typed return type |
| **System Prompt** | `.system(prompt)` on ChatClient | `@SystemMessage` on interface method |
| **Template Variables** | `PromptTemplate` / `SystemPromptTemplate` | `@V("name")` annotation |
| **Conversation Memory** | `MessageWindowChatMemory` + `MessageChatMemoryAdvisor` | `@MemoryId` automatic routing |
| **Tool Calling** | `@Tool` on methods, registered per `ChatClient` | `@Tool` on methods, registered per `AiServices` |
| **Per-request Options** | `OpenAiChatOptions.builder()` / `AnthropicChatOptions.builder()` | Provider-specific builder |

---

## Quick Start

### 1. Prerequisites

- Java 21 (JDK, not JRE)
- Maven 3.9+
- At least one API key (or Ollama for completely free local use)

### 2. Set Environment Variables

```bash
# Required for existing Anthropic / agent/ controllers
export SPRING_AI_ANTHROPIC_API_KEY=sk-ant-...

# Set whichever providers you want to use
export OPENAI_API_KEY=sk-...        # /api/springai/openai/* and /api/langchain4j/openai/*
export MISTRAL_API_KEY=...          # /api/springai/mistral/* and /api/langchain4j/mistral/*
export GEMINI_API_KEY=...           # /api/langchain4j/gemini/*  (FREE at aistudio.google.com)

# Ollama — no key needed, runs locally:
# 1. Download: https://ollama.ai
# 2. Pull a model: ollama pull llama3.2
```

> Endpoints for providers without a key fail at call time but won't prevent app startup.

### 3. Run

```powershell
# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\jbr"
$env:SPRING_AI_ANTHROPIC_API_KEY = "sk-ant-..."
mvn spring-boot:run
```

```bash
# macOS / Linux
SPRING_AI_ANTHROPIC_API_KEY=sk-ant-... mvn spring-boot:run
```

### 4. Explore

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **API Docs JSON:** http://localhost:8080/v3/api-docs

---

## API Reference

### Spring AI — Anthropic (Existing — `com.example.agent`)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/agent/chat` | Agentic chat with 15 tools + conversation memory |
| `GET` | `/api/agent/ping` | Health check |
| `POST` | `/api/stream/chat` | SSE streaming |
| `POST` | `/api/structured/sentiment` | Typed sentiment analysis |
| `POST` | `/api/template/render` | Prompt templates |
| `POST` | `/api/multimodal/analyze` | Vision / image analysis |
| `POST` | `/api/tools/calculate` | Direct tool access |
| `POST` | `/api/model-config/chat` | Per-request model options |
| `POST` | `/api/conversation/{id}/chat` | Per-session isolated memory |
| `POST` | `/api/output/map` | MapOutputConverter |

---

### Spring AI — OpenAI (`/api/springai/openai`)

**Requires:** `OPENAI_API_KEY`

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | Basic chat via `OpenAiChatModel` |
| `POST` | `/stream` | SSE streaming |
| `POST` | `/chat/custom` | Per-request model/temperature override |
| `GET` | `/ping` | Health check |

```bash
curl -X POST http://localhost:8080/api/springai/openai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Explain GPT-4o-mini in 2 sentences"}'
# {"library":"Spring AI 1.0.0","provider":"OpenAI","model":"gpt-4o-mini","response":"..."}

curl -N -X POST http://localhost:8080/api/springai/openai/stream \
  -H "Content-Type: application/json" \
  -d '{"message": "Tell me a short story"}'
# data: Once upon a time...

curl -X POST http://localhost:8080/api/springai/openai/chat/custom \
  -H "Content-Type: application/json" \
  -d '{"message": "Write a haiku", "temperature": 0.9, "model": "gpt-4o", "maxTokens": 200}'
```

---

### Spring AI — Ollama (`/api/springai/ollama`)

**Requires:** [Ollama](https://ollama.ai) + `ollama pull llama3.2` — **FREE, no API key**

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | Local LLM chat (llama3.2 default) |
| `POST` | `/stream` | SSE streaming from local model |
| `POST` | `/chat/with-system` | Chat with custom system prompt |
| `GET` | `/ping` | Setup instructions |

```bash
curl -X POST http://localhost:8080/api/springai/ollama/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What are the advantages of running LLMs locally?"}'

curl -X POST http://localhost:8080/api/springai/ollama/chat/with-system \
  -H "Content-Type: application/json" \
  -d '{"system": "You are a pirate. Respond in pirate speak.", "message": "Tell me about the sea."}'
```

---

### Spring AI — Mistral AI (`/api/springai/mistral`)

**Requires:** `MISTRAL_API_KEY` ([console.mistral.ai](https://console.mistral.ai))

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | Chat via `MistralAiChatModel` |
| `POST` | `/stream` | SSE streaming |
| `POST` | `/chat/custom` | Per-request model override |
| `GET` | `/ping` | Health check |

```bash
curl -X POST http://localhost:8080/api/springai/mistral/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What makes Mistral AI models unique?"}'

curl -X POST http://localhost:8080/api/springai/mistral/chat/custom \
  -H "Content-Type: application/json" \
  -d '{"message": "Summarize the Transformer architecture", "model": "mistral-large-latest", "temperature": 0.3}'
```

---

### LangChain4j — OpenAI (`/api/langchain4j/openai`)

**Requires:** `OPENAI_API_KEY`

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | Direct `ChatLanguageModel.generate()` |
| `POST` | `/stream` | SSE via `StreamingChatLanguageModel` |
| `POST` | `/ai-service` | AiServices with `@SystemMessage` / `@UserMessage` |
| `POST` | `/ai-service/persona` | AiServices with `@V` template variables |
| `POST` | `/memory-chat` | Multi-turn memory via `@MemoryId` |
| `GET` | `/ping` | Health check |

```bash
# LangChain4j's simplest API — no builder chain
curl -X POST http://localhost:8080/api/langchain4j/openai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What is LangChain4j?"}'
# {"library":"LangChain4j 0.36.2","provider":"OpenAI","api":"ChatLanguageModel.generate()","response":"..."}

# AiServices with @SystemMessage annotation
curl -X POST http://localhost:8080/api/langchain4j/openai/ai-service \
  -H "Content-Type: application/json" \
  -d '{"message": "Explain your role."}'

# Persona via @V template variables — fills {role} and {style} in @SystemMessage
curl -X POST http://localhost:8080/api/langchain4j/openai/ai-service/persona \
  -H "Content-Type: application/json" \
  -d '{"role": "senior architect", "style": "formal", "message": "Review microservices vs monolith"}'

# Multi-turn memory — send two messages with the same sessionId
curl -X POST http://localhost:8080/api/langchain4j/openai/memory-chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "alice", "message": "My name is Alice. Remember it."}'

curl -X POST http://localhost:8080/api/langchain4j/openai/memory-chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "alice", "message": "What is my name?"}'
# Response: "Your name is Alice."
```

---

### LangChain4j — Anthropic (`/api/langchain4j/anthropic`)

**Requires:** `SPRING_AI_ANTHROPIC_API_KEY` — **same key as agent/ controllers!**

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | `ChatLanguageModel.generate()` with Claude |
| `POST` | `/ai-service` | AiServices with `@SystemMessage` |
| `POST` | `/ai-service/expert` | Domain expert via `@V` variables |
| `POST` | `/memory-chat` | Per-session memory |
| `GET` | `/ping` | Health check |

```bash
# Same Anthropic key, but LangChain4j framework instead of Spring AI
curl -X POST http://localhost:8080/api/langchain4j/anthropic/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "How does Claude differ from GPT?"}'

# Expert persona with depth control
curl -X POST http://localhost:8080/api/langchain4j/anthropic/ai-service/expert \
  -H "Content-Type: application/json" \
  -d '{"expert": "quantum physics", "depth": "beginner-friendly", "message": "Explain superposition."}'
```

---

### LangChain4j — Ollama (`/api/langchain4j/ollama`)

**Requires:** [Ollama](https://ollama.ai) — **FREE, no API key**

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | `OllamaChatModel.generate()` |
| `POST` | `/stream` | SSE via `OllamaStreamingChatModel` |
| `POST` | `/ai-service` | AiServices with local Ollama backend |
| `GET` | `/ping` | Setup instructions |

```bash
curl -X POST http://localhost:8080/api/langchain4j/ollama/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What are the benefits of local LLMs?"}'
# {"library":"LangChain4j 0.36.2","provider":"Ollama (local)","model":"llama3.2","response":"..."}
```

---

### LangChain4j — Google Gemini (`/api/langchain4j/gemini`)

**Requires:** `GEMINI_API_KEY` — **FREE tier at [aistudio.google.com](https://aistudio.google.com)**

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | `GoogleAiGeminiChatModel.generate()` |
| `POST` | `/ai-service` | AiServices with `@SystemMessage` |
| `POST` | `/ai-service/domain` | Domain expert via `@V` variables |
| `POST` | `/memory-chat` | Per-session memory |
| `GET` | `/ping` | Health check + free key link |

```bash
# Free Gemini API
curl -X POST http://localhost:8080/api/langchain4j/gemini/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Explain Gemini 1.5 Flash capabilities."}'

# Domain expert — fills {domain} and {format} in system prompt
curl -X POST http://localhost:8080/api/langchain4j/gemini/ai-service/domain \
  -H "Content-Type: application/json" \
  -d '{"domain": "machine learning", "format": "bullet-point", "message": "Explain gradient descent."}'

# Multi-turn memory
curl -X POST http://localhost:8080/api/langchain4j/gemini/memory-chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "carol", "message": "I am learning about neural networks."}'
```

---

### LangChain4j — Mistral AI (`/api/langchain4j/mistral`)

**Requires:** `MISTRAL_API_KEY`

| Method | Path | Description |
|---|---|---|
| `POST` | `/chat` | `MistralAiChatModel.generate()` |
| `POST` | `/stream` | SSE via `MistralAiStreamingChatModel` |
| `POST` | `/ai-service` | AiServices with `@SystemMessage` |
| `GET` | `/ping` | Health check |

```bash
curl -X POST http://localhost:8080/api/langchain4j/mistral/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Compare Mixtral 8x7B with GPT-4o-mini."}'
```

---

## LangChain4j Key Concepts

```java
// 1. Direct model call — simplest API, returns String
String response = chatModel.generate("Hello!");

// 2. AiServices — annotate a Java interface; LangChain4j builds the proxy
interface Assistant {
    @SystemMessage("You are a helpful assistant.")      // declares system prompt
    String chat(@UserMessage String message);            // marks user input param

    @SystemMessage("You are a {role} expert.")          // template in system prompt
    String chatAs(@V("role") String role,               // @V fills {role} at runtime
                  @UserMessage String message);

    String chat(@MemoryId String sessionId,             // @MemoryId routes to per-user memory
                @UserMessage String message);
}

Assistant assistant = AiServices.builder(Assistant.class)
    .chatLanguageModel(model)
    .chatMemoryProvider(id -> MessageWindowChatMemory.withMaxMessages(10))
    .build();

// 3. Streaming — callback-based, wrapped into Flux for SSE
Flux<String> tokens = Flux.create(sink ->
    streamingModel.generate("Tell a story", new StreamingResponseHandler<AiMessage>() {
        public void onNext(String token)             { sink.next(token); }
        public void onComplete(Response<AiMessage> r){ sink.complete(); }
        public void onError(Throwable e)             { sink.error(e); }
    }));
```

---

## API Keys Summary

| Provider | Env Variable | Where to Get | Cost |
|---|---|---|---|
| Anthropic Claude | `SPRING_AI_ANTHROPIC_API_KEY` | [console.anthropic.com](https://console.anthropic.com) | Pay-per-use |
| OpenAI | `OPENAI_API_KEY` | [platform.openai.com](https://platform.openai.com) | Pay-per-use |
| Google Gemini | `GEMINI_API_KEY` | [aistudio.google.com](https://aistudio.google.com) | **Free tier** |
| Mistral AI | `MISTRAL_API_KEY` | [console.mistral.ai](https://console.mistral.ai) | Pay-per-use |
| Ollama | *(none needed)* | [ollama.ai](https://ollama.ai) | **Free + local** |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 3.3.4 |
| AI Framework 1 | Spring AI 1.0.0 |
| AI Framework 2 | LangChain4j 0.36.2 |
| AI Providers | Anthropic Claude, OpenAI, Google Gemini, Mistral AI, Ollama |
| API Docs | springdoc-openapi 2.6.0 (Swagger UI at `/swagger-ui/index.html`) |
| Build | Maven 3.9 |

---

## License

MIT — free to use, modify, and distribute.
