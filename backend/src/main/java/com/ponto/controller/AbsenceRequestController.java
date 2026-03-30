package com.ponto.controller;

import com.ponto.dto.AbsenceRequestCreateDTO;
import com.ponto.dto.AbsenceRequestResponseDTO;
import com.ponto.dto.ApprovalDecisionDTO;
import com.ponto.service.AbsenceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
public class AbsenceRequestController {

    private final AbsenceRequestService absenceRequestService;

    @PostMapping
    public ResponseEntity<AbsenceRequestResponseDTO> create(@Valid @RequestBody AbsenceRequestCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(absenceRequestService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AbsenceRequestResponseDTO>> listOwn() {
        return ResponseEntity.ok(absenceRequestService.listOwn());
    }

    @GetMapping("/company")
    public ResponseEntity<List<AbsenceRequestResponseDTO>> listCompany(@RequestParam(required = false) Boolean onlyPending) {
        return ResponseEntity.ok(absenceRequestService.listCompany(onlyPending));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<AbsenceRequestResponseDTO> approve(@PathVariable Long id, @RequestBody(required = false) ApprovalDecisionDTO decision) {
        return ResponseEntity.ok(absenceRequestService.approve(id, decision));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<AbsenceRequestResponseDTO> reject(@PathVariable Long id, @RequestBody(required = false) ApprovalDecisionDTO decision) {
        return ResponseEntity.ok(absenceRequestService.reject(id, decision));
    }
}
