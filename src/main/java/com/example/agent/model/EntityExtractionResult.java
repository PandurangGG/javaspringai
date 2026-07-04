package com.example.agent.model;

import java.util.List;

public record EntityExtractionResult(
        List<String> people,
        List<String> places,
        List<String> organizations,
        List<String> dates,
        List<String> keywords
) {}
