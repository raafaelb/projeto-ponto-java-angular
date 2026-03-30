package com.ponto.service;

import com.ponto.dto.OvertimeCreateDTO;
import com.ponto.dto.OvertimeResponseDTO;
import com.ponto.entity.*;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.OvertimeAdjustmentRepository;
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
class OvertimeServiceTest {

    @Mock
    private OvertimeAdjustmentRepository overtimeRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private OvertimeService overtimeService;

    @Test
    void shouldCreateOvertimeRequest() {
        User user = new User();
        user.setId(12L);
        user.setRole(User.UserRole.EMPLOYEE);

        Company company = new Company();
        company.setId(1L);

        Employee employee = new Employee();
        employee.setId(33L);
        employee.setName("Bruno");
        employee.setCompany(company);
        employee.setUser(user);

        OvertimeCreateDTO dto = new OvertimeCreateDTO();
        dto.setWorkDate(LocalDate.of(2026, 4, 3));
        dto.setRequestedMinutes(120);
        dto.setReason("Fechamento mensal");

        OvertimeAdjustment saved = new OvertimeAdjustment();
        saved.setId(20L);
        saved.setEmployee(employee);
        saved.setCompany(company);
        saved.setWorkDate(dto.getWorkDate());
        saved.setRequestedMinutes(dto.getRequestedMinutes());
        saved.setReason(dto.getReason());
        saved.setStatus(ApprovalStatus.PENDING);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(employeeRepository.findByUserId(12L)).thenReturn(Optional.of(employee));
        when(overtimeRepository.save(any(OvertimeAdjustment.class))).thenReturn(saved);

        OvertimeResponseDTO response = overtimeService.create(dto);

        assertEquals(20L, response.getId());
        assertEquals(120, response.getRequestedMinutes());
        assertEquals("Bruno", response.getEmployeeName());
    }
}
