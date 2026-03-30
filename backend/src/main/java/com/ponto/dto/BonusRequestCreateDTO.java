package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BonusRequestCreateDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate referenceDate;
    private String reason;
}
