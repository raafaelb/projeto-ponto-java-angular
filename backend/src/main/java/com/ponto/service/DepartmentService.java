package com.ponto.service;

import com.ponto.dto.DepartmentRequestDTO;
import com.ponto.dto.DepartmentResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.Department;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> listAll() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        return departmentRepository.findAllByCompanyIdOrderByNameAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DepartmentResponseDTO create(DepartmentRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        String name = request.getName().trim();

        if (departmentRepository.existsByNameAndCompanyId(name, companyId)) {
            throw new BusinessException("Ja existe departamento com esse nome");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        Department department = new Department();
        department.setName(name);
        department.setDescription(request.getDescription());
        department.setCompany(company);

        return toResponse(departmentRepository.save(department));
    }

    public DepartmentResponseDTO update(Long id, DepartmentRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Department department = departmentRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento nao encontrado"));

        String name = request.getName().trim();
        if (!department.getName().equalsIgnoreCase(name)
                && departmentRepository.existsByNameAndCompanyId(name, currentUser.getCompany().getId())) {
            throw new BusinessException("Ja existe departamento com esse nome");
        }

        department.setName(name);
        department.setDescription(request.getDescription());

        return toResponse(departmentRepository.save(department));
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Department department = departmentRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento nao encontrado"));

        departmentRepository.delete(department);
    }

    private DepartmentResponseDTO toResponse(Department department) {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setCompanyId(department.getCompany().getId());
        return dto;
    }
}
