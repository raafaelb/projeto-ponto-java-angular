package com.ponto.dto;

import com.ponto.entity.AbsenceType;
import com.ponto.entity.ApprovalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AbsenceRequestResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private AbsenceType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private ApprovalStatus status;
    private String reviewComment;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
