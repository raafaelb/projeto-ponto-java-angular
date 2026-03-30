package com.ponto.service;

import com.ponto.dto.*;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.BenefitEnrollmentRepository;
import com.ponto.repository.BenefitPlanRepository;
import com.ponto.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitsService {

    private final BenefitPlanRepository benefitPlanRepository;
    private final BenefitEnrollmentRepository benefitEnrollmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;
    private final ComplianceAuditService complianceAuditService;

    public BenefitPlanResponseDTO createPlan(BenefitPlanRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        BenefitPlan plan = new BenefitPlan();
        plan.setCompany(currentUser.getCompany());
        plan.setName(request.getName().trim());
        plan.setDescription(sanitizeOptional(request.getDescription()));
        plan.setMonthlyEmployerCost(request.getMonthlyEmployerCost());
        plan.setMonthlyEmployeeCost(request.getMonthlyEmployeeCost());
        plan.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        plan = benefitPlanRepository.save(plan);

        complianceAuditService.log(
                currentUser.getCompany(),
                "BENEFIT_PLAN_CREATED",
                "Plano de beneficio criado",
                "INFO",
                "BenefitPlan",
                plan.getId(),
                currentUser
        );

        return toPlanResponse(plan);
    }

    @Transactional(readOnly = true)
    public List<BenefitPlanResponseDTO> listCompanyPlans() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return benefitPlanRepository.findAllByCompanyIdOrderByNameAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toPlanResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BenefitPlanResponseDTO> listEmployeeAvailablePlans() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        if (currentUser.getCompany() == null) {
            throw new BusinessException("Usuario sem empresa vinculada");
        }
        return benefitPlanRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toPlanResponse)
                .toList();
    }

    public BenefitEnrollmentResponseDTO requestEnrollment(BenefitEnrollmentCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));

        BenefitPlan plan = benefitPlanRepository.findByIdAndCompanyId(request.getBenefitPlanId(), employee.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano de beneficio nao encontrado"));

        BenefitEnrollment enrollment = new BenefitEnrollment();
        enrollment.setBenefitPlan(plan);
        enrollment.setEmployee(employee);
        enrollment.setCompany(employee.getCompany());
        enrollment.setStatus(BenefitEnrollmentStatus.PENDING);
        enrollment = benefitEnrollmentRepository.save(enrollment);

        complianceAuditService.log(
                employee.getCompany(),
                "BENEFIT_ENROLLMENT_REQUESTED",
                "Solicitacao de adesao a beneficio criada",
                "INFO",
                "BenefitEnrollment",
                enrollment.getId(),
                currentUser
        );

        return toEnrollmentResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<BenefitEnrollmentResponseDTO> listOwnEnrollments() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));
        return benefitEnrollmentRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BenefitEnrollmentResponseDTO> listCompanyEnrollments(Boolean onlyPending) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        Long companyId = currentUser.getCompany().getId();

        List<BenefitEnrollment> list = Boolean.TRUE.equals(onlyPending)
                ? benefitEnrollmentRepository.findAllByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, BenefitEnrollmentStatus.PENDING)
                : benefitEnrollmentRepository.findAllByCompanyIdOrderByCreatedAtDesc(companyId);

        return list.stream().map(this::toEnrollmentResponse).toList();
    }

    public BenefitEnrollmentResponseDTO approveEnrollment(Long id, ApprovalDecisionDTO decision) {
        return reviewEnrollment(id, decision, BenefitEnrollmentStatus.ENROLLED);
    }

    public BenefitEnrollmentResponseDTO cancelEnrollment(Long id, ApprovalDecisionDTO decision) {
        return reviewEnrollment(id, decision, BenefitEnrollmentStatus.CANCELLED);
    }

    private BenefitEnrollmentResponseDTO reviewEnrollment(Long id, ApprovalDecisionDTO decision, BenefitEnrollmentStatus status) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        BenefitEnrollment enrollment = benefitEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de beneficio nao encontrada"));
        currentUserService.validateCompanyAccess(currentUser, enrollment.getCompany().getId());

        if (enrollment.getStatus() != BenefitEnrollmentStatus.PENDING) {
            throw new BusinessException("Solicitacao ja revisada anteriormente");
        }

        enrollment.setStatus(status);
        enrollment.setStartDate(status == BenefitEnrollmentStatus.ENROLLED ? LocalDate.now() : null);
        enrollment.setEndDate(status == BenefitEnrollmentStatus.CANCELLED ? LocalDate.now() : null);
        enrollment.setReviewComment(decision != null ? sanitizeOptional(decision.getComment()) : null);
        enrollment.setReviewedBy(currentUser);
        enrollment.setReviewedAt(LocalDateTime.now());
        enrollment = benefitEnrollmentRepository.save(enrollment);

        complianceAuditService.log(
                currentUser.getCompany(),
                status == BenefitEnrollmentStatus.ENROLLED ? "BENEFIT_ENROLLMENT_APPROVED" : "BENEFIT_ENROLLMENT_CANCELLED",
                "Solicitacao de beneficio revisada",
                "INFO",
                "BenefitEnrollment",
                enrollment.getId(),
                currentUser
        );

        return toEnrollmentResponse(enrollment);
    }

    private BenefitPlanResponseDTO toPlanResponse(BenefitPlan plan) {
        BenefitPlanResponseDTO dto = new BenefitPlanResponseDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setMonthlyEmployerCost(plan.getMonthlyEmployerCost());
        dto.setMonthlyEmployeeCost(plan.getMonthlyEmployeeCost());
        dto.setActive(plan.getActive());
        dto.setCreatedAt(plan.getCreatedAt());
        return dto;
    }

    private BenefitEnrollmentResponseDTO toEnrollmentResponse(BenefitEnrollment enrollment) {
        BenefitEnrollmentResponseDTO dto = new BenefitEnrollmentResponseDTO();
        dto.setId(enrollment.getId());
        dto.setBenefitPlanId(enrollment.getBenefitPlan().getId());
        dto.setBenefitPlanName(enrollment.getBenefitPlan().getName());
        dto.setEmployeeId(enrollment.getEmployee().getId());
        dto.setEmployeeName(enrollment.getEmployee().getName());
        dto.setStatus(enrollment.getStatus());
        dto.setStartDate(enrollment.getStartDate());
        dto.setEndDate(enrollment.getEndDate());
        dto.setReviewComment(enrollment.getReviewComment());
        dto.setReviewedBy(enrollment.getReviewedBy() != null ? enrollment.getReviewedBy().getName() : null);
        dto.setReviewedAt(enrollment.getReviewedAt());
        dto.setCreatedAt(enrollment.getCreatedAt());
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
