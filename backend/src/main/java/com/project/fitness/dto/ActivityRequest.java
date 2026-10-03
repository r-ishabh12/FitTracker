package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {

    @NotNull
    private ActivityType type;
    private Map<String, Object> additionalMetrics;
    @NotNull
    @Min(1)
    private Integer duration;
    @Min(0)
    private Integer caloriesBurned;
    @NotNull
    private LocalDateTime startTime;
}
