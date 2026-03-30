package com.ponto.repository;

import com.ponto.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findAllByCompanyIdOrderByHolidayDateAsc(Long companyId);
    List<Holiday> findAllByCompanyIdAndHolidayDateBetweenOrderByHolidayDateAsc(Long companyId, LocalDate start, LocalDate end);
    boolean existsByCompanyIdAndHolidayDateAndName(Long companyId, LocalDate holidayDate, String name);
}
