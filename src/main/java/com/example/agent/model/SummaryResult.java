package com.example.agent.model;

import java.util.List;

public record SummaryResult(
        String title,
        String briefSummary,
        List<String> keyPoints,
        String sentiment,
        int estimatedReadingTimeSeconds
) {}
