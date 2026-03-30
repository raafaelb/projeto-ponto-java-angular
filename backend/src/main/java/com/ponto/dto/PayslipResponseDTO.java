package com.ponto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayslipResponseDTO {
    private Long id;
    private Long payrollCycleId;
    private Long employeeId;
    private String employeeName;
    private BigDecimal grossPay;
    private BigDecimal deductions;
    private BigDecimal taxWithheld;
    private BigDecimal overtimePay;
    private BigDecimal bonusPay;
    private BigDecimal netPay;
    private LocalDateTime createdAt;
}
