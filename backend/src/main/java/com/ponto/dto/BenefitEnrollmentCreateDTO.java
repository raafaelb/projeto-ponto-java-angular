package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BenefitEnrollmentCreateDTO {
    @NotNull
    private Long benefitPlanId;
}
