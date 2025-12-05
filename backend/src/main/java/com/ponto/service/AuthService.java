package com.ponto.service;

import com.ponto.entity.User;
import com.ponto.repository.UserRepository;
import com.ponto.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public Map<String, Object> autenticar(String username, String password) {
        System.out.println("Autenticando usuário: " + username);
        
        // 1. Busca usuário no banco
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // 2. Verifica senha
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }
        
        System.out.println("Usuário autenticado: " + username + " - Role: " + user.getRole());
        
        // 3. Gera token JWT
        String token = jwtTokenProvider.criarToken(
            username, 
            user.getRole().name(),
            user.getId()
        );
        
        // 4. Prepara resposta com informações do usuário
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("token", token);
        
        // 5. Informações do usuário
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("role", user.getRole().name());
        
        // 6. Se não for ADMIN, adiciona companyId
        if (user.getRole() != User.UserRole.ADMIN && user.getCompany() != null) {
            userInfo.put("companyId", user.getCompany().getId());
        }
        
        userInfo.put("avatar", null); // Implemente upload depois
        userInfo.put("permissions", user.getPermissions());
        
        authResponse.put("user", userInfo);
        authResponse.put("expiresIn", 86400000); // 24 horas
        authResponse.put("message", "Login realizado com sucesso");
        
        return authResponse;
    }
    
    // Método para criar usuários de teste (opcional)
    @Transactional
    public void criarUsuariosTeste() {
        if (!userRepository.findByUsername("admin").isPresent()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@sistema.com");
            admin.setName("Administrador Sistema");
            admin.setRole(User.UserRole.ADMIN);
            admin.setPermissions(Arrays.asList("CREATE", "READ", "UPDATE", "DELETE", "MANAGE_USERS"));
            userRepository.save(admin);
            System.out.println("Usuário admin criado");
        }
    }
}