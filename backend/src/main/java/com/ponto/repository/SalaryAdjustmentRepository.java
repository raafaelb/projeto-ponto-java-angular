package com.ponto.repository;

import com.ponto.entity.SalaryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryAdjustmentRepository extends JpaRepository<SalaryAdjustment, Long> {
    List<SalaryAdjustment> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<SalaryAdjustment> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
