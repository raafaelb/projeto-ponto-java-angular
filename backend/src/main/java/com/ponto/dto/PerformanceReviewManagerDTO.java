package com.ponto.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PerformanceReviewManagerDTO {
    @Min(1)
    @Max(5)
    private Integer managerScore;
    private String managerFeedback;
}
