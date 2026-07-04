package com.example.agent.model;

import java.util.List;

public record SentimentResult(
        String sentiment,       // POSITIVE, NEGATIVE, NEUTRAL, or MIXED
        double score,           // confidence 0.0 – 1.0
        String reasoning,       // one-sentence explanation
        List<String> highlights // phrases that drove the sentiment
) {}
