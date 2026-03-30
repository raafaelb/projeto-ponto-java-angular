package com.ponto.service;

import com.ponto.dto.CompanyDTO;
import com.ponto.dto.CompanyRequestDTO;
import com.ponto.dto.CompanyResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> findAll() {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUserService.isAdmin(currentUser)) {
            return companyRepository.findAll().stream().map(this::convertToResponseDTO).collect(Collectors.toList());
        }

        if (currentUser.getCompany() == null) {
            return List.of();
        }

        return List.of(convertToResponseDTO(currentUser.getCompany()));
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO findById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada com ID: " + id));

        currentUserService.validateCompanyAccess(currentUser, company.getId());
        return convertToResponseDTO(company);
    }

    public CompanyResponseDTO create(CompanyRequestDTO companyRequestDTO) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateAdminOnly(currentUser);

        if (companyRepository.existsByCnpj(companyRequestDTO.getCnpj())) {
            throw new BusinessException("CNPJ ja cadastrado: " + companyRequestDTO.getCnpj());
        }

        Company company = new Company();
        company.setCnpj(companyRequestDTO.getCnpj());
        company.setRazaoSocial(companyRequestDTO.getRazaoSocial());
        company.setNomeFantasia(companyRequestDTO.getNomeFantasia());

        Company savedCompany = companyRepository.save(company);
        log.info("Empresa criada com ID: {}", savedCompany.getId());

        return convertToResponseDTO(savedCompany);
    }

    public CompanyResponseDTO update(Long id, CompanyRequestDTO companyRequestDTO) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateAdminOnly(currentUser);

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada com ID: " + id));

        if (!company.getCnpj().equals(companyRequestDTO.getCnpj())
                && companyRepository.existsByCnpj(companyRequestDTO.getCnpj())) {
            throw new BusinessException("CNPJ ja cadastrado: " + companyRequestDTO.getCnpj());
        }

        company.setCnpj(companyRequestDTO.getCnpj());
        company.setRazaoSocial(companyRequestDTO.getRazaoSocial());
        company.setNomeFantasia(companyRequestDTO.getNomeFantasia());

        Company updatedCompany = companyRepository.save(company);
        return convertToResponseDTO(updatedCompany);
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateAdminOnly(currentUser);

        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa nao encontrada com ID: " + id);
        }

        companyRepository.deleteById(id);
    }

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

    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setCnpj(company.getCnpj());
        dto.setRazaoSocial(company.getRazaoSocial());
        dto.setNomeFantasia(company.getNomeFantasia());
        return dto;
    }
}
