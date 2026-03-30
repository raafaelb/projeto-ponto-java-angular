package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalaryAdjustmentCreateDTO {
    @NotNull
    private Long employeeId;
    @NotNull
    private BigDecimal newSalary;
    @NotNull
    private LocalDate effectiveDate;
    private String reason;
}
