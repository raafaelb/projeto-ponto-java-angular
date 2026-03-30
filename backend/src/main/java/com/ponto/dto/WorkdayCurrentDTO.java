package com.ponto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkdayCurrentDTO {
    private boolean clockedIn;
    private LocalDateTime clockInAt;
    private Long workedMinutesUntilNow;
}
