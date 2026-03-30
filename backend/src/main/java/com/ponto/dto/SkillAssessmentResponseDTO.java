package com.ponto.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SkillAssessmentResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String skillName;
    private Integer currentLevel;
    private Integer targetLevel;
    private LocalDate lastAssessedDate;
    private String notes;
    private LocalDateTime updatedAt;
}
