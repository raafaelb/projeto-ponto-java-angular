package com.ponto.repository;

import com.ponto.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByCompanyId(Long companyId);
    boolean existsByEmailAndCompanyId(String email, Long companyId);
    Optional<Employee> findByUserId(Long userId);
}
