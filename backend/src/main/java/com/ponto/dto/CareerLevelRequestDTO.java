package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CareerLevelRequestDTO {
    @NotBlank
    private String name;
    @NotNull
    private Integer rankOrder;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
