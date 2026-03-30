package com.ponto.service;

import com.ponto.dto.PerformanceGoalRequestDTO;
import com.ponto.dto.PerformanceGoalResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.Employee;
import com.ponto.entity.GoalStatus;
import com.ponto.entity.PerformanceGoal;
import com.ponto.entity.User;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PerformanceGoalRepository;
import com.ponto.repository.PerformanceReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private PerformanceGoalRepository goalRepository;

    @Mock
    private PerformanceReviewRepository reviewRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PerformanceService performanceService;

    @Test
    void createGoalForEmployeeShouldReturnMappedResponse() {
        Company company = new Company();
        company.setId(7L);

        User manager = new User();
        manager.setRole(User.UserRole.COMPANY);
        manager.setCompany(company);

        Employee employee = new Employee();
        employee.setId(11L);
        employee.setName("Maria");
        employee.setCompany(company);

        PerformanceGoal saved = new PerformanceGoal();
        saved.setId(101L);
        saved.setEmployee(employee);
        saved.setCompany(company);
        saved.setTitle("Entregar projeto");
        saved.setDescription("Meta trimestral");
        saved.setWeight(40);
        saved.setDueDate(LocalDate.of(2026, 6, 30));
        saved.setStatus(GoalStatus.IN_PROGRESS);

        PerformanceGoalRequestDTO request = new PerformanceGoalRequestDTO();
        request.setTitle("Entregar projeto");
        request.setDescription("Meta trimestral");
        request.setWeight(40);
        request.setDueDate(LocalDate.of(2026, 6, 30));
        request.setStatus(GoalStatus.IN_PROGRESS);

        when(currentUserService.getCurrentUser()).thenReturn(manager);
        when(employeeRepository.findByIdAndCompanyId(11L, 7L)).thenReturn(Optional.of(employee));
        when(goalRepository.save(any(PerformanceGoal.class))).thenReturn(saved);

        PerformanceGoalResponseDTO response = performanceService.createGoal(11L, request);

        assertEquals(101L, response.getId());
        assertEquals("Maria", response.getEmployeeName());
        assertEquals("Entregar projeto", response.getTitle());
        assertEquals(GoalStatus.IN_PROGRESS, response.getStatus());
    }
}
