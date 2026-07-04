package com.example.agent.service;

import com.example.agent.model.SentimentResult;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Demonstrates Spring AI output converters that are NOT used elsewhere:
 *
 *  1. MapOutputConverter  — model response parsed into Map<String, Object>
 *  2. ListOutputConverter — model response parsed into List<String>
 *  3. BeanOutputConverter (explicit) — same mechanism as chatClient.entity(),
 *     but used directly so you can inspect the raw JSON before conversion.
 *
 * All three converters work by:
 *   a) Appending format instructions to the user prompt via getFormat()
 *   b) Parsing the model's raw text response via convert(String)
 */
@Service
public class OutputConverterService {

    private final ChatClient chatClient;

    public OutputConverterService(AnthropicChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    // ── 1. MapOutputConverter ────────────────────────────────────────────────

    public Map<String, Object> toMap(String userPrompt) {
        MapOutputConverter converter = new MapOutputConverter();

        String raw = chatClient.prompt()
                .user(userPrompt + "\n\n" + converter.getFormat())
                .call()
                .content();

        return converter.convert(raw);
    }

    // ── 2. ListOutputConverter ───────────────────────────────────────────────

    public List<String> toList(String userPrompt) {
        ListOutputConverter converter = new ListOutputConverter(new DefaultConversionService());

        String raw = chatClient.prompt()
                .user(userPrompt + "\n\n" + converter.getFormat())
                .call()
                .content();

        return converter.convert(raw);
    }

    // ── 3. Explicit BeanOutputConverter ─────────────────────────────────────

    public Map<String, Object> toBeanWithRaw(String text) {
        BeanOutputConverter<SentimentResult> converter = new BeanOutputConverter<>(SentimentResult.class);

        String raw = chatClient.prompt()
                .user("Analyze the sentiment of the following text.\n\nText:\n" + text
                        + "\n\n" + converter.getFormat())
                .call()
                .content();

        SentimentResult bean = converter.convert(raw);

        // Expose both the parsed bean AND the raw model output for comparison
        return Map.of(
                "raw",   raw,
                "parsed", Map.of(
                        "sentiment",   bean.sentiment(),
                        "score",       bean.score(),
                        "reasoning",   bean.reasoning(),
                        "highlights",  bean.highlights()
                )
        );
    }
}
