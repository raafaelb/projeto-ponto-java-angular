package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequestDTO {
    @NotNull
    private LocalDate holidayDate;

    @NotBlank
    private String name;

    private Boolean optionalHoliday;
}
