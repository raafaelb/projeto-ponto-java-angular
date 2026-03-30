package com.ponto.service;

import com.ponto.dto.UserRequestDTO;
import com.ponto.dto.UserResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listAll(Long companyId) {
        User currentUser = currentUserService.getCurrentUser();
        List<User> users;

        if (currentUserService.isAdmin(currentUser) && companyId == null) {
            users = userRepository.findAll();
        } else {
            Long accessibleCompanyId = currentUserService.resolveAccessibleCompanyId(currentUser, companyId);
            users = userRepository.findAllByCompanyId(accessibleCompanyId);
        }

        return users.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));

        validateAccess(currentUser, target);
        return toResponse(target);
    }

    public UserResponseDTO create(UserRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username ja cadastrado");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email ja cadastrado");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BusinessException("Senha deve ter no minimo 6 caracteres");
        }

        User.UserRole role = request.getRole();
        Company company = resolveCompanyForCreate(currentUser, request.getCompanyId(), role);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCompany(company);
        user.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());

        return toResponse(userRepository.save(user));
    }

    public UserResponseDTO update(Long id, UserRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));

        validateAccess(currentUser, target);

        if (!target.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username ja cadastrado");
        }
        if (!target.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email ja cadastrado");
        }

        if (request.getRole() == User.UserRole.ADMIN && !currentUserService.isAdmin(currentUser)) {
            throw new BusinessException("Somente admin pode atribuir role ADMIN");
        }

        target.setUsername(request.getUsername());
        target.setName(request.getName());
        target.setEmail(request.getEmail());
        target.setRole(request.getRole());
        target.setActive(request.getActive() == null ? target.getActive() : request.getActive());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 6) {
                throw new BusinessException("Senha deve ter no minimo 6 caracteres");
            }
            target.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() == User.UserRole.ADMIN) {
            target.setCompany(null);
        } else {
            Long companyId = currentUserService.isAdmin(currentUser)
                    ? request.getCompanyId()
                    : currentUser.getCompany().getId();
            if (companyId == null) {
                throw new BusinessException("companyId e obrigatorio para usuarios nao admin");
            }
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
            target.setCompany(company);
        }

        return toResponse(userRepository.save(target));
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));

        validateAccess(currentUser, target);

        if (target.getRole() == User.UserRole.ADMIN && !currentUserService.isAdmin(currentUser)) {
            throw new BusinessException("Usuario da empresa nao pode remover admin");
        }

        userRepository.delete(target);
    }

    private Company resolveCompanyForCreate(User currentUser, Long companyId, User.UserRole role) {
        if (role == User.UserRole.ADMIN) {
            currentUserService.validateAdminOnly(currentUser);
            return null;
        }

        Long resolvedCompanyId;
        if (currentUserService.isAdmin(currentUser)) {
            if (companyId == null) {
                throw new BusinessException("companyId e obrigatorio para role COMPANY/EMPLOYEE");
            }
            resolvedCompanyId = companyId;
        } else {
            if (currentUser.getCompany() == null) {
                throw new BusinessException("Usuario sem empresa vinculada");
            }
            resolvedCompanyId = currentUser.getCompany().getId();
        }

        return companyRepository.findById(resolvedCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
    }

    private void validateAccess(User currentUser, User target) {
        if (currentUserService.isAdmin(currentUser)) {
            return;
        }

        if (target.getCompany() == null || currentUser.getCompany() == null
                || !target.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new BusinessException("Acesso permitido apenas para usuarios da propria empresa");
        }
    }

    private UserResponseDTO toResponse(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);
        response.setCompanyName(user.getCompany() != null ? user.getCompany().getNomeFantasia() : null);
        response.setActive(user.getActive());
        response.setDataCriacao(user.getDataCriacao());
        response.setDataAtualizacao(user.getDataAtualizacao());
        return response;
    }
}
