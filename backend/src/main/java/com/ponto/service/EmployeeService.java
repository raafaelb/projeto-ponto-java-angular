package com.ponto.service;

import com.ponto.dto.EmployeeRequestDTO;
import com.ponto.dto.EmployeeResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.Employee;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.UserRepository;
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
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> listAll() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        return employeeRepository.findAllByCompanyId(companyId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        currentUserService.validateCompanyAccess(currentUser, employee.getCompany().getId());
        return toResponse(employee);
    }

    public EmployeeResponseDTO create(EmployeeRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        String username = request.getUsername() != null ? request.getUsername().trim() : null;
        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        String password = request.getPassword() != null ? request.getPassword().trim() : null;

        if (username == null || username.isBlank()) {
            throw new BusinessException("Username e obrigatorio");
        }
        if (email == null || email.isBlank()) {
            throw new BusinessException("Email e obrigatorio");
        }
        if (employeeRepository.existsByEmailAndCompanyId(email, companyId)) {
            throw new BusinessException("Ja existe funcionario com esse email nesta empresa");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username ja cadastrado");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email ja cadastrado");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException("Senha deve ter no minimo 6 caracteres");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        User employeeUser = new User();
        employeeUser.setUsername(username);
        employeeUser.setName(request.getName());
        employeeUser.setEmail(email);
        employeeUser.setPassword(passwordEncoder.encode(password));
        employeeUser.setRole(User.UserRole.EMPLOYEE);
        employeeUser.setCompany(company);
        employeeUser.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        employeeUser = userRepository.save(employeeUser);

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(email);
        employee.setPosition(request.getPosition());
        employee.setHiringDate(request.getHiringDate());
        employee.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        employee.setCompany(company);
        employee.setUser(employeeUser);

        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        currentUserService.validateCompanyAccess(currentUser, employee.getCompany().getId());

        String username = request.getUsername() != null ? request.getUsername().trim() : null;
        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        String password = request.getPassword() != null ? request.getPassword().trim() : null;
        if (username == null || username.isBlank()) {
            throw new BusinessException("Username e obrigatorio");
        }
        if (email == null || email.isBlank()) {
            throw new BusinessException("Email e obrigatorio");
        }

        User employeeUser = employee.getUser();
        if (employeeUser == null) {
            if (password == null || password.length() < 6) {
                throw new BusinessException("Funcionario sem login. Informe senha com no minimo 6 caracteres para criar acesso.");
            }

            employeeUser = new User();
            employeeUser.setRole(User.UserRole.EMPLOYEE);
            employeeUser.setCompany(employee.getCompany());
        } else {
            if (!employeeUser.getUsername().equals(username) && userRepository.existsByUsername(username)) {
                throw new BusinessException("Username ja cadastrado");
            }
            if (!employeeUser.getEmail().equals(email) && userRepository.existsByEmail(email)) {
                throw new BusinessException("Email ja cadastrado");
            }
        }

        employee.setName(request.getName());
        employee.setEmail(email);
        employee.setPosition(request.getPosition());
        employee.setHiringDate(request.getHiringDate());
        employee.setActive(request.getActive() == null ? employee.getActive() : request.getActive());

        employeeUser.setUsername(username);
        employeeUser.setName(request.getName());
        employeeUser.setEmail(email);
        employeeUser.setActive(employee.getActive());

        if (password != null && !password.isBlank()) {
            if (password.length() < 6) {
                throw new BusinessException("Senha deve ter no minimo 6 caracteres");
            }
            employeeUser.setPassword(passwordEncoder.encode(password));
        } else if (employee.getUser() == null) {
            throw new BusinessException("Senha obrigatoria para criar acesso do funcionario.");
        }

        userRepository.save(employeeUser);
        employee.setUser(employeeUser);
        return toResponse(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        currentUserService.validateCompanyAccess(currentUser, employee.getCompany().getId());
        Long userId = employee.getUser() != null ? employee.getUser().getId() : null;

        employeeRepository.delete(employee);
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    private EmployeeResponseDTO toResponse(Employee employee) {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setPosition(employee.getPosition());
        response.setHiringDate(employee.getHiringDate());
        response.setActive(employee.getActive());
        response.setCompanyId(employee.getCompany().getId());
        response.setCompanyName(employee.getCompany().getNomeFantasia());
        response.setUserId(employee.getUser() != null ? employee.getUser().getId() : null);
        response.setUsername(employee.getUser() != null ? employee.getUser().getUsername() : null);
        response.setDataCriacao(employee.getDataCriacao());
        response.setDataAtualizacao(employee.getDataAtualizacao());
        return response;
    }
}
