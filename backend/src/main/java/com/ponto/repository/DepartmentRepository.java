package com.ponto.repository;

import com.ponto.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByCompanyIdOrderByNameAsc(Long companyId);
    boolean existsByNameAndCompanyId(String name, Long companyId);
    Optional<Department> findByIdAndCompanyId(Long id, Long companyId);
}
