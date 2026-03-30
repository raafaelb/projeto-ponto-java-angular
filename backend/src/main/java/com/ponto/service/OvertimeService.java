package com.ponto.service;

import com.ponto.dto.ApprovalDecisionDTO;
import com.ponto.dto.OvertimeCreateDTO;
import com.ponto.dto.OvertimeResponseDTO;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.OvertimeAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OvertimeService {

    private final OvertimeAdjustmentRepository overtimeRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public OvertimeResponseDTO create(OvertimeCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        if (request.getRequestedMinutes() == null || request.getRequestedMinutes() <= 0) {
            throw new BusinessException("Minutos solicitados devem ser maiores que zero");
        }

        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));

        OvertimeAdjustment overtime = new OvertimeAdjustment();
        overtime.setEmployee(employee);
        overtime.setCompany(employee.getCompany());
        overtime.setWorkDate(request.getWorkDate());
        overtime.setRequestedMinutes(request.getRequestedMinutes());
        overtime.setReason(request.getReason());

        return toResponse(overtimeRepository.save(overtime));
    }

    @Transactional(readOnly = true)
    public List<OvertimeResponseDTO> listOwn() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        Employee employee = employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));

        return overtimeRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OvertimeResponseDTO> listCompany(Boolean onlyPending) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();

        List<OvertimeAdjustment> list = Boolean.TRUE.equals(onlyPending)
                ? overtimeRepository.findAllByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, ApprovalStatus.PENDING)
                : overtimeRepository.findAllByCompanyIdOrderByCreatedAtDesc(companyId);

        return list.stream().map(this::toResponse).toList();
    }

    public OvertimeResponseDTO approve(Long id, ApprovalDecisionDTO decision) {
        return review(id, decision, ApprovalStatus.APPROVED);
    }

    public OvertimeResponseDTO reject(Long id, ApprovalDecisionDTO decision) {
        return review(id, decision, ApprovalStatus.REJECTED);
    }

    private OvertimeResponseDTO review(Long id, ApprovalDecisionDTO decision, ApprovalStatus status) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        OvertimeAdjustment entity = overtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de hora extra nao encontrada"));

        currentUserService.validateCompanyAccess(currentUser, entity.getCompany().getId());
        if (entity.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("Solicitacao ja revisada anteriormente");
        }

        entity.setStatus(status);
        entity.setReviewedBy(currentUser);
        entity.setReviewedAt(java.time.LocalDateTime.now());
        entity.setReviewComment(decision != null ? decision.getComment() : null);

        return toResponse(overtimeRepository.save(entity));
    }

    public long approvedMinutesInRange(Long companyId, Long employeeId, LocalDate start, LocalDate end) {
        return overtimeRepository.findAllByCompanyIdAndWorkDateBetweenAndStatus(companyId, start, end, ApprovalStatus.APPROVED)
                .stream()
                .filter(o -> o.getEmployee().getId().equals(employeeId))
                .mapToLong(OvertimeAdjustment::getRequestedMinutes)
                .sum();
    }

    private OvertimeResponseDTO toResponse(OvertimeAdjustment entity) {
        OvertimeResponseDTO dto = new OvertimeResponseDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setWorkDate(entity.getWorkDate());
        dto.setRequestedMinutes(entity.getRequestedMinutes());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setReviewComment(entity.getReviewComment());
        dto.setReviewedBy(entity.getReviewedBy() != null ? entity.getReviewedBy().getName() : null);
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
