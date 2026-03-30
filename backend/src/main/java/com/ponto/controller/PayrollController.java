package com.ponto.controller;

import com.ponto.dto.*;
import com.ponto.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/cycles")
    public ResponseEntity<PayrollCycleResponseDTO> createCycle(@Valid @RequestBody PayrollCycleCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.createCycle(request));
    }

    @GetMapping("/cycles")
    public ResponseEntity<List<PayrollCycleResponseDTO>> listCycles() {
        return ResponseEntity.ok(payrollService.listCycles());
    }

    @PostMapping("/cycles/{id}/close")
    public ResponseEntity<PayrollCycleResponseDTO> closeCycle(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.closeCycle(id));
    }

    @PostMapping("/payslips")
    public ResponseEntity<PayslipResponseDTO> createPayslip(@Valid @RequestBody PayslipCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.createPayslip(request));
    }

    @GetMapping("/payslips/company")
    public ResponseEntity<List<PayslipResponseDTO>> listCompanyPayslips() {
        return ResponseEntity.ok(payrollService.listCompanyPayslips());
    }

    @GetMapping("/payslips/me")
    public ResponseEntity<List<PayslipResponseDTO>> listOwnPayslips() {
        return ResponseEntity.ok(payrollService.listOwnPayslips());
    }
}
