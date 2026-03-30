package com.ponto.repository;

import com.ponto.entity.PerformanceGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, Long> {
    List<PerformanceGoal> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<PerformanceGoal> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
