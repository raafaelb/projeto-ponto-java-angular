package com.ponto.service;

import com.ponto.dto.UserRequestDTO;
import com.ponto.dto.UserResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ForbiddenException;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService userManagementService;

    private User companyUser;
    private Company company;

    @BeforeEach
    void setup() {
        company = new Company();
        company.setId(30L);
        company.setNomeFantasia("Empresa 30");

        companyUser = new User();
        companyUser.setRole(User.UserRole.COMPANY);
        companyUser.setCompany(company);
    }

    @Test
    void companyUserCannotCreateAdmin() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("novoadmin");
        request.setName("Novo Admin");
        request.setEmail("novoadmin@x.com");
        request.setPassword("123456");
        request.setRole(User.UserRole.ADMIN);

        when(currentUserService.getCurrentUser()).thenReturn(companyUser);

        assertThrows(ForbiddenException.class, () -> userManagementService.create(request));
    }

    @Test
    void createCompanyUserBindsToOwnCompany() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("gestor1");
        request.setName("Gestor 1");
        request.setEmail("gestor1@empresa.com");
        request.setPassword("123456");
        request.setRole(User.UserRole.COMPANY);

        User persisted = new User();
        persisted.setId(1L);
        persisted.setUsername("gestor1");
        persisted.setName("Gestor 1");
        persisted.setEmail("gestor1@empresa.com");
        persisted.setRole(User.UserRole.COMPANY);
        persisted.setCompany(company);
        persisted.setActive(true);

        when(currentUserService.getCurrentUser()).thenReturn(companyUser);
        when(userRepository.existsByUsername("gestor1")).thenReturn(false);
        when(userRepository.existsByEmail("gestor1@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(companyRepository.findById(30L)).thenReturn(Optional.of(company));
        when(userRepository.save(any(User.class))).thenReturn(persisted);

        UserResponseDTO response = userManagementService.create(request);

        assertEquals(User.UserRole.COMPANY, response.getRole());
        assertEquals(30L, response.getCompanyId());
        verify(userRepository).save(any(User.class));
    }
}
