package com.ponto.dto;

import com.ponto.entity.GoalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PerformanceGoalResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String title;
    private String description;
    private Integer weight;
    private LocalDate dueDate;
    private GoalStatus status;
    private LocalDateTime createdAt;
}
