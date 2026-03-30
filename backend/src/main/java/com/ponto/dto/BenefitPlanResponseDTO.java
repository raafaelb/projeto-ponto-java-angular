package com.ponto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BenefitPlanResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal monthlyEmployerCost;
    private BigDecimal monthlyEmployeeCost;
    private Boolean active;
    private LocalDateTime createdAt;
}
