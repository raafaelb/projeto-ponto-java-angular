package com.ponto.repository;

import com.ponto.entity.AnomalyType;
import com.ponto.entity.AttendanceAnomaly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceAnomalyRepository extends JpaRepository<AttendanceAnomaly, Long> {
    List<AttendanceAnomaly> findAllByCompanyIdOrderByOccurrenceDateDescCreatedAtDesc(Long companyId);
    List<AttendanceAnomaly> findAllByCompanyIdAndResolvedOrderByOccurrenceDateDescCreatedAtDesc(Long companyId, Boolean resolved);
    boolean existsByEmployeeIdAndOccurrenceDateAndType(Long employeeId, LocalDate occurrenceDate, AnomalyType type);
    List<AttendanceAnomaly> findAllByCompanyIdAndOccurrenceDateBetweenOrderByOccurrenceDateAsc(Long companyId, LocalDate start, LocalDate end);
}
