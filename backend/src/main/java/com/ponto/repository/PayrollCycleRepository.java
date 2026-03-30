package com.ponto.repository;

import com.ponto.entity.PayrollCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollCycleRepository extends JpaRepository<PayrollCycle, Long> {
    List<PayrollCycle> findAllByCompanyIdOrderByPeriodStartDesc(Long companyId);
    Optional<PayrollCycle> findByIdAndCompanyId(Long id, Long companyId);
}
