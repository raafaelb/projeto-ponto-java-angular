package com.ponto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ponto.entity.User;
import com.ponto.entity.User.UserRole;
import com.ponto.repository.UserRepository;

@SpringBootApplication
public class PontoApplication {

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("INICIANDO SISTEMA DE PONTO");
        System.out.println("==================================");
        SpringApplication.run(PontoApplication.class, args);
    }
    
    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("=== INICIANDO DATABASE SETUP ===");
            
            if (userRepository.findByUsername("admin").isEmpty()) {
                System.out.println("Criando usuario admin...");
                
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                
                userRepository.save(admin);
                
                System.out.println("==================================");
                System.out.println("USUARIO ADMIN CRIADO COM SUCESSO");
                System.out.println("Username: admin");
                System.out.println("Senha: admin123");
                System.out.println("==================================");
            } else {
                System.out.println("Usuario admin ja existe no banco.");
                
                // Verificar o hash atual
                User admin = userRepository.findByUsername("admin").get();
                System.out.println("Hash atual do admin: " + admin.getPassword().substring(0, 30) + "...");
            }
        };
    }
}