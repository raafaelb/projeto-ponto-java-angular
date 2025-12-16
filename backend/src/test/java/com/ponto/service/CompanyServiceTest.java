package com.ponto.service;

import com.ponto.dto.CompanyRequestDTO;
import com.ponto.dto.CompanyResponseDTO;
import com.ponto.entity.Company;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CompanyService companyService;

    private Company company;
    private CompanyRequestDTO requestDTO;
    private CompanyResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        company = new Company();
        company.setId(1L);
        company.setCnpj("12345678000199");
        company.setRazaoSocial("Empresa Teste LTDA");
        company.setNomeFantasia("Empresa Teste");

        requestDTO = new CompanyRequestDTO();
        requestDTO.setCnpj("12345678000199");
        requestDTO.setRazaoSocial("Empresa Teste LTDA");
        requestDTO.setNomeFantasia("Empresa Teste");

        responseDTO = new CompanyResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCnpj("12345678000199");
        responseDTO.setRazaoSocial("Empresa Teste LTDA");
        responseDTO.setNomeFantasia("Empresa Teste");
    }

    @Test
    void whenCreateCompany_thenReturnCompanyResponseDTO() {
        // Arrange
        when(modelMapper.map(requestDTO, Company.class)).thenReturn(company);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(modelMapper.map(company, CompanyResponseDTO.class)).thenReturn(responseDTO);

        // Act
        CompanyResponseDTO savedCompany = companyService.create(requestDTO);

        // Assert
        assertNotNull(savedCompany);
        assertEquals("12345678000199", savedCompany.getCnpj());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void whenFindAll_thenReturnListOfCompanyResponseDTO() {
        // Arrange
        List<Company> companies = Arrays.asList(company);
        when(companyRepository.findAll()).thenReturn(companies);
        when(modelMapper.map(company, CompanyResponseDTO.class)).thenReturn(responseDTO);

        // Act
        List<CompanyResponseDTO> companyList = companyService.findAll();

        // Assert
        assertFalse(companyList.isEmpty());
        assertEquals(1, companyList.size());
        verify(companyRepository, times(1)).findAll();
    }

    @Test
    void whenFindById_thenReturnCompanyResponseDTO() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(modelMapper.map(company, CompanyResponseDTO.class)).thenReturn(responseDTO);

        // Act
        CompanyResponseDTO foundCompany = companyService.findById(1L);

        // Assert
        assertNotNull(foundCompany);
        assertEquals("Empresa Teste LTDA", foundCompany.getRazaoSocial());
        verify(companyRepository, times(1)).findById(1L);
    }

    @Test
    void whenFindById_thenThrowResourceNotFoundException() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> companyService.findById(1L));
        verify(companyRepository, times(1)).findById(1L);
    }

    @Test
    void whenUpdate_thenReturnUpdatedCompany() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(modelMapper.map(company, CompanyResponseDTO.class)).thenReturn(responseDTO);

        // Update some fields
        requestDTO.setNomeFantasia("Updated Name");
        company.setNomeFantasia("Updated Name");
        responseDTO.setNomeFantasia("Updated Name");

        // Act
        CompanyResponseDTO updatedCompany = companyService.update(1L, requestDTO);

        // Assert
        assertNotNull(updatedCompany);
        assertEquals("Updated Name", updatedCompany.getNomeFantasia());
        verify(companyRepository, times(1)).findById(1L);
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    void whenUpdateNonExistingCompany_thenThrowResourceNotFoundException() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> companyService.update(1L, requestDTO));
        verify(companyRepository, times(1)).findById(1L);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void whenDelete_thenCompanyShouldBeDeleted() {
        // Arrange
        when(companyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(companyRepository).deleteById(1L);

        // Act
        companyService.delete(1L);

        // Assert
        verify(companyRepository, times(1)).existsById(1L);
        verify(companyRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteNonExistingCompany_thenThrowResourceNotFoundException() {
        // Arrange
        when(companyRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> companyService.delete(1L));
        verify(companyRepository, times(1)).existsById(1L);
        verify(companyRepository, never()).deleteById(anyLong());
    }

    @Test
    void whenCnpjAlreadyExists_thenThrowBusinessException() {
        // Arrange
        when(companyRepository.existsByCnpj("12345678000199")).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, 
            () -> companyService.create(requestDTO));
        verify(companyRepository, times(1)).existsByCnpj("12345678000199");
        verify(companyRepository, never()).save(any(Company.class));
    }
}