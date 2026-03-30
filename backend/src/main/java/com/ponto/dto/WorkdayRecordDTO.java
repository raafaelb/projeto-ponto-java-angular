package com.ponto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkdayRecordDTO {
    private Long id;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private Long workedMinutes;
    private String observacao;
}
