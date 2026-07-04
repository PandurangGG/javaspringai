package com.example.agent.service;

import com.example.agent.model.CodeReviewResult;
import com.example.agent.model.EntityExtractionResult;
import com.example.agent.model.SentimentResult;
import com.example.agent.model.SummaryResult;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Demonstrates Spring AI's structured output via chatClient.call().entity(Class).
 * Spring AI injects a BeanOutputConverter that appends JSON-schema instructions
 * to the prompt and deserializes the model's response into the target type.
 */
@Service
public class StructuredOutputService {

    private final ChatClient chatClient;

    public StructuredOutputService(AnthropicChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public SentimentResult analyzeSentiment(String text) {
        return chatClient.prompt()
                .user("Analyze the sentiment of the following text and return a structured result.\n\nText:\n" + text)
                .call()
                .entity(SentimentResult.class);
    }

    public EntityExtractionResult extractEntities(String text) {
        return chatClient.prompt()
                .user("Extract all named entities from the following text and categorize them.\n\nText:\n" + text)
                .call()
                .entity(EntityExtractionResult.class);
    }

    public SummaryResult summarize(String text) {
        return chatClient.prompt()
                .user("Produce a structured summary of the following text.\n\nText:\n" + text)
                .call()
                .entity(SummaryResult.class);
    }

    public CodeReviewResult reviewCode(String code, String language) {
        return chatClient.prompt()
                .user("""
                        Review the following %s code for quality, bugs, and style.
                        Return a structured report with a quality score (1–10), issues, improvement suggestions,
                        and a corrected version of the code.

                        ```%s
                        %s
                        ```
                        """.formatted(language, language, code))
                .call()
                .entity(CodeReviewResult.class);
    }
}
