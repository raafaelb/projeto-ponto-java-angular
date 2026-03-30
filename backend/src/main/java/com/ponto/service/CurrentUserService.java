package com.ponto.service;

import com.ponto.entity.User;
import com.ponto.exception.ForbiddenException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new ForbiddenException("Usuario nao autenticado");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado nao encontrado"));
    }

    public boolean isAdmin(User user) {
        return user.getRole() == User.UserRole.ADMIN;
    }

    public boolean isCompanyManager(User user) {
        return user.getRole() == User.UserRole.COMPANY;
    }

    public boolean isEmployee(User user) {
        return user.getRole() == User.UserRole.EMPLOYEE;
    }

    public Long resolveAccessibleCompanyId(User user, Long requestedCompanyId) {
        if (isAdmin(user)) {
            if (requestedCompanyId == null) {
                throw new ForbiddenException("companyId e obrigatorio para esta operacao");
            }
            return requestedCompanyId;
        }

        if (user.getCompany() == null) {
            throw new ForbiddenException("Usuario sem empresa vinculada");
        }

        Long companyId = user.getCompany().getId();
        if (requestedCompanyId != null && !requestedCompanyId.equals(companyId)) {
            throw new ForbiddenException("Acesso permitido apenas para a propria empresa");
        }

        return companyId;
    }

    public void validateCompanyAccess(User user, Long companyId) {
        if (isAdmin(user)) {
            return;
        }

        if (user.getCompany() == null || !user.getCompany().getId().equals(companyId)) {
            throw new ForbiddenException("Acesso permitido apenas para a propria empresa");
        }
    }

    public void validateAdminOnly(User user) {
        if (!isAdmin(user)) {
            throw new ForbiddenException("Somente administradores podem executar esta operacao");
        }
    }

    public void validateCompanyManagerOnly(User user) {
        if (!isCompanyManager(user)) {
            throw new ForbiddenException("Somente gestores da empresa podem executar esta operacao");
        }
    }

    public void validateEmployeeOnly(User user) {
        if (!isEmployee(user)) {
            throw new ForbiddenException("Somente funcionarios podem executar esta operacao");
        }
    }
}
