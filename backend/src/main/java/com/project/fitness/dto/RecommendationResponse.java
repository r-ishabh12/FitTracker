package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationResponse(
        String id,
        String activityId,
        ActivityType activityType,
        String type,
        String recommendation,
        List<String> improvements,
        List<String> suggestions,
        List<String> safety,
        LocalDateTime createdAt
) {}
