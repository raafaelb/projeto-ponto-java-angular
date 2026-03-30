package com.ponto.controller;

import com.ponto.dto.ComplianceAuditEventResponseDTO;
import com.ponto.dto.PolicyAcknowledgmentResponseDTO;
import com.ponto.dto.PolicyDocumentRequestDTO;
import com.ponto.dto.PolicyDocumentResponseDTO;
import com.ponto.service.ComplianceAuditService;
import com.ponto.service.ComplianceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;
    private final ComplianceAuditService complianceAuditService;

    @PostMapping("/policies")
    public ResponseEntity<PolicyDocumentResponseDTO> createPolicy(@Valid @RequestBody PolicyDocumentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complianceService.createPolicy(request));
    }

    @GetMapping("/policies/company")
    public ResponseEntity<List<PolicyDocumentResponseDTO>> listCompanyPolicies() {
        return ResponseEntity.ok(complianceService.listCompanyPolicies());
    }

    @GetMapping("/policies/me")
    public ResponseEntity<List<PolicyDocumentResponseDTO>> listEmployeePolicies() {
        return ResponseEntity.ok(complianceService.listEmployeePolicies());
    }

    @PostMapping("/policies/{policyId}/ack")
    public ResponseEntity<PolicyAcknowledgmentResponseDTO> acknowledgePolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(complianceService.acknowledgePolicy(policyId));
    }

    @GetMapping("/acks/me")
    public ResponseEntity<List<PolicyAcknowledgmentResponseDTO>> listOwnAcknowledgments() {
        return ResponseEntity.ok(complianceService.listOwnAcknowledgments());
    }

    @GetMapping("/acks/company")
    public ResponseEntity<List<PolicyAcknowledgmentResponseDTO>> listCompanyAcknowledgments(@RequestParam(required = false) Boolean onlyPending) {
        return ResponseEntity.ok(complianceService.listCompanyAcknowledgments(onlyPending));
    }

    @GetMapping("/audit-events")
    public ResponseEntity<List<ComplianceAuditEventResponseDTO>> listCompanyAuditEvents() {
        return ResponseEntity.ok(complianceAuditService.listCompanyEvents());
    }
}
