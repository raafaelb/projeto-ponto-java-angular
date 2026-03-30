package com.ponto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplianceAuditEventResponseDTO {
    private Long id;
    private String eventType;
    private String message;
    private String severity;
    private String entityType;
    private Long entityId;
    private String createdBy;
    private LocalDateTime createdAt;
}
