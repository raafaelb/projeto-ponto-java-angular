package com.ponto.dto;

import com.ponto.entity.PromotionStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PromotionRequestResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long fromLevelId;
    private String fromLevelName;
    private Long toLevelId;
    private String toLevelName;
    private String justification;
    private LocalDate effectiveDate;
    private PromotionStatus status;
    private String reviewComment;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
