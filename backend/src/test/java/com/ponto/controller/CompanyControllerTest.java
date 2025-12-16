package com.ponto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponto.dto.CompanyRequestDTO;
import com.ponto.dto.CompanyResponseDTO;
import com.ponto.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(companyController).build();
    }

    @Test
    void whenGetAllCompanies_thenReturnListOfCompanies() throws Exception {
        // Arrange
        CompanyResponseDTO company1 = new CompanyResponseDTO();
        company1.setId(1L);
        company1.setCnpj("12345678000199");
        company1.setRazaoSocial("Empresa 1");

        CompanyResponseDTO company2 = new CompanyResponseDTO();
        company2.setId(2L);
        company2.setCnpj("98765432000199");
        company2.setRazaoSocial("Empresa 2");

        when(companyService.findAll()).thenReturn(Arrays.asList(company1, company2));

        // Act & Assert
        mockMvc.perform(get("/api/companies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].razaoSocial", is("Empresa 1")))
                .andExpect(jsonPath("$[1].razaoSocial", is("Empresa 2")));

        verify(companyService, times(1)).findAll();
    }

    @Test
    void whenGetCompanyById_thenReturnCompany() throws Exception {
        // Arrange
        CompanyResponseDTO company = new CompanyResponseDTO();
        company.setId(1L);
        company.setCnpj("12345678000199");
        company.setRazaoSocial("Empresa Teste");

        when(companyService.findById(1L)).thenReturn(company);

        // Act & Assert
        mockMvc.perform(get("/api/companies/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial", is("Empresa Teste")));

        verify(companyService, times(1)).findById(1L);
    }

    @Test
    void whenCreateCompany_thenReturnCreatedCompany() throws Exception {
        // Arrange
        CompanyRequestDTO requestDTO = new CompanyRequestDTO();
        requestDTO.setCnpj("12345678000199");
        requestDTO.setRazaoSocial("Nova Empresa");
        requestDTO.setNomeFantasia("Empresa Nova");

        CompanyResponseDTO responseDTO = new CompanyResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCnpj("12345678000199");
        responseDTO.setRazaoSocial("Nova Empresa");
        responseDTO.setNomeFantasia("Empresa Nova");

        when(companyService.create(any(CompanyRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.razaoSocial", is("Nova Empresa")));

        verify(companyService, times(1)).create(any(CompanyRequestDTO.class));
    }

    @Test
    void whenUpdateCompany_thenReturnUpdatedCompany() throws Exception {
        // Arrange
        CompanyRequestDTO requestDTO = new CompanyRequestDTO();
        requestDTO.setCnpj("12345678000199");
        requestDTO.setRazaoSocial("Empresa Atualizada");
        requestDTO.setNomeFantasia("Nome Fantasia Atualizado");

        CompanyResponseDTO responseDTO = new CompanyResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCnpj("12345678000199");
        responseDTO.setRazaoSocial("Empresa Atualizada");
        responseDTO.setNomeFantasia("Nome Fantasia Atualizado");

        when(companyService.update(eq(1L), any(CompanyRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/companies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial", is("Empresa Atualizada")))
                .andExpect(jsonPath("$.nomeFantasia", is("Nome Fantasia Atualizado")));

        verify(companyService, times(1)).update(eq(1L), any(CompanyRequestDTO.class));
    }

    @Test
    void whenDeleteCompany_thenReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(companyService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/companies/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).delete(1L);
    }
}