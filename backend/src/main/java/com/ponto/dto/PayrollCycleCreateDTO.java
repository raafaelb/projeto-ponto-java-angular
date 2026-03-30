package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PayrollCycleCreateDTO {
    @NotNull
    private LocalDate periodStart;
    @NotNull
    private LocalDate periodEnd;
    private LocalDate paymentDate;
    private String notes;
}
