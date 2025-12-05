package com.ponto.service;

import com.ponto.dto.CompanyDTO;
import com.ponto.dto.CompanyRequestDTO;
import com.ponto.dto.CompanyResponseDTO;
import com.ponto.entity.Company;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j  // Para logging
@Service
@RequiredArgsConstructor  // Gera construtor com final fields
@Transactional
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    
    public List<CompanyResponseDTO> findAll() {
        log.info("Buscando todas as empresas");
        return companyRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public CompanyResponseDTO findById(Long id) {
        log.info("Buscando empresa com ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com ID: " + id));
        return convertToResponseDTO(company);
    }
    
    public CompanyResponseDTO create(CompanyRequestDTO companyRequestDTO) {
        log.info("Criando nova empresa: {}", companyRequestDTO.getRazaoSocial());
        
        // Validação de negócio
        if (companyRepository.existsByCnpj(companyRequestDTO.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado: " + companyRequestDTO.getCnpj());
        }
        
        // Converter DTO para Entity
        Company company = new Company();
        company.setCnpj(companyRequestDTO.getCnpj());
        company.setRazaoSocial(companyRequestDTO.getRazaoSocial());
        company.setNomeFantasia(companyRequestDTO.getNomeFantasia());
        company.setDataCriacao(LocalDateTime.now());
        company.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar
        Company savedCompany = companyRepository.save(company);
        log.info("Empresa criada com ID: {}", savedCompany.getId());
        
        return convertToResponseDTO(savedCompany);
    }
    
    public CompanyResponseDTO update(Long id, CompanyRequestDTO companyRequestDTO) {
        log.info("Atualizando empresa ID: {}", id);
        
        // Buscar empresa existente
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com ID: " + id));
        
        // Validação: CNPJ único (se mudou)
        if (!company.getCnpj().equals(companyRequestDTO.getCnpj()) &&
            companyRepository.existsByCnpj(companyRequestDTO.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado: " + companyRequestDTO.getCnpj());
        }
        
        // Atualizar campos
        company.setCnpj(companyRequestDTO.getCnpj());
        company.setRazaoSocial(companyRequestDTO.getRazaoSocial());
        company.setNomeFantasia(companyRequestDTO.getNomeFantasia());
        company.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar
        Company updatedCompany = companyRepository.save(company);
        log.info("Empresa ID: {} atualizada", id);
        
        return convertToResponseDTO(updatedCompany);
    }
    
    public void delete(Long id) {
        log.info("Deletando empresa ID: {}", id);
        
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa não encontrada com ID: " + id);
        }
        
        companyRepository.deleteById(id);
        log.info("Empresa ID: {} deletada", id);
    }
    
    // Conversor: Entity → ResponseDTO
    private CompanyResponseDTO convertToResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setCnpj(company.getCnpj());
        dto.setRazaoSocial(company.getRazaoSocial());
        dto.setNomeFantasia(company.getNomeFantasia());
        dto.setDataCriacao(company.getDataCriacao());
        dto.setDataAtualizacao(company.getDataAtualizacao());
        return dto;
    }
    
    // Conversor: Entity → DTO (para listagem simples)
    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setCnpj(company.getCnpj());
        dto.setRazaoSocial(company.getRazaoSocial());
        dto.setNomeFantasia(company.getNomeFantasia());
        return dto;
    }
}