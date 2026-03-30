package com.ponto.repository;

import com.ponto.entity.SkillAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillAssessmentRepository extends JpaRepository<SkillAssessment, Long> {
    List<SkillAssessment> findAllByEmployeeIdOrderByUpdatedAtDesc(Long employeeId);
    List<SkillAssessment> findAllByCompanyIdOrderByUpdatedAtDesc(Long companyId);
}
