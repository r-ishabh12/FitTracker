package com.project.fitness.service;

import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.dto.DashboardSummary;
import com.project.fitness.model.Activity;
import com.project.fitness.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ActivityRepository activityRepository;
    private final ActivityService activityService;

    public DashboardSummary summary(String userId) {
        List<Activity> activities = activityRepository.findByUserIdOrderByStartTimeDesc(userId);
        LocalDate today = LocalDate.now();
        Map<LocalDate, List<Activity>> byDay = activities.stream()
                .filter(activity -> activity.getStartTime() != null)
                .filter(activity -> !activity.getStartTime().toLocalDate().isBefore(today.minusDays(6)))
                .filter(activity -> !activity.getStartTime().toLocalDate().isAfter(today))
                .collect(Collectors.groupingBy(activity -> activity.getStartTime().toLocalDate()));

        List<DashboardSummary.DailyActivity> week = new ArrayList<>();
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate date = today.minusDays(offset);
            List<Activity> dayActivities = byDay.getOrDefault(date, List.of());
            week.add(new DashboardSummary.DailyActivity(
                    date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    dayActivities.size(),
                    dayActivities.stream().mapToInt(activity -> Optional.ofNullable(activity.getDuration()).orElse(0)).sum(),
                    dayActivities.stream().mapToInt(activity -> Optional.ofNullable(activity.getCaloriesBurned()).orElse(0)).sum()
            ));
        }

        return new DashboardSummary(
                activities.size(),
                activities.stream().mapToInt(activity -> Optional.ofNullable(activity.getDuration()).orElse(0)).sum(),
                activities.stream().mapToInt(activity -> Optional.ofNullable(activity.getCaloriesBurned()).orElse(0)).sum(),
                week,
                activities.stream().limit(5).map(activityService::toResponse).toList()
        );
    }
}
