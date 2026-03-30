package com.ponto.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PolicyDocumentResponseDTO {
    private Long id;
    private String title;
    private String version;
    private LocalDate effectiveDate;
    private String contentSummary;
    private Boolean active;
    private LocalDateTime createdAt;
}
