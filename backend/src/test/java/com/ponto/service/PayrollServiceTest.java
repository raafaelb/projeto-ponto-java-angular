package com.ponto.service;

import com.ponto.dto.PayrollCycleCreateDTO;
import com.ponto.dto.PayrollCycleResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.PayrollCycle;
import com.ponto.entity.User;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PayrollCycleRepository;
import com.ponto.repository.PayslipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollCycleRepository payrollCycleRepository;
    @Mock
    private PayslipRepository payslipRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private ComplianceAuditService complianceAuditService;

    @InjectMocks
    private PayrollService payrollService;

    @Test
    void createCycleShouldReturnCreatedCycle() {
        Company company = new Company();
        company.setId(1L);
        User manager = new User();
        manager.setRole(User.UserRole.COMPANY);
        manager.setCompany(company);

        PayrollCycleCreateDTO request = new PayrollCycleCreateDTO();
        request.setPeriodStart(LocalDate.of(2026, 4, 1));
        request.setPeriodEnd(LocalDate.of(2026, 4, 30));
        request.setPaymentDate(LocalDate.of(2026, 5, 5));

        PayrollCycle saved = new PayrollCycle();
        saved.setId(10L);
        saved.setCompany(company);
        saved.setPeriodStart(request.getPeriodStart());
        saved.setPeriodEnd(request.getPeriodEnd());
        saved.setPaymentDate(request.getPaymentDate());

        when(currentUserService.getCurrentUser()).thenReturn(manager);
        doNothing().when(currentUserService).validateCompanyManagerOnly(manager);
        when(payrollCycleRepository.save(any(PayrollCycle.class))).thenReturn(saved);
        doNothing().when(complianceAuditService).log(any(), any(), any(), any(), any(), any(), any());

        PayrollCycleResponseDTO response = payrollService.createCycle(request);
        assertEquals(10L, response.getId());
        assertEquals(LocalDate.of(2026, 4, 1), response.getPeriodStart());
    }
}
