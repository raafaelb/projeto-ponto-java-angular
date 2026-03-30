package com.ponto.controller;

import com.ponto.dto.*;
import com.ponto.service.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping("/goals/employee/{employeeId}")
    public ResponseEntity<PerformanceGoalResponseDTO> createGoal(
            @PathVariable Long employeeId,
            @Valid @RequestBody PerformanceGoalRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.createGoal(employeeId, request));
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<PerformanceGoalResponseDTO> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceGoalRequestDTO request
    ) {
        return ResponseEntity.ok(performanceService.updateGoal(id, request));
    }

    @GetMapping("/goals/company")
    public ResponseEntity<List<PerformanceGoalResponseDTO>> listCompanyGoals() {
        return ResponseEntity.ok(performanceService.listCompanyGoals());
    }

    @GetMapping("/goals/me")
    public ResponseEntity<List<PerformanceGoalResponseDTO>> listOwnGoals() {
        return ResponseEntity.ok(performanceService.listOwnGoals());
    }

    @PostMapping("/reviews")
    public ResponseEntity<PerformanceReviewResponseDTO> createReview(@Valid @RequestBody PerformanceReviewCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.createReview(request));
    }

    @PostMapping("/reviews/{id}/self")
    public ResponseEntity<PerformanceReviewResponseDTO> submitSelfReview(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewSelfDTO request
    ) {
        return ResponseEntity.ok(performanceService.submitSelfReview(id, request));
    }

    @PostMapping("/reviews/{id}/manager")
    public ResponseEntity<PerformanceReviewResponseDTO> submitManagerReview(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewManagerDTO request
    ) {
        return ResponseEntity.ok(performanceService.submitManagerReview(id, request));
    }

    @GetMapping("/reviews/company")
    public ResponseEntity<List<PerformanceReviewResponseDTO>> listCompanyReviews() {
        return ResponseEntity.ok(performanceService.listCompanyReviews());
    }

    @GetMapping("/reviews/me")
    public ResponseEntity<List<PerformanceReviewResponseDTO>> listOwnReviews() {
        return ResponseEntity.ok(performanceService.listOwnReviews());
    }
}
