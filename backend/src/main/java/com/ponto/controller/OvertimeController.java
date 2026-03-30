package com.ponto.controller;

import com.ponto.dto.ApprovalDecisionDTO;
import com.ponto.dto.OvertimeCreateDTO;
import com.ponto.dto.OvertimeResponseDTO;
import com.ponto.service.OvertimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    @PostMapping
    public ResponseEntity<OvertimeResponseDTO> create(@Valid @RequestBody OvertimeCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(overtimeService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<OvertimeResponseDTO>> listOwn() {
        return ResponseEntity.ok(overtimeService.listOwn());
    }

    @GetMapping("/company")
    public ResponseEntity<List<OvertimeResponseDTO>> listCompany(@RequestParam(required = false) Boolean onlyPending) {
        return ResponseEntity.ok(overtimeService.listCompany(onlyPending));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<OvertimeResponseDTO> approve(@PathVariable Long id, @RequestBody(required = false) ApprovalDecisionDTO decision) {
        return ResponseEntity.ok(overtimeService.approve(id, decision));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<OvertimeResponseDTO> reject(@PathVariable Long id, @RequestBody(required = false) ApprovalDecisionDTO decision) {
        return ResponseEntity.ok(overtimeService.reject(id, decision));
    }
}
