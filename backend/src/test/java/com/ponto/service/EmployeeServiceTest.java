package com.ponto.service;

import com.ponto.dto.EmployeeRequestDTO;
import com.ponto.dto.EmployeeResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.Department;
import com.ponto.entity.Employee;
import com.ponto.entity.User;
import com.ponto.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    private User companyUser;
    private Company company;

    @BeforeEach
    void setup() {
        company = new Company();
        company.setId(10L);
        company.setNomeFantasia("Empresa XPTO");

        companyUser = new User();
        companyUser.setId(99L);
        companyUser.setRole(User.UserRole.COMPANY);
        companyUser.setCompany(company);
    }

    @Test
    void createEmployeeCreatesLinkedUserAndDepartment() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setName("Ana");
        request.setEmail("ana@empresa.com");
        request.setEmployeeCode("EMP-001");
        request.setUsername("ana.func");
        request.setPassword("123456");
        request.setPosition("Analista");
        request.setHiringDate(LocalDate.of(2025, 1, 10));
        request.setDepartmentId(5L);

        Department department = new Department();
        department.setId(5L);
        department.setName("Financeiro");

        User employeeUser = new User();
        employeeUser.setId(1L);
        employeeUser.setUsername("ana.func");
        employeeUser.setEmail("ana@empresa.com");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setPosition(request.getPosition());
        employee.setHiringDate(request.getHiringDate());
        employee.setCompany(company);
        employee.setDepartment(department);
        employee.setUser(employeeUser);
        employee.setActive(true);

        when(currentUserService.getCurrentUser()).thenReturn(companyUser);
        doNothing().when(currentUserService).validateCompanyManagerOnly(companyUser);
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(departmentRepository.findByIdAndCompanyId(5L, 10L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByEmailAndCompanyId("ana@empresa.com", 10L)).thenReturn(false);
        when(employeeRepository.existsByEmployeeCodeAndCompanyId("EMP-001", 10L)).thenReturn(false);
        when(userRepository.existsByUsername("ana.func")).thenReturn(false);
        when(userRepository.existsByEmail("ana@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(employeeUser);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO response = employeeService.create(request);

        assertEquals("Ana", response.getName());
        assertEquals("EMP-001", response.getEmployeeCode());
        assertEquals("Financeiro", response.getDepartmentName());
    }

    @Test
    void listAllForCompanyReturnsSortedEmployees() {
        User employeeUser = new User();
        employeeUser.setId(2L);
        employeeUser.setUsername("bruno.dev");

        Employee employee = new Employee();
        employee.setId(2L);
        employee.setName("Bruno");
        employee.setEmail("bruno@empresa.com");
        employee.setEmployeeCode("EMP-002");
        employee.setPosition("Dev");
        employee.setHiringDate(LocalDate.of(2024, 3, 1));
        employee.setCompany(company);
        employee.setUser(employeeUser);
        employee.setActive(true);

        when(currentUserService.getCurrentUser()).thenReturn(companyUser);
        doNothing().when(currentUserService).validateCompanyManagerOnly(companyUser);
        when(employeeRepository.findAllByCompanyIdOrderByNameAsc(10L)).thenReturn(List.of(employee));

        List<EmployeeResponseDTO> result = employeeService.listAll();

        assertEquals(1, result.size());
        assertEquals("Bruno", result.get(0).getName());
        assertEquals("EMP-002", result.get(0).getEmployeeCode());
    }
}
