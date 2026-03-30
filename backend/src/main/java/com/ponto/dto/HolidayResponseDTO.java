package com.ponto.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayResponseDTO {
    private Long id;
    private LocalDate holidayDate;
    private String name;
    private Boolean optionalHoliday;
}
