package com.ponto.controller;

import com.ponto.dto.*;
import com.ponto.service.CompensationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compensation")
@RequiredArgsConstructor
public class CompensationController {

    private final CompensationService compensationService;

    @PostMapping("/salary-adjustments")
    public ResponseEntity<SalaryAdjustmentResponseDTO> createSalaryAdjustment(@Valid @RequestBody SalaryAdjustmentCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compensationService.createSalaryAdjustment(request));
    }

    @GetMapping("/salary-adjustments/company")
    public ResponseEntity<List<SalaryAdjustmentResponseDTO>> listCompanySalaryAdjustments() {
        return ResponseEntity.ok(compensationService.listCompanySalaryAdjustments());
    }

    @GetMapping("/salary-adjustments/me")
    public ResponseEntity<List<SalaryAdjustmentResponseDTO>> listOwnSalaryAdjustments() {
        return ResponseEntity.ok(compensationService.listOwnSalaryAdjustments());
    }

    @PostMapping("/salary-adjustments/{id}/approve")
    public ResponseEntity<SalaryAdjustmentResponseDTO> approveSalaryAdjustment(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(compensationService.approveSalaryAdjustment(id, decision));
    }

    @PostMapping("/salary-adjustments/{id}/reject")
    public ResponseEntity<SalaryAdjustmentResponseDTO> rejectSalaryAdjustment(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(compensationService.rejectSalaryAdjustment(id, decision));
    }

    @PostMapping("/bonus-requests")
    public ResponseEntity<BonusRequestResponseDTO> createBonusRequest(@Valid @RequestBody BonusRequestCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compensationService.createBonusRequest(request));
    }

    @GetMapping("/bonus-requests/me")
    public ResponseEntity<List<BonusRequestResponseDTO>> listOwnBonusRequests() {
        return ResponseEntity.ok(compensationService.listOwnBonusRequests());
    }

    @GetMapping("/bonus-requests/company")
    public ResponseEntity<List<BonusRequestResponseDTO>> listCompanyBonusRequests() {
        return ResponseEntity.ok(compensationService.listCompanyBonusRequests());
    }

    @PostMapping("/bonus-requests/{id}/approve")
    public ResponseEntity<BonusRequestResponseDTO> approveBonusRequest(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(compensationService.approveBonusRequest(id, decision));
    }

    @PostMapping("/bonus-requests/{id}/reject")
    public ResponseEntity<BonusRequestResponseDTO> rejectBonusRequest(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(compensationService.rejectBonusRequest(id, decision));
    }
}
