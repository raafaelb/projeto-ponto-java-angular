package com.ponto.service;

import com.ponto.dto.PolicyAcknowledgmentResponseDTO;
import com.ponto.dto.PolicyDocumentRequestDTO;
import com.ponto.dto.PolicyDocumentResponseDTO;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PolicyAcknowledgmentRepository;
import com.ponto.repository.PolicyDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceService {

    private final PolicyDocumentRepository policyDocumentRepository;
    private final PolicyAcknowledgmentRepository policyAcknowledgmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;
    private final ComplianceAuditService complianceAuditService;

    public PolicyDocumentResponseDTO createPolicy(PolicyDocumentRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        PolicyDocument policy = new PolicyDocument();
        policy.setCompany(currentUser.getCompany());
        policy.setTitle(request.getTitle().trim());
        policy.setVersion(request.getVersion().trim());
        policy.setEffectiveDate(request.getEffectiveDate());
        policy.setContentSummary(sanitizeOptional(request.getContentSummary()));
        policy.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        policy = policyDocumentRepository.save(policy);

        complianceAuditService.log(
                currentUser.getCompany(),
                "POLICY_CREATED",
                "Politica corporativa criada",
                "INFO",
                "PolicyDocument",
                policy.getId(),
                currentUser
        );

        return toPolicyResponse(policy);
    }

    @Transactional(readOnly = true)
    public List<PolicyDocumentResponseDTO> listCompanyPolicies() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return policyDocumentRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PolicyDocumentResponseDTO> listEmployeePolicies() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        if (currentUser.getCompany() == null) {
            throw new BusinessException("Usuario sem empresa vinculada");
        }
        return policyDocumentRepository.findAllByCompanyIdAndActiveTrueOrderByEffectiveDateDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    public PolicyAcknowledgmentResponseDTO acknowledgePolicy(Long policyId) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));

        PolicyDocument policy = policyDocumentRepository.findByIdAndCompanyId(policyId, employee.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Politica nao encontrada"));

        PolicyAcknowledgment acknowledgment = policyAcknowledgmentRepository
                .findByPolicyDocumentIdAndEmployeeId(policyId, employee.getId())
                .orElseGet(() -> {
                    PolicyAcknowledgment created = new PolicyAcknowledgment();
                    created.setPolicyDocument(policy);
                    created.setEmployee(employee);
                    created.setCompany(employee.getCompany());
                    return created;
                });

        acknowledgment.setStatus(PolicyAckStatus.ACKNOWLEDGED);
        acknowledgment.setAcknowledgedAt(LocalDateTime.now());
        acknowledgment = policyAcknowledgmentRepository.save(acknowledgment);

        complianceAuditService.log(
                employee.getCompany(),
                "POLICY_ACKNOWLEDGED",
                "Politica confirmada por funcionario",
                "INFO",
                "PolicyAcknowledgment",
                acknowledgment.getId(),
                currentUser
        );

        return toAckResponse(acknowledgment);
    }

    @Transactional(readOnly = true)
    public List<PolicyAcknowledgmentResponseDTO> listOwnAcknowledgments() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));
        return policyAcknowledgmentRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toAckResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PolicyAcknowledgmentResponseDTO> listCompanyAcknowledgments(Boolean onlyPending) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        Long companyId = currentUser.getCompany().getId();

        List<PolicyAcknowledgment> list = Boolean.TRUE.equals(onlyPending)
                ? policyAcknowledgmentRepository.findAllByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, PolicyAckStatus.REQUIRED)
                : policyAcknowledgmentRepository.findAllByCompanyIdOrderByCreatedAtDesc(companyId);

        return list.stream().map(this::toAckResponse).toList();
    }

    private PolicyDocumentResponseDTO toPolicyResponse(PolicyDocument policy) {
        PolicyDocumentResponseDTO dto = new PolicyDocumentResponseDTO();
        dto.setId(policy.getId());
        dto.setTitle(policy.getTitle());
        dto.setVersion(policy.getVersion());
        dto.setEffectiveDate(policy.getEffectiveDate());
        dto.setContentSummary(policy.getContentSummary());
        dto.setActive(policy.getActive());
        dto.setCreatedAt(policy.getCreatedAt());
        return dto;
    }

    private PolicyAcknowledgmentResponseDTO toAckResponse(PolicyAcknowledgment ack) {
        PolicyAcknowledgmentResponseDTO dto = new PolicyAcknowledgmentResponseDTO();
        dto.setId(ack.getId());
        dto.setPolicyDocumentId(ack.getPolicyDocument().getId());
        dto.setPolicyTitle(ack.getPolicyDocument().getTitle());
        dto.setPolicyVersion(ack.getPolicyDocument().getVersion());
        dto.setStatus(ack.getStatus());
        dto.setAcknowledgedAt(ack.getAcknowledgedAt());
        dto.setCreatedAt(ack.getCreatedAt());
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
