package com.ponto.service;

import com.ponto.dto.BenefitPlanRequestDTO;
import com.ponto.dto.BenefitPlanResponseDTO;
import com.ponto.entity.BenefitPlan;
import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.repository.BenefitEnrollmentRepository;
import com.ponto.repository.BenefitPlanRepository;
import com.ponto.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenefitsServiceTest {

    @Mock
    private BenefitPlanRepository benefitPlanRepository;
    @Mock
    private BenefitEnrollmentRepository benefitEnrollmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private ComplianceAuditService complianceAuditService;

    @InjectMocks
    private BenefitsService benefitsService;

    @Test
    void createPlanShouldReturnCreatedPlan() {
        Company company = new Company();
        company.setId(1L);
        User manager = new User();
        manager.setRole(User.UserRole.COMPANY);
        manager.setCompany(company);

        BenefitPlanRequestDTO request = new BenefitPlanRequestDTO();
        request.setName("Plano Saude");
        request.setMonthlyEmployerCost(new BigDecimal("400.00"));
        request.setMonthlyEmployeeCost(new BigDecimal("120.00"));

        BenefitPlan saved = new BenefitPlan();
        saved.setId(5L);
        saved.setCompany(company);
        saved.setName("Plano Saude");
        saved.setMonthlyEmployerCost(new BigDecimal("400.00"));
        saved.setMonthlyEmployeeCost(new BigDecimal("120.00"));
        saved.setActive(true);

        when(currentUserService.getCurrentUser()).thenReturn(manager);
        doNothing().when(currentUserService).validateCompanyManagerOnly(manager);
        when(benefitPlanRepository.save(any(BenefitPlan.class))).thenReturn(saved);
        doNothing().when(complianceAuditService).log(any(), any(), any(), any(), any(), any(), any());

        BenefitPlanResponseDTO response = benefitsService.createPlan(request);
        assertEquals(5L, response.getId());
        assertEquals("Plano Saude", response.getName());
    }
}
