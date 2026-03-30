package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PolicyDocumentRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String version;
    @NotNull
    private LocalDate effectiveDate;
    private String contentSummary;
    private Boolean active;
}
