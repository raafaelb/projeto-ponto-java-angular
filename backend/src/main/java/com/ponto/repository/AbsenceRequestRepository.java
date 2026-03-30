package com.ponto.repository;

import com.ponto.entity.AbsenceRequest;
import com.ponto.entity.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Long> {
    List<AbsenceRequest> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<AbsenceRequest> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<AbsenceRequest> findAllByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, ApprovalStatus status);
    List<AbsenceRequest> findAllByCompanyIdAndStartDateBetweenOrderByStartDateAsc(Long companyId, LocalDate start, LocalDate end);
}
