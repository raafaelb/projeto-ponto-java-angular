package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayslipCreateDTO {
    @NotNull
    private Long payrollCycleId;
    @NotNull
    private Long employeeId;
    @NotNull
    private BigDecimal grossPay;
    @NotNull
    private BigDecimal deductions;
    @NotNull
    private BigDecimal taxWithheld;
    @NotNull
    private BigDecimal overtimePay;
    @NotNull
    private BigDecimal bonusPay;
}
