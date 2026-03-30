package com.ponto.service;

import com.ponto.dto.EmployeeRequestDTO;
import com.ponto.dto.EmployeeResponseDTO;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final CareerLevelRepository careerLevelRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> listAll() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        return employeeRepository.findAllByCompanyIdOrderByNameAsc(companyId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Employee employee = employeeRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        return toResponse(employee);
    }

    public EmployeeResponseDTO create(EmployeeRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        Sanitized sanitized = sanitize(request);

        if (employeeRepository.existsByEmailAndCompanyId(sanitized.email(), companyId)) {
            throw new BusinessException("Ja existe funcionario com esse email nesta empresa");
        }
        if (employeeRepository.existsByEmployeeCodeAndCompanyId(sanitized.employeeCode(), companyId)) {
            throw new BusinessException("Matricula ja cadastrada nesta empresa");
        }
        if (userRepository.existsByUsername(sanitized.username())) {
            throw new BusinessException("Username ja cadastrado");
        }
        if (userRepository.existsByEmail(sanitized.email())) {
            throw new BusinessException("Email ja cadastrado");
        }
        if (sanitized.password() == null || sanitized.password().length() < 6) {
            throw new BusinessException("Senha deve ter no minimo 6 caracteres");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        User employeeUser = new User();
        employeeUser.setUsername(sanitized.username());
        employeeUser.setName(request.getName());
        employeeUser.setEmail(sanitized.email());
        employeeUser.setPassword(passwordEncoder.encode(sanitized.password()));
        employeeUser.setRole(User.UserRole.EMPLOYEE);
        employeeUser.setCompany(company);
        employeeUser.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        employeeUser = userRepository.save(employeeUser);

        Employee employee = new Employee();
        employee.setUser(employeeUser);
        employee.setCompany(company);
        applyEmployeeFields(employee, request, companyId, null);

        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        Employee employee = employeeRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        Sanitized sanitized = sanitize(request);

        if (!employee.getEmployeeCode().equals(sanitized.employeeCode())
                && employeeRepository.existsByEmployeeCodeAndCompanyId(sanitized.employeeCode(), companyId)) {
            throw new BusinessException("Matricula ja cadastrada nesta empresa");
        }

        User employeeUser = employee.getUser();
        if (employeeUser == null) {
            if (sanitized.password() == null || sanitized.password().length() < 6) {
                throw new BusinessException("Funcionario sem login. Informe senha com no minimo 6 caracteres para criar acesso.");
            }
            employeeUser = new User();
            employeeUser.setRole(User.UserRole.EMPLOYEE);
            employeeUser.setCompany(employee.getCompany());
        } else {
            if (!employeeUser.getUsername().equals(sanitized.username()) && userRepository.existsByUsername(sanitized.username())) {
                throw new BusinessException("Username ja cadastrado");
            }
            if (!employeeUser.getEmail().equals(sanitized.email()) && userRepository.existsByEmail(sanitized.email())) {
                throw new BusinessException("Email ja cadastrado");
            }
        }

        employeeUser.setUsername(sanitized.username());
        employeeUser.setName(request.getName());
        employeeUser.setEmail(sanitized.email());
        employeeUser.setActive(request.getActive() == null ? employee.getActive() : request.getActive());

        if (sanitized.password() != null && !sanitized.password().isBlank()) {
            if (sanitized.password().length() < 6) {
                throw new BusinessException("Senha deve ter no minimo 6 caracteres");
            }
            employeeUser.setPassword(passwordEncoder.encode(sanitized.password()));
        } else if (employee.getUser() == null) {
            throw new BusinessException("Senha obrigatoria para criar acesso do funcionario.");
        }

        employeeUser = userRepository.save(employeeUser);
        employee.setUser(employeeUser);

        applyEmployeeFields(employee, request, companyId, id);

        return toResponse(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Employee employee = employeeRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        Long userId = employee.getUser() != null ? employee.getUser().getId() : null;
        employeeRepository.delete(employee);

        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    private void applyEmployeeFields(Employee employee, EmployeeRequestDTO request, Long companyId, Long editingEmployeeId) {
        Sanitized sanitized = sanitize(request);

        employee.setName(request.getName());
        employee.setEmail(sanitized.email());
        employee.setEmployeeCode(sanitized.employeeCode());
        employee.setPosition(request.getPosition());
        employee.setHiringDate(request.getHiringDate());
        employee.setBirthDate(request.getBirthDate());
        employee.setPhone(sanitizeOptional(request.getPhone()));
        employee.setAddress(sanitizeOptional(request.getAddress()));
        employee.setEmergencyContactName(sanitizeOptional(request.getEmergencyContactName()));
        employee.setEmergencyContactPhone(sanitizeOptional(request.getEmergencyContactPhone()));
        employee.setContractType(sanitizeOptional(request.getContractType()));
        employee.setContractStartDate(request.getContractStartDate());
        employee.setContractEndDate(request.getContractEndDate());
        employee.setCurrentSalary(request.getCurrentSalary());
        employee.setActive(request.getActive() == null ? employee.getActive() : request.getActive());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdAndCompanyId(request.getDepartmentId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento nao encontrado"));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }

        if (request.getTeamId() != null) {
            Team team = teamRepository.findByIdAndCompanyId(request.getTeamId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Time nao encontrado"));
            employee.setTeam(team);
        } else {
            employee.setTeam(null);
        }

        if (request.getManagerEmployeeId() != null) {
            if (editingEmployeeId != null && request.getManagerEmployeeId().equals(editingEmployeeId)) {
                throw new BusinessException("Funcionario nao pode ser gestor de si mesmo");
            }
            Employee manager = employeeRepository.findByIdAndCompanyId(request.getManagerEmployeeId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gestor nao encontrado"));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        if (request.getCareerLevelId() != null) {
            CareerLevel careerLevel = careerLevelRepository.findByIdAndCompanyId(request.getCareerLevelId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Nivel de carreira nao encontrado"));
            employee.setCareerLevel(careerLevel);
        } else {
            employee.setCareerLevel(null);
        }
    }

    private Sanitized sanitize(EmployeeRequestDTO request) {
        String username = sanitizeRequired(request.getUsername(), "Username e obrigatorio");
        String email = sanitizeRequired(request.getEmail(), "Email e obrigatorio");
        String employeeCode = sanitizeRequired(request.getEmployeeCode(), "Matricula e obrigatoria");
        String password = request.getPassword() != null ? request.getPassword().trim() : null;
        return new Sanitized(username, email, employeeCode, password);
    }

    private String sanitizeRequired(String value, String error) {
        String sanitized = value != null ? value.trim() : null;
        if (sanitized == null || sanitized.isBlank()) {
            throw new BusinessException(error);
        }
        return sanitized;
    }

    private String sanitizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim();
        return sanitized.isBlank() ? null : sanitized;
    }

    private EmployeeResponseDTO toResponse(Employee employee) {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setPosition(employee.getPosition());
        response.setHiringDate(employee.getHiringDate());
        response.setBirthDate(employee.getBirthDate());
        response.setPhone(employee.getPhone());
        response.setAddress(employee.getAddress());
        response.setEmergencyContactName(employee.getEmergencyContactName());
        response.setEmergencyContactPhone(employee.getEmergencyContactPhone());
        response.setContractType(employee.getContractType());
        response.setContractStartDate(employee.getContractStartDate());
        response.setContractEndDate(employee.getContractEndDate());
        response.setActive(employee.getActive());
        response.setCompanyId(employee.getCompany().getId());
        response.setCompanyName(employee.getCompany().getNomeFantasia());

        response.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null);
        response.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
        response.setTeamId(employee.getTeam() != null ? employee.getTeam().getId() : null);
        response.setTeamName(employee.getTeam() != null ? employee.getTeam().getName() : null);
        response.setManagerEmployeeId(employee.getManager() != null ? employee.getManager().getId() : null);
        response.setManagerName(employee.getManager() != null ? employee.getManager().getName() : null);
        response.setCareerLevelId(employee.getCareerLevel() != null ? employee.getCareerLevel().getId() : null);
        response.setCareerLevelName(employee.getCareerLevel() != null ? employee.getCareerLevel().getName() : null);
        response.setCurrentSalary(employee.getCurrentSalary());

        response.setUserId(employee.getUser() != null ? employee.getUser().getId() : null);
        response.setUsername(employee.getUser() != null ? employee.getUser().getUsername() : null);
        response.setDataCriacao(employee.getDataCriacao());
        response.setDataAtualizacao(employee.getDataAtualizacao());
        return response;
    }

    private record Sanitized(String username, String email, String employeeCode, String password) {
    }
}
