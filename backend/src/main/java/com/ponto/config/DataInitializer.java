package com.ponto.config;

import com.ponto.entity.User;
import com.ponto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Criar usuário admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@sistema.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
            
            System.out.println("=========================================");
            System.out.println("USUÁRIO ADMIN CRIADO COM SUCESSO!");
            System.out.println("Usuário: admin");
            System.out.println("Senha: admin123");
            System.out.println("=========================================");
        }
        
        // Criar usuário teste
        if (userRepository.findByUsername("teste").isEmpty()) {
            User teste = new User();
            teste.setName("teste");
            teste.setEmail("teste@sistema.com");
            teste.setUsername("teste");
            teste.setPassword(passwordEncoder.encode("123456"));
            teste.setRole(User.UserRole.ADMIN);
            userRepository.save(teste);
            
            System.out.println("Usuário teste criado: teste / 123456");
        }
    }
}