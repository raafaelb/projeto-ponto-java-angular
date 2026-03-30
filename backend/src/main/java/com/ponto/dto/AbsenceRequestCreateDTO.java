package com.ponto.dto;

import com.ponto.entity.AbsenceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AbsenceRequestCreateDTO {
    @NotNull
    private AbsenceType type;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String reason;
}
