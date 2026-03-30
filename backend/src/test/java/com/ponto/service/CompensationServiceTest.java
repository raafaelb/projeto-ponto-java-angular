package com.ponto.service;

import com.ponto.dto.BonusRequestCreateDTO;
import com.ponto.dto.BonusRequestResponseDTO;
import com.ponto.dto.SalaryAdjustmentCreateDTO;
import com.ponto.dto.SalaryAdjustmentResponseDTO;
import com.ponto.entity.BonusRequest;
import com.ponto.entity.Company;
import com.ponto.entity.Employee;
import com.ponto.entity.SalaryAdjustment;
import com.ponto.entity.User;
import com.ponto.repository.BonusRequestRepository;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.SalaryAdjustmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompensationServiceTest {

    @Mock
    private SalaryAdjustmentRepository salaryAdjustmentRepository;

    @Mock
    private BonusRequestRepository bonusRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CompensationService compensationService;

    @Test
    void createSalaryAdjustmentShouldReturnCreatedItem() {
        Company company = new Company();
        company.setId(1L);

        User manager = new User();
        manager.setRole(User.UserRole.COMPANY);
        manager.setCompany(company);

        Employee employee = new Employee();
        employee.setId(2L);
        employee.setName("Bruno");
        employee.setCompany(company);
        employee.setCurrentSalary(new BigDecimal("4500.00"));

        SalaryAdjustment saved = new SalaryAdjustment();
        saved.setId(11L);
        saved.setEmployee(employee);
        saved.setCompany(company);
        saved.setPreviousSalary(new BigDecimal("4500.00"));
        saved.setNewSalary(new BigDecimal("5200.00"));
        saved.setEffectiveDate(LocalDate.of(2026, 7, 1));

        SalaryAdjustmentCreateDTO request = new SalaryAdjustmentCreateDTO();
        request.setEmployeeId(2L);
        request.setNewSalary(new BigDecimal("5200.00"));
        request.setEffectiveDate(LocalDate.of(2026, 7, 1));
        request.setReason("Promocao");

        when(currentUserService.getCurrentUser()).thenReturn(manager);
        when(employeeRepository.findByIdAndCompanyId(2L, 1L)).thenReturn(Optional.of(employee));
        when(salaryAdjustmentRepository.save(any(SalaryAdjustment.class))).thenReturn(saved);

        SalaryAdjustmentResponseDTO response = compensationService.createSalaryAdjustment(request);
        assertEquals(11L, response.getId());
        assertEquals("Bruno", response.getEmployeeName());
        assertEquals(new BigDecimal("5200.00"), response.getNewSalary());
    }

    @Test
    void createBonusRequestShouldReturnCreatedItem() {
        Company company = new Company();
        company.setId(1L);

        User employeeUser = new User();
        employeeUser.setId(9L);
        employeeUser.setRole(User.UserRole.EMPLOYEE);
        employeeUser.setCompany(company);

        Employee employee = new Employee();
        employee.setId(3L);
        employee.setName("Ana");
        employee.setCompany(company);
        employee.setUser(employeeUser);

        BonusRequest saved = new BonusRequest();
        saved.setId(20L);
        saved.setEmployee(employee);
        saved.setCompany(company);
        saved.setAmount(new BigDecimal("800.00"));
        saved.setReferenceDate(LocalDate.of(2026, 6, 30));

        BonusRequestCreateDTO request = new BonusRequestCreateDTO();
        request.setAmount(new BigDecimal("800.00"));
        request.setReferenceDate(LocalDate.of(2026, 6, 30));
        request.setReason("Meta batida");

        when(currentUserService.getCurrentUser()).thenReturn(employeeUser);
        when(employeeRepository.findByUserId(9L)).thenReturn(Optional.of(employee));
        when(bonusRequestRepository.save(any(BonusRequest.class))).thenReturn(saved);

        BonusRequestResponseDTO response = compensationService.createBonusRequest(request);
        assertEquals(20L, response.getId());
        assertEquals("Ana", response.getEmployeeName());
        assertEquals(new BigDecimal("800.00"), response.getAmount());
    }
}
