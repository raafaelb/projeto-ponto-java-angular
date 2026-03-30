package com.ponto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CareerLevelResponseDTO {
    private Long id;
    private String name;
    private Integer rankOrder;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
