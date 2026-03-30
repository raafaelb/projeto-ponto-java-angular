package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionRequestCreateDTO {
    @NotNull
    private Long toLevelId;
    private String justification;
    private LocalDate effectiveDate;
}
