package com.project.fitness.service.recommendation;

import java.util.List;

public record RecommendationAdvice(
        String type,
        String recommendation,
        List<String> improvements,
        List<String> suggestions,
        List<String> safety
) {}
