package com.ponto.dto;

import com.ponto.entity.GoalStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PerformanceGoalRequestDTO {
    @NotBlank
    private String title;
    private String description;

    @Min(1)
    @Max(100)
    private Integer weight;

    private LocalDate dueDate;
    private GoalStatus status;
}
