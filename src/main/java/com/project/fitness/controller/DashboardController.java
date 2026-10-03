package com.project.fitness.controller;

import com.project.fitness.dto.DashboardSummary;
import com.project.fitness.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary summary(Authentication authentication) {
        return dashboardService.summary(authentication.getName());
    }
}
