package com.ponto.dto;

import com.ponto.entity.BenefitEnrollmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BenefitEnrollmentResponseDTO {
    private Long id;
    private Long benefitPlanId;
    private String benefitPlanName;
    private Long employeeId;
    private String employeeName;
    private BenefitEnrollmentStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reviewComment;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
