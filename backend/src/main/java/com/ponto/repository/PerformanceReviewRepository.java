package com.ponto.repository;

import com.ponto.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<PerformanceReview> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
