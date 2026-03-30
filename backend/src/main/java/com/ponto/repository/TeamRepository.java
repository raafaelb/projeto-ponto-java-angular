package com.ponto.repository;

import com.ponto.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByCompanyIdOrderByNameAsc(Long companyId);
    boolean existsByNameAndCompanyId(String name, Long companyId);
    Optional<Team> findByIdAndCompanyId(Long id, Long companyId);
}
