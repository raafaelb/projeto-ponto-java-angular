package com.ponto.dto;

import com.ponto.entity.ReviewStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PerformanceReviewResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer selfScore;
    private Integer managerScore;
    private String selfComment;
    private String managerFeedback;
    private ReviewStatus status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
