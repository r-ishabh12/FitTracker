package com.project.fitness.dto;

import java.util.List;

public record DashboardSummary(
        int totalActivities,
        int totalDurationMinutes,
        int totalCalories,
        List<DailyActivity> weeklyActivity,
        List<ActivityResponse> recentActivities
) {
    public record DailyActivity(String day, int activities, int minutes, int calories) {}
}
