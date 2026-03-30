package com.ponto.service;

import com.ponto.dto.TeamRequestDTO;
import com.ponto.dto.TeamResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.Department;
import com.ponto.entity.Team;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.DepartmentRepository;
import com.ponto.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<TeamResponseDTO> listAll() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        return teamRepository.findAllByCompanyIdOrderByNameAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TeamResponseDTO create(TeamRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        String name = request.getName().trim();

        if (teamRepository.existsByNameAndCompanyId(name, companyId)) {
            throw new BusinessException("Ja existe time com esse nome");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        Team team = new Team();
        team.setName(name);
        team.setDescription(request.getDescription());
        team.setCompany(company);

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdAndCompanyId(request.getDepartmentId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento nao encontrado"));
            team.setDepartment(department);
        }

        return toResponse(teamRepository.save(team));
    }

    public TeamResponseDTO update(Long id, TeamRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        Team team = teamRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Time nao encontrado"));

        String name = request.getName().trim();
        if (!team.getName().equalsIgnoreCase(name) && teamRepository.existsByNameAndCompanyId(name, companyId)) {
            throw new BusinessException("Ja existe time com esse nome");
        }

        team.setName(name);
        team.setDescription(request.getDescription());

        if (request.getDepartmentId() == null) {
            team.setDepartment(null);
        } else {
            Department department = departmentRepository.findByIdAndCompanyId(request.getDepartmentId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento nao encontrado"));
            team.setDepartment(department);
        }

        return toResponse(teamRepository.save(team));
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Team team = teamRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Time nao encontrado"));

        teamRepository.delete(team);
    }

    private TeamResponseDTO toResponse(Team team) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setCompanyId(team.getCompany().getId());
        dto.setDepartmentId(team.getDepartment() != null ? team.getDepartment().getId() : null);
        dto.setDepartmentName(team.getDepartment() != null ? team.getDepartment().getName() : null);
        return dto;
    }
}
