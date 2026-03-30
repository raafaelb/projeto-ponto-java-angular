package com.ponto.service;

import com.ponto.dto.AbsenceRequestCreateDTO;
import com.ponto.dto.AbsenceRequestResponseDTO;
import com.ponto.entity.*;
import com.ponto.repository.AbsenceRequestRepository;
import com.ponto.repository.EmployeeRepository;
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
class AbsenceRequestServiceTest {

    @Mock
    private AbsenceRequestRepository absenceRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AbsenceRequestService absenceRequestService;

    @Test
    void shouldCreateAbsenceRequest() {
        User employeeUser = new User();
        employeeUser.setId(5L);
        employeeUser.setRole(User.UserRole.EMPLOYEE);

        Company company = new Company();
        company.setId(1L);

        Employee employee = new Employee();
        employee.setId(8L);
        employee.setName("Ana");
        employee.setCompany(company);
        employee.setUser(employeeUser);

        AbsenceRequestCreateDTO dto = new AbsenceRequestCreateDTO();
        dto.setType(AbsenceType.VACATION);
        dto.setStartDate(LocalDate.of(2026, 4, 10));
        dto.setEndDate(LocalDate.of(2026, 4, 15));
        dto.setReason("Viagem");

        AbsenceRequest saved = new AbsenceRequest();
        saved.setId(1L);
        saved.setEmployee(employee);
        saved.setCompany(company);
        saved.setType(dto.getType());
        saved.setStartDate(dto.getStartDate());
        saved.setEndDate(dto.getEndDate());
        saved.setReason(dto.getReason());
        saved.setStatus(ApprovalStatus.PENDING);

        when(currentUserService.getCurrentUser()).thenReturn(employeeUser);
        when(employeeRepository.findByUserId(5L)).thenReturn(Optional.of(employee));
        when(absenceRequestRepository.save(any(AbsenceRequest.class))).thenReturn(saved);

        AbsenceRequestResponseDTO response = absenceRequestService.create(dto);

        assertEquals(1L, response.getId());
        assertEquals(AbsenceType.VACATION, response.getType());
        assertEquals("Ana", response.getEmployeeName());
    }
}
