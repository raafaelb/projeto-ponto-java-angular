package com.ponto.dto;

import com.ponto.entity.AnomalyType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceAnomalyDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate occurrenceDate;
    private AnomalyType type;
    private String description;
    private Boolean resolved;
    private String resolvedBy;
    private LocalDateTime resolvedAt;
}
