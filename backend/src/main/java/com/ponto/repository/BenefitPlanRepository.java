package com.ponto.repository;

import com.ponto.entity.BenefitPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BenefitPlanRepository extends JpaRepository<BenefitPlan, Long> {
    List<BenefitPlan> findAllByCompanyIdOrderByNameAsc(Long companyId);
    List<BenefitPlan> findAllByCompanyIdAndActiveTrueOrderByNameAsc(Long companyId);
    Optional<BenefitPlan> findByIdAndCompanyId(Long id, Long companyId);
}
