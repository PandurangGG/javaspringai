package com.example.agent.model;

import java.util.List;

public record CodeReviewResult(
        String language,
        String overallAssessment,   // EXCELLENT, GOOD, FAIR, or POOR
        int qualityScore,            // 1 – 10
        List<String> issues,
        List<String> suggestions,
        String improvedCode
) {}
