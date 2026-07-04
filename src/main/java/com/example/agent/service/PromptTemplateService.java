package com.example.agent.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Demonstrates two Spring AI prompt-template approaches:
 *  1. PromptTemplate.render() — standalone template rendering before calling the model.
 *  2. ChatClient .user(u -> u.text(...).param(...)) — inline parameter substitution.
 */
@Service
public class PromptTemplateService {

    private final ChatClient chatClient;

    public PromptTemplateService(AnthropicChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    // ── Approach 1: Spring AI PromptTemplate ────────────────────────────────

    public String renderTemplate(String template, Map<String, Object> variables) {
        String rendered = new PromptTemplate(template).render(variables);
        return chatClient.prompt()
                .user(rendered)
                .call()
                .content();
    }

    // ── Approach 2: ChatClient inline param substitution ────────────────────

    public String translateText(String text, String targetLanguage, String tone) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Translate the following text to {language}.
                        Tone: {tone}. Return only the translated text with no explanation.

                        Text:
                        {text}
                        """)
                        .param("language", targetLanguage)
                        .param("tone", tone)
                        .param("text", text))
                .call()
                .content();
    }

    public String generateEmail(String purpose, String recipientRole, String senderName, String keyPoints) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Write a professional email for the following purpose: {purpose}
                        Recipient role: {recipientRole}
                        Sender name: {senderName}
                        Key points to include: {keyPoints}

                        Include a subject line and a complete email body.
                        """)
                        .param("purpose", purpose)
                        .param("recipientRole", recipientRole)
                        .param("senderName", senderName)
                        .param("keyPoints", keyPoints))
                .call()
                .content();
    }

    public String explainConcept(String concept, String audienceLevel, String domain) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Explain "{concept}" to a {audienceLevel}-level audience in the {domain} domain.
                        Use analogies appropriate for that level.
                        Structure the answer with:
                        1. Overview
                        2. Key Points
                        3. Practical Example
                        """)
                        .param("concept", concept)
                        .param("audienceLevel", audienceLevel)
                        .param("domain", domain))
                .call()
                .content();
    }

    public String generateUnitTests(String code, String language, String testFramework) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Generate comprehensive unit tests for the following {language} code using {framework}.
                        Cover happy paths, edge cases, and error scenarios.
                        Return only the test code, ready to run.

                        Code:
                        ```{language}
                        {code}
                        ```
                        """)
                        .param("language", language)
                        .param("framework", testFramework)
                        .param("code", code))
                .call()
                .content();
    }
}
