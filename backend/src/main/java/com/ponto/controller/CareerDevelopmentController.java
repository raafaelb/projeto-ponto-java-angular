package com.ponto.controller;

import com.ponto.dto.*;
import com.ponto.service.CareerDevelopmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career")
@RequiredArgsConstructor
public class CareerDevelopmentController {

    private final CareerDevelopmentService careerDevelopmentService;

    @PostMapping("/levels")
    public ResponseEntity<CareerLevelResponseDTO> createCareerLevel(@Valid @RequestBody CareerLevelRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(careerDevelopmentService.createCareerLevel(request));
    }

    @GetMapping("/levels")
    public ResponseEntity<List<CareerLevelResponseDTO>> listCareerLevels() {
        return ResponseEntity.ok(careerDevelopmentService.listCareerLevels());
    }

    @DeleteMapping("/levels/{id}")
    public ResponseEntity<Void> deleteCareerLevel(@PathVariable Long id) {
        careerDevelopmentService.deleteCareerLevel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/levels/{levelId}/assign/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> assignEmployeeLevel(
            @PathVariable Long levelId,
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(careerDevelopmentService.assignEmployeeLevel(employeeId, levelId));
    }

    @PostMapping("/skills")
    public ResponseEntity<SkillAssessmentResponseDTO> createSkillAssessment(@Valid @RequestBody SkillAssessmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(careerDevelopmentService.createSkillAssessment(request));
    }

    @GetMapping("/skills/company")
    public ResponseEntity<List<SkillAssessmentResponseDTO>> listCompanySkills() {
        return ResponseEntity.ok(careerDevelopmentService.listCompanySkills());
    }

    @GetMapping("/skills/me")
    public ResponseEntity<List<SkillAssessmentResponseDTO>> listOwnSkills() {
        return ResponseEntity.ok(careerDevelopmentService.listOwnSkills());
    }

    @PostMapping("/promotions")
    public ResponseEntity<PromotionRequestResponseDTO> createPromotionRequest(@Valid @RequestBody PromotionRequestCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(careerDevelopmentService.createPromotionRequest(request));
    }

    @GetMapping("/promotions/me")
    public ResponseEntity<List<PromotionRequestResponseDTO>> listOwnPromotionRequests() {
        return ResponseEntity.ok(careerDevelopmentService.listOwnPromotionRequests());
    }

    @GetMapping("/promotions/company")
    public ResponseEntity<List<PromotionRequestResponseDTO>> listCompanyPromotionRequests() {
        return ResponseEntity.ok(careerDevelopmentService.listCompanyPromotionRequests());
    }

    @PostMapping("/promotions/{id}/approve")
    public ResponseEntity<PromotionRequestResponseDTO> approvePromotionRequest(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(careerDevelopmentService.approvePromotionRequest(id, decision));
    }

    @PostMapping("/promotions/{id}/reject")
    public ResponseEntity<PromotionRequestResponseDTO> rejectPromotionRequest(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalDecisionDTO decision
    ) {
        return ResponseEntity.ok(careerDevelopmentService.rejectPromotionRequest(id, decision));
    }
}
