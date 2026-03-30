package com.ponto.service;

import com.ponto.dto.CompanyRequestDTO;
import com.ponto.dto.CompanyResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CompanyService companyService;

    private User admin;
    private User companyUser;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setCnpj("12345678000199");
        company.setRazaoSocial("Empresa Teste LTDA");
        company.setNomeFantasia("Empresa Teste");

        admin = new User();
        admin.setRole(User.UserRole.ADMIN);

        companyUser = new User();
        companyUser.setRole(User.UserRole.COMPANY);
        companyUser.setCompany(company);
    }

    @Test
    void findAllReturnsAllForAdmin() {
        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(currentUserService.isAdmin(admin)).thenReturn(true);
        when(companyRepository.findAll()).thenReturn(List.of(company));

        List<CompanyResponseDTO> result = companyService.findAll();

        assertEquals(1, result.size());
        assertEquals("Empresa Teste LTDA", result.get(0).getRazaoSocial());
    }

    @Test
    void findAllReturnsOwnCompanyForCompanyUser() {
        when(currentUserService.getCurrentUser()).thenReturn(companyUser);
        when(currentUserService.isAdmin(companyUser)).thenReturn(false);

        List<CompanyResponseDTO> result = companyService.findAll();

        assertEquals(1, result.size());
        assertEquals(company.getId(), result.get(0).getId());
    }

    @Test
    void createThrowsWhenCnpjAlreadyExists() {
        CompanyRequestDTO request = new CompanyRequestDTO();
        request.setCnpj("12345678000199");
        request.setRazaoSocial("Nova");
        request.setNomeFantasia("Nova");

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        doNothing().when(currentUserService).validateAdminOnly(admin);
        when(companyRepository.existsByCnpj("12345678000199")).thenReturn(true);

        assertThrows(BusinessException.class, () -> companyService.create(request));
    }

    @Test
    void findByIdValidatesCompanyAccess() {
        when(currentUserService.getCurrentUser()).thenReturn(companyUser);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        CompanyResponseDTO response = companyService.findById(1L);

        verify(currentUserService).validateCompanyAccess(companyUser, 1L);
        assertEquals(1L, response.getId());
    }
}
