package com.ponto.dto;

import com.ponto.entity.PayrollCycleStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PayrollCycleResponseDTO {
    private Long id;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate paymentDate;
    private PayrollCycleStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
