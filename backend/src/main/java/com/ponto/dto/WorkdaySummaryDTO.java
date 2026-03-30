package com.ponto.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkdaySummaryDTO {
    private List<WorkdayRecordDTO> records;
    private Long totalWorkedMinutes;
}
