package com.ponto.service;

import com.ponto.entity.User;
import com.ponto.repository.UserRepository;
import com.ponto.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, Object> autenticar(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Usuario inativo");
        }

        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        String token = jwtTokenProvider.criarToken(
                username,
                user.getRole().name(),
                user.getId(),
                companyId
        );

        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("role", user.getRole().name());
        userInfo.put("companyId", companyId);
        userInfo.put("active", user.getActive());
        userInfo.put("permissions", user.getPermissions());

        authResponse.put("user", userInfo);
        authResponse.put("expiresIn", 86400000);
        authResponse.put("message", "Login realizado com sucesso");

        return authResponse;
    }

    public void criarUsuariosTeste() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@sistema.com");
            admin.setName("Administrador Sistema");
            admin.setRole(User.UserRole.ADMIN);
            admin.setPermissions(Arrays.asList("CREATE", "READ", "UPDATE", "DELETE", "MANAGE_USERS"));
            admin.setActive(true);
            userRepository.save(admin);
        }
    }
}
