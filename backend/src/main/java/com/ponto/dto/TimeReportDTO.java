package com.ponto.dto;

import lombok.Data;

import java.util.List;

@Data
public class TimeReportDTO {
    private List<TimeReportRowDTO> rows;
    private Long totalWorkedMinutes;
    private Long totalApprovedOvertimeMinutes;
    private Long totalAbsenceDays;
    private Long totalAnomalies;
}
