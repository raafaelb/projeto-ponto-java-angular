package com.ponto.repository;

import com.ponto.entity.BonusRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonusRequestRepository extends JpaRepository<BonusRequest, Long> {
    List<BonusRequest> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<BonusRequest> findAllByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
