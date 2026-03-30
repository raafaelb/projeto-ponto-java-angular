package com.ponto.controller;

import com.ponto.dto.*;
import com.ponto.service.BenefitsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benefits")
@RequiredArgsConstructor
public class BenefitsController {

    private final BenefitsService benefitsService;

    @PostMapping("/plans")
    public ResponseEntity<BenefitPlanResponseDTO> createPlan(@Valid @RequestBody BenefitPlanRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(benefitsService.createPlan(request));
    }

    @GetMapping("/plans/company")
    public ResponseEntity<List<BenefitPlanResponseDTO>> listCompanyPlans() {
        return ResponseEntity.ok(benefitsService.listCompanyPlans());
    }

    @GetMapping("/plans/me")
    public ResponseEntity<List<BenefitPlanResponseDTO>> listEmployeeAvailablePlans() {
        return ResponseEntity.ok(benefitsService.listEmployeeAvailablePlans());
    }

    @PostMapping("/enrollments")
    public ResponseEntity<BenefitEnrollmentResponseDTO> requestEnrollment(@Valid @RequestBody BenefitEnrollmentCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(benefitsService.requestEnrollment(request));
    }

    @GetMapping("/enrollments/me")
    public ResponseEntity<List<BenefitEnrollmentResponseDTO>> listOwnEnrollments() {
        return ResponseEntity.ok(benefitsService.listOwnEnrollments());
    }

    @GetMapping("/enrollments/company")
    public ResponseEntity<List<BenefitEnrollmentResponseDTO>> listCompanyEnrollments(@RequestParam(required = false) Boolean onlyPending) {
        return ResponseEntity.ok(benefitsService.listCompanyEnrollments(onlyPending));
    }

    @PostMapping("/enrollments/{id}/approve")
    public ResponseEntity<BenefitEnrollmentResponseDTO> approveEnrollment(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(benefitsService.approveEnrollment(id, decision));
    }

    @PostMapping("/enrollments/{id}/cancel")
    public ResponseEntity<BenefitEnrollmentResponseDTO> cancelEnrollment(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(benefitsService.cancelEnrollment(id, decision));
    }
}
