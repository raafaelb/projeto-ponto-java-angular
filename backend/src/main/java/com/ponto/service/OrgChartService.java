package com.ponto.service;

import com.ponto.dto.OrgChartNodeDTO;
import com.ponto.entity.Employee;
import com.ponto.entity.User;
import com.ponto.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrgChartService {

    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public List<OrgChartNodeDTO> listNodes() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        return employeeRepository.findAllByCompanyIdOrderByNameAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toNode)
                .toList();
    }

    private OrgChartNodeDTO toNode(Employee employee) {
        OrgChartNodeDTO dto = new OrgChartNodeDTO();
        dto.setEmployeeId(employee.getId());
        dto.setEmployeeName(employee.getName());
        dto.setPosition(employee.getPosition());
        dto.setDepartment(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
        dto.setTeam(employee.getTeam() != null ? employee.getTeam().getName() : null);
        dto.setManagerEmployeeId(employee.getManager() != null ? employee.getManager().getId() : null);
        dto.setManagerName(employee.getManager() != null ? employee.getManager().getName() : null);
        return dto;
    }
}
