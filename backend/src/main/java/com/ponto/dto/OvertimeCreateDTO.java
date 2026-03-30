package com.ponto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OvertimeCreateDTO {
    @NotNull
    private LocalDate workDate;

    @NotNull
    private Integer requestedMinutes;

    private String reason;
}
