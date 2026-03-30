package com.ponto.service;

import com.ponto.dto.*;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PayrollCycleRepository;
import com.ponto.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollService {

    private final PayrollCycleRepository payrollCycleRepository;
    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;
    private final ComplianceAuditService complianceAuditService;

    public PayrollCycleResponseDTO createCycle(PayrollCycleCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        if (request.getPeriodEnd().isBefore(request.getPeriodStart())) {
            throw new BusinessException("Periodo final nao pode ser anterior ao periodo inicial");
        }

        PayrollCycle cycle = new PayrollCycle();
        cycle.setCompany(currentUser.getCompany());
        cycle.setPeriodStart(request.getPeriodStart());
        cycle.setPeriodEnd(request.getPeriodEnd());
        cycle.setPaymentDate(request.getPaymentDate());
        cycle.setNotes(sanitizeOptional(request.getNotes()));
        cycle.setStatus(PayrollCycleStatus.DRAFT);
        cycle = payrollCycleRepository.save(cycle);

        complianceAuditService.log(
                currentUser.getCompany(),
                "PAYROLL_CYCLE_CREATED",
                "Ciclo de folha criado",
                "INFO",
                "PayrollCycle",
                cycle.getId(),
                currentUser
        );

        return toCycleResponse(cycle);
    }

    @Transactional(readOnly = true)
    public List<PayrollCycleResponseDTO> listCycles() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return payrollCycleRepository.findAllByCompanyIdOrderByPeriodStartDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toCycleResponse)
                .toList();
    }

    public PayrollCycleResponseDTO closeCycle(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        PayrollCycle cycle = payrollCycleRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ciclo de folha nao encontrado"));

        cycle.setStatus(PayrollCycleStatus.CLOSED);
        cycle = payrollCycleRepository.save(cycle);

        complianceAuditService.log(
                currentUser.getCompany(),
                "PAYROLL_CYCLE_CLOSED",
                "Ciclo de folha fechado",
                "INFO",
                "PayrollCycle",
                cycle.getId(),
                currentUser
        );

        return toCycleResponse(cycle);
    }

    public PayslipResponseDTO createPayslip(PayslipCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        Long companyId = currentUser.getCompany().getId();

        PayrollCycle cycle = payrollCycleRepository.findByIdAndCompanyId(request.getPayrollCycleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Ciclo de folha nao encontrado"));
        Employee employee = employeeRepository.findByIdAndCompanyId(request.getEmployeeId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        Payslip payslip = new Payslip();
        payslip.setPayrollCycle(cycle);
        payslip.setEmployee(employee);
        payslip.setCompany(employee.getCompany());
        payslip.setGrossPay(request.getGrossPay());
        payslip.setDeductions(request.getDeductions());
        payslip.setTaxWithheld(request.getTaxWithheld());
        payslip.setOvertimePay(request.getOvertimePay());
        payslip.setBonusPay(request.getBonusPay());
        payslip.setNetPay(
                safe(request.getGrossPay())
                        .add(safe(request.getOvertimePay()))
                        .add(safe(request.getBonusPay()))
                        .subtract(safe(request.getDeductions()))
                        .subtract(safe(request.getTaxWithheld()))
        );
        payslip = payslipRepository.save(payslip);

        complianceAuditService.log(
                currentUser.getCompany(),
                "PAYSLIP_GENERATED",
                "Holerite gerado para funcionario",
                "INFO",
                "Payslip",
                payslip.getId(),
                currentUser
        );

        return toPayslipResponse(payslip);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDTO> listCompanyPayslips() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return payslipRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toPayslipResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDTO> listOwnPayslips() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));
        return payslipRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toPayslipResponse)
                .toList();
    }

    private PayrollCycleResponseDTO toCycleResponse(PayrollCycle cycle) {
        PayrollCycleResponseDTO dto = new PayrollCycleResponseDTO();
        dto.setId(cycle.getId());
        dto.setPeriodStart(cycle.getPeriodStart());
        dto.setPeriodEnd(cycle.getPeriodEnd());
        dto.setPaymentDate(cycle.getPaymentDate());
        dto.setStatus(cycle.getStatus());
        dto.setNotes(cycle.getNotes());
        dto.setCreatedAt(cycle.getCreatedAt());
        return dto;
    }

    private PayslipResponseDTO toPayslipResponse(Payslip payslip) {
        PayslipResponseDTO dto = new PayslipResponseDTO();
        dto.setId(payslip.getId());
        dto.setPayrollCycleId(payslip.getPayrollCycle().getId());
        dto.setEmployeeId(payslip.getEmployee().getId());
        dto.setEmployeeName(payslip.getEmployee().getName());
        dto.setGrossPay(payslip.getGrossPay());
        dto.setDeductions(payslip.getDeductions());
        dto.setTaxWithheld(payslip.getTaxWithheld());
        dto.setOvertimePay(payslip.getOvertimePay());
        dto.setBonusPay(payslip.getBonusPay());
        dto.setNetPay(payslip.getNetPay());
        dto.setCreatedAt(payslip.getCreatedAt());
        return dto;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String sanitizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim();
        return sanitized.isBlank() ? null : sanitized;
    }
}
