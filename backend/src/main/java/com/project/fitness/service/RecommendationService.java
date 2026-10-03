package com.project.fitness.service;

import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.model.Activity;
import com.project.fitness.model.Recommendation;
import com.project.fitness.repository.RecommendationRepository;
import com.project.fitness.service.recommendation.RecommendationAdvice;
import com.project.fitness.service.recommendation.RecommendationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ActivityService activityService;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationProvider recommendationProvider;

    public RecommendationResponse generate(String userId, String activityId) {
        Activity activity = activityService.getOwnedActivity(userId, activityId);
        RecommendationAdvice advice = recommendationProvider.generate(activity);
        Recommendation recommendation = Recommendation.builder()
                .user(activity.getUser())
                .activity(activity)
                .type(advice.type())
                .recommendation(advice.recommendation())
                .improvements(advice.improvements())
                .suggestions(advice.suggestions())
                .safety(advice.safety())
                .build();
        return toResponse(recommendationRepository.save(recommendation));
    }

    public List<RecommendationResponse> forUser(String userId) {
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse).toList();
    }

    private RecommendationResponse toResponse(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getId(), recommendation.getActivity().getId(), recommendation.getActivity().getType(),
                recommendation.getType(), recommendation.getRecommendation(), recommendation.getImprovements(),
                recommendation.getSuggestions(), recommendation.getSafety(), recommendation.getCreatedAt());
    }
}
