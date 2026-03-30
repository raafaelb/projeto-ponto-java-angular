package com.ponto.repository;

import com.ponto.entity.PolicyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyDocumentRepository extends JpaRepository<PolicyDocument, Long> {
    List<PolicyDocument> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<PolicyDocument> findAllByCompanyIdAndActiveTrueOrderByEffectiveDateDesc(Long companyId);
    Optional<PolicyDocument> findByIdAndCompanyId(Long id, Long companyId);
}
