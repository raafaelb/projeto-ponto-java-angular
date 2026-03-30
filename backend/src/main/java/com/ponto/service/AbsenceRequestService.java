package com.ponto.service;

import com.ponto.dto.AbsenceRequestCreateDTO;
import com.ponto.dto.AbsenceRequestResponseDTO;
import com.ponto.dto.ApprovalDecisionDTO;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.AbsenceRequestRepository;
import com.ponto.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AbsenceRequestService {

    private final AbsenceRequestRepository absenceRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public AbsenceRequestResponseDTO create(AbsenceRequestCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("Data final nao pode ser anterior a data inicial");
        }

        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));

        AbsenceRequest entity = new AbsenceRequest();
        entity.setEmployee(employee);
        entity.setCompany(employee.getCompany());
        entity.setType(request.getType());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setReason(request.getReason());

        return toResponse(absenceRequestRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AbsenceRequestResponseDTO> listOwn() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));

        return absenceRequestRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AbsenceRequestResponseDTO> listCompany(Boolean onlyPending) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();

        List<AbsenceRequest> requests = Boolean.TRUE.equals(onlyPending)
                ? absenceRequestRepository.findAllByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, ApprovalStatus.PENDING)
                : absenceRequestRepository.findAllByCompanyIdOrderByCreatedAtDesc(companyId);

        return requests.stream().map(this::toResponse).toList();
    }

    public AbsenceRequestResponseDTO approve(Long id, ApprovalDecisionDTO decision) {
        return review(id, decision, ApprovalStatus.APPROVED);
    }

    public AbsenceRequestResponseDTO reject(Long id, ApprovalDecisionDTO decision) {
        return review(id, decision, ApprovalStatus.REJECTED);
    }

    private AbsenceRequestResponseDTO review(Long id, ApprovalDecisionDTO decision, ApprovalStatus status) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        AbsenceRequest request = absenceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de ausencia nao encontrada"));

        currentUserService.validateCompanyAccess(currentUser, request.getCompany().getId());

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("Solicitacao ja revisada anteriormente");
        }

        request.setStatus(status);
        request.setReviewedBy(currentUser);
        request.setReviewedAt(java.time.LocalDateTime.now());
        request.setReviewComment(decision != null ? decision.getComment() : null);

        return toResponse(absenceRequestRepository.save(request));
    }

    private AbsenceRequestResponseDTO toResponse(AbsenceRequest entity) {
        AbsenceRequestResponseDTO dto = new AbsenceRequestResponseDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setType(entity.getType());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setReviewComment(entity.getReviewComment());
        dto.setReviewedBy(entity.getReviewedBy() != null ? entity.getReviewedBy().getName() : null);
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public long calculateAbsenceDaysInRange(Long companyId, Long employeeId, java.time.LocalDate start, java.time.LocalDate end) {
        return absenceRequestRepository.findAllByCompanyIdAndStartDateBetweenOrderByStartDateAsc(companyId, start, end)
                .stream()
                .filter(r -> r.getStatus() == ApprovalStatus.APPROVED && r.getEmployee().getId().equals(employeeId))
                .mapToLong(r -> ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()) + 1)
                .sum();
    }
}
