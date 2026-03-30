package com.ponto.dto;

import com.ponto.entity.PolicyAckStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PolicyAcknowledgmentResponseDTO {
    private Long id;
    private Long policyDocumentId;
    private String policyTitle;
    private String policyVersion;
    private PolicyAckStatus status;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime createdAt;
}
