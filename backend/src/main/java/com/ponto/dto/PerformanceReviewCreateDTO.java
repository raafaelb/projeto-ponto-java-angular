package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PerformanceReviewCreateDTO {
    @NotNull
    private Long employeeId;
    @NotNull
    private LocalDate periodStart;
    @NotNull
    private LocalDate periodEnd;
}
