package com.ponto.service;

import com.ponto.dto.CareerLevelRequestDTO;
import com.ponto.dto.CareerLevelResponseDTO;
import com.ponto.dto.PromotionRequestCreateDTO;
import com.ponto.dto.PromotionRequestResponseDTO;
import com.ponto.entity.CareerLevel;
import com.ponto.entity.Company;
import com.ponto.entity.Employee;
import com.ponto.entity.PromotionRequest;
import com.ponto.entity.User;
import com.ponto.repository.CareerLevelRepository;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PromotionRequestRepository;
import com.ponto.repository.SkillAssessmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareerDevelopmentServiceTest {

    @Mock
    private CareerLevelRepository careerLevelRepository;

    @Mock
    private SkillAssessmentRepository skillAssessmentRepository;

    @Mock
    private PromotionRequestRepository promotionRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CareerDevelopmentService careerDevelopmentService;

    @Test
    void createCareerLevelShouldReturnCreatedLevel() {
        Company company = new Company();
        company.setId(1L);

        User manager = new User();
        manager.setRole(User.UserRole.COMPANY);
        manager.setCompany(company);

        CareerLevel saved = new CareerLevel();
        saved.setId(3L);
        saved.setCompany(company);
        saved.setName("Senior");
        saved.setRankOrder(3);

        CareerLevelRequestDTO request = new CareerLevelRequestDTO();
        request.setName("Senior");
        request.setRankOrder(3);
        request.setDescription("Nivel senior");

        when(currentUserService.getCurrentUser()).thenReturn(manager);
        when(careerLevelRepository.existsByCompanyIdAndName(1L, "Senior")).thenReturn(false);
        when(careerLevelRepository.save(any(CareerLevel.class))).thenReturn(saved);

        CareerLevelResponseDTO response = careerDevelopmentService.createCareerLevel(request);
        assertEquals(3L, response.getId());
        assertEquals("Senior", response.getName());
    }

    @Test
    void createPromotionRequestShouldReturnCreatedRequest() {
        Company company = new Company();
        company.setId(1L);

        User employeeUser = new User();
        employeeUser.setId(8L);
        employeeUser.setRole(User.UserRole.EMPLOYEE);
        employeeUser.setCompany(company);

        CareerLevel targetLevel = new CareerLevel();
        targetLevel.setId(5L);
        targetLevel.setName("Especialista");
        targetLevel.setCompany(company);

        Employee employee = new Employee();
        employee.setId(15L);
        employee.setName("Carlos");
        employee.setCompany(company);
        employee.setUser(employeeUser);

        PromotionRequest saved = new PromotionRequest();
        saved.setId(40L);
        saved.setEmployee(employee);
        saved.setCompany(company);
        saved.setToLevel(targetLevel);

        PromotionRequestCreateDTO request = new PromotionRequestCreateDTO();
        request.setToLevelId(5L);
        request.setJustification("Resultados acima da media");

        when(currentUserService.getCurrentUser()).thenReturn(employeeUser);
        when(employeeRepository.findByUserId(8L)).thenReturn(Optional.of(employee));
        when(careerLevelRepository.findByIdAndCompanyId(5L, 1L)).thenReturn(Optional.of(targetLevel));
        when(promotionRequestRepository.save(any(PromotionRequest.class))).thenReturn(saved);

        PromotionRequestResponseDTO response = careerDevelopmentService.createPromotionRequest(request);
        assertEquals(40L, response.getId());
        assertEquals("Carlos", response.getEmployeeName());
        assertEquals("Especialista", response.getToLevelName());
    }
}
