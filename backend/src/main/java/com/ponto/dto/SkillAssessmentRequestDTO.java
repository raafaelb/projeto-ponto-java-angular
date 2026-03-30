package com.ponto.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SkillAssessmentRequestDTO {
    private Long employeeId;
    private String skillName;
    private Integer currentLevel;
    private Integer targetLevel;
    private LocalDate lastAssessedDate;
    private String notes;
}
