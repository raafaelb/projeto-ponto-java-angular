package com.ponto.repository;

import com.ponto.entity.CareerLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerLevelRepository extends JpaRepository<CareerLevel, Long> {
    List<CareerLevel> findAllByCompanyIdOrderByRankOrderAsc(Long companyId);
    Optional<CareerLevel> findByIdAndCompanyId(Long id, Long companyId);
    boolean existsByCompanyIdAndName(Long companyId, String name);
}
