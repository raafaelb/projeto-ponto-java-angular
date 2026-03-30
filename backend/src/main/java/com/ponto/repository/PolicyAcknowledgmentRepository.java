package com.ponto.repository;

import com.ponto.entity.PolicyAcknowledgment;
import com.ponto.entity.PolicyAckStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyAcknowledgmentRepository extends JpaRepository<PolicyAcknowledgment, Long> {
    List<PolicyAcknowledgment> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<PolicyAcknowledgment> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<PolicyAcknowledgment> findAllByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, PolicyAckStatus status);
    Optional<PolicyAcknowledgment> findByPolicyDocumentIdAndEmployeeId(Long policyDocumentId, Long employeeId);
}
