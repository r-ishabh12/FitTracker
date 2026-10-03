package com.project.fitness.controller;

import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/{activityId}/generate")
    public ResponseEntity<RecommendationResponse> generate(@PathVariable String activityId,
                                                            Authentication authentication) {
        return ResponseEntity.status(201).body(recommendationService.generate(authentication.getName(), activityId));
    }

    @GetMapping
    public List<RecommendationResponse> forCurrentUser(Authentication authentication) {
        return recommendationService.forUser(authentication.getName());
    }
}
