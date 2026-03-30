package com.ponto.service;

import com.ponto.dto.ComplianceAuditEventResponseDTO;
import com.ponto.entity.ComplianceAuditEvent;
import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.repository.ComplianceAuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceAuditService {

    private final ComplianceAuditEventRepository auditEventRepository;
    private final CurrentUserService currentUserService;

    public void log(Company company, String eventType, String message, String severity, String entityType, Long entityId, User createdBy) {
        ComplianceAuditEvent event = new ComplianceAuditEvent();
        event.setCompany(company);
        event.setEventType(eventType);
        event.setMessage(message);
        event.setSeverity(severity);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setCreatedBy(createdBy);
        auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<ComplianceAuditEventResponseDTO> listCompanyEvents() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return auditEventRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ComplianceAuditEventResponseDTO toResponse(ComplianceAuditEvent event) {
        ComplianceAuditEventResponseDTO dto = new ComplianceAuditEventResponseDTO();
        dto.setId(event.getId());
        dto.setEventType(event.getEventType());
        dto.setMessage(event.getMessage());
        dto.setSeverity(event.getSeverity());
        dto.setEntityType(event.getEntityType());
        dto.setEntityId(event.getEntityId());
        dto.setCreatedBy(event.getCreatedBy() != null ? event.getCreatedBy().getName() : null);
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }
}
