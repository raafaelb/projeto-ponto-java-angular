package com.ponto.repository;

import com.ponto.entity.PromotionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRequestRepository extends JpaRepository<PromotionRequest, Long> {
    List<PromotionRequest> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<PromotionRequest> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
