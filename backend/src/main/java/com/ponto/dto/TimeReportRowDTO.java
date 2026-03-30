package com.ponto.dto;

import lombok.Data;

@Data
public class TimeReportRowDTO {
    private Long employeeId;
    private String employeeName;
    private String department;
    private String team;
    private Long workedMinutes;
    private Long approvedOvertimeMinutes;
    private Long absenceDays;
    private Long anomalyCount;
}
