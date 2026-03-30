package com.ponto.repository;

import com.ponto.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<Payslip> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
