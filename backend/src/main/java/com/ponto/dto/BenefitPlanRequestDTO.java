package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BenefitPlanRequestDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private BigDecimal monthlyEmployerCost;
    @NotNull
    private BigDecimal monthlyEmployeeCost;
    private Boolean active;
}
