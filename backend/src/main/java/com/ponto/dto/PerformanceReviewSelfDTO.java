package com.ponto.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PerformanceReviewSelfDTO {
    @Min(1)
    @Max(5)
    private Integer selfScore;
    private String selfComment;
}
