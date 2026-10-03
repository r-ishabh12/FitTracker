package com.project.fitness.service.recommendation;

import com.project.fitness.model.Activity;

public interface RecommendationProvider {
    RecommendationAdvice generate(Activity activity);
}
