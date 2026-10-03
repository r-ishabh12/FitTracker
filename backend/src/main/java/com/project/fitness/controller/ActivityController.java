package com.project.fitness.controller;

import com.project.fitness.dto.ActivityRequest;
import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@Valid @RequestBody ActivityRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.status(201).body(activityService.trackActivity(authentication.getName(), request));
    }

    @GetMapping
    public List<ActivityResponse> getUserActivities(Authentication authentication) {
        return activityService.getUserActivities(authentication.getName());
    }
}
