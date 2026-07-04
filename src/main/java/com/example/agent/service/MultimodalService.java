package com.example.agent.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Demonstrates Spring AI's multimodal (vision) support.
 * Claude accepts image URLs directly; the media() method attaches the image
 * alongside the text prompt so the model can reason about both.
 */
@Service
public class MultimodalService {

    private final ChatClient chatClient;

    public MultimodalService(AnthropicChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public String analyzeImage(String imageUrl, String question) throws MalformedURLException {
        URL url = new URL(imageUrl);
        MimeType mimeType = detectMimeType(imageUrl);
        return chatClient.prompt()
                .user(u -> u.text(question).media(mimeType, url))
                .call()
                .content();
    }

    public String describeImage(String imageUrl) throws MalformedURLException {
        return analyzeImage(imageUrl,
                "Describe this image in detail. Include: subject, setting, colors, mood, and any notable elements.");
    }

    public String extractTextFromImage(String imageUrl) throws MalformedURLException {
        return analyzeImage(imageUrl,
                "Extract and return all visible text from this image exactly as it appears, preserving layout where possible.");
    }

    public String compareImages(String imageUrl1, String imageUrl2, String aspect) throws MalformedURLException {
        URL url1 = new URL(imageUrl1);
        URL url2 = new URL(imageUrl2);
        MimeType mime1 = detectMimeType(imageUrl1);
        MimeType mime2 = detectMimeType(imageUrl2);
        return chatClient.prompt()
                .user(u -> u.text("Compare these two images focusing on: " + aspect +
                                "\nImage 1 is attached first, Image 2 is attached second. " +
                                "Provide a structured comparison with similarities, differences, and a conclusion.")
                        .media(mime1, url1)
                        .media(mime2, url2))
                .call()
                .content();
    }

    private MimeType detectMimeType(String url) {
        String lower = url.toLowerCase();
        if (lower.contains(".png"))  return MimeTypeUtils.IMAGE_PNG;
        if (lower.contains(".gif"))  return MimeTypeUtils.IMAGE_GIF;
        if (lower.contains(".webp")) return MimeType.valueOf("image/webp");
        return MimeTypeUtils.IMAGE_JPEG;
    }
}
