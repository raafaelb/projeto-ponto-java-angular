package com.ponto.repository;

import com.ponto.entity.ComplianceAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplianceAuditEventRepository extends JpaRepository<ComplianceAuditEvent, Long> {
    List<ComplianceAuditEvent> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
