package com.ponto.repository;

import com.ponto.entity.BenefitEnrollment;
import com.ponto.entity.BenefitEnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitEnrollmentRepository extends JpaRepository<BenefitEnrollment, Long> {
    List<BenefitEnrollment> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<BenefitEnrollment> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<BenefitEnrollment> findAllByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, BenefitEnrollmentStatus status);
}
