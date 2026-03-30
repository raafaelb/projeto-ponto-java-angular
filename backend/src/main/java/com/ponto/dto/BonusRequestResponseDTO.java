package com.ponto.dto;

import com.ponto.entity.ApprovalStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BonusRequestResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private BigDecimal amount;
    private LocalDate referenceDate;
    private String reason;
    private ApprovalStatus status;
    private String reviewComment;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
