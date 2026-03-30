package com.ponto.repository;

import com.ponto.entity.ApprovalStatus;
import com.ponto.entity.OvertimeAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OvertimeAdjustmentRepository extends JpaRepository<OvertimeAdjustment, Long> {
    List<OvertimeAdjustment> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<OvertimeAdjustment> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<OvertimeAdjustment> findAllByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, ApprovalStatus status);
    List<OvertimeAdjustment> findAllByCompanyIdAndWorkDateBetweenAndStatus(Long companyId, LocalDate start, LocalDate end, ApprovalStatus status);
}
