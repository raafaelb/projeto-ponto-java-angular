package com.ponto.service;

import com.ponto.dto.*;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.BonusRequestRepository;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.SalaryAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompensationService {

    private final SalaryAdjustmentRepository salaryAdjustmentRepository;
    private final BonusRequestRepository bonusRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public SalaryAdjustmentResponseDTO createSalaryAdjustment(SalaryAdjustmentCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        Employee employee = employeeRepository.findByIdAndCompanyId(request.getEmployeeId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        SalaryAdjustment adjustment = new SalaryAdjustment();
        adjustment.setEmployee(employee);
        adjustment.setCompany(employee.getCompany());
        adjustment.setPreviousSalary(employee.getCurrentSalary() == null ? BigDecimal.ZERO : employee.getCurrentSalary());
        adjustment.setNewSalary(request.getNewSalary());
        adjustment.setEffectiveDate(request.getEffectiveDate());
        adjustment.setReason(sanitizeOptional(request.getReason()));
        adjustment.setStatus(ApprovalStatus.PENDING);

        return toSalaryResponse(salaryAdjustmentRepository.save(adjustment));
    }

    @Transactional(readOnly = true)
    public List<SalaryAdjustmentResponseDTO> listCompanySalaryAdjustments() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return salaryAdjustmentRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toSalaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalaryAdjustmentResponseDTO> listOwnSalaryAdjustments() {
        Employee employee = resolveCurrentEmployee();
        return salaryAdjustmentRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toSalaryResponse)
                .toList();
    }

    public SalaryAdjustmentResponseDTO approveSalaryAdjustment(Long id, ApprovalDecisionDTO decision) {
        return reviewSalaryAdjustment(id, decision, ApprovalStatus.APPROVED);
    }

    public SalaryAdjustmentResponseDTO rejectSalaryAdjustment(Long id, ApprovalDecisionDTO decision) {
        return reviewSalaryAdjustment(id, decision, ApprovalStatus.REJECTED);
    }

    private SalaryAdjustmentResponseDTO reviewSalaryAdjustment(Long id, ApprovalDecisionDTO decision, ApprovalStatus status) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        SalaryAdjustment adjustment = salaryAdjustmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste salarial nao encontrado"));
        currentUserService.validateCompanyAccess(currentUser, adjustment.getCompany().getId());

        if (adjustment.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("Ajuste salarial ja revisado anteriormente");
        }

        adjustment.setStatus(status);
        adjustment.setReviewComment(decision != null ? sanitizeOptional(decision.getComment()) : null);
        adjustment.setReviewedBy(currentUser);
        adjustment.setReviewedAt(LocalDateTime.now());

        if (status == ApprovalStatus.APPROVED) {
            Employee employee = adjustment.getEmployee();
            employee.setCurrentSalary(adjustment.getNewSalary());
            employeeRepository.save(employee);
        }

        return toSalaryResponse(salaryAdjustmentRepository.save(adjustment));
    }

    public BonusRequestResponseDTO createBonusRequest(BonusRequestCreateDTO request) {
        Employee employee = resolveCurrentEmployee();

        BonusRequest bonusRequest = new BonusRequest();
        bonusRequest.setEmployee(employee);
        bonusRequest.setCompany(employee.getCompany());
        bonusRequest.setAmount(request.getAmount());
        bonusRequest.setReferenceDate(request.getReferenceDate());
        bonusRequest.setReason(sanitizeOptional(request.getReason()));
        bonusRequest.setStatus(ApprovalStatus.PENDING);

        return toBonusResponse(bonusRequestRepository.save(bonusRequest));
    }

    @Transactional(readOnly = true)
    public List<BonusRequestResponseDTO> listOwnBonusRequests() {
        Employee employee = resolveCurrentEmployee();
        return bonusRequestRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toBonusResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BonusRequestResponseDTO> listCompanyBonusRequests() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return bonusRequestRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toBonusResponse)
                .toList();
    }

    public BonusRequestResponseDTO approveBonusRequest(Long id, ApprovalDecisionDTO decision) {
        return reviewBonusRequest(id, decision, ApprovalStatus.APPROVED);
    }

    public BonusRequestResponseDTO rejectBonusRequest(Long id, ApprovalDecisionDTO decision) {
        return reviewBonusRequest(id, decision, ApprovalStatus.REJECTED);
    }

    private BonusRequestResponseDTO reviewBonusRequest(Long id, ApprovalDecisionDTO decision, ApprovalStatus status) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        BonusRequest request = bonusRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de bonus nao encontrada"));
        currentUserService.validateCompanyAccess(currentUser, request.getCompany().getId());

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("Solicitacao de bonus ja revisada anteriormente");
        }

        request.setStatus(status);
        request.setReviewComment(decision != null ? sanitizeOptional(decision.getComment()) : null);
        request.setReviewedBy(currentUser);
        request.setReviewedAt(LocalDateTime.now());

        return toBonusResponse(bonusRequestRepository.save(request));
    }

    private Employee resolveCurrentEmployee() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        return employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));
    }

    private SalaryAdjustmentResponseDTO toSalaryResponse(SalaryAdjustment entity) {
        SalaryAdjustmentResponseDTO dto = new SalaryAdjustmentResponseDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setPreviousSalary(entity.getPreviousSalary());
        dto.setNewSalary(entity.getNewSalary());
        dto.setEffectiveDate(entity.getEffectiveDate());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setReviewComment(entity.getReviewComment());
        dto.setReviewedBy(entity.getReviewedBy() != null ? entity.getReviewedBy().getName() : null);
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private BonusRequestResponseDTO toBonusResponse(BonusRequest entity) {
        BonusRequestResponseDTO dto = new BonusRequestResponseDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setAmount(entity.getAmount());
        dto.setReferenceDate(entity.getReferenceDate());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setReviewComment(entity.getReviewComment());
        dto.setReviewedBy(entity.getReviewedBy() != null ? entity.getReviewedBy().getName() : null);
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private String sanitizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim();
        return sanitized.isBlank() ? null : sanitized;
    }
}
