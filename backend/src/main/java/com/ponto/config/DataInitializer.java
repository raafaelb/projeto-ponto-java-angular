package com.ponto.config;

import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Company company = companyRepository.findByCnpj("12345678000199").orElseGet(() -> {
            Company nova = new Company();
            nova.setCnpj("12345678000199");
            nova.setRazaoSocial("Empresa Demo LTDA");
            nova.setNomeFantasia("Empresa Demo");
            return companyRepository.save(nova);
        });

        boolean adminExists = userRepository.findByUsername("admin").isPresent()
                || userRepository.findByEmail("admin@sistema.com").isPresent();
        if (!adminExists) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@sistema.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        } else {
            log.debug("DataInitializer: usuario admin ja existe (username/email), criacao ignorada");
        }

        boolean companyUserExists = userRepository.findByUsername("empresa").isPresent()
                || userRepository.findByEmail("empresa@sistema.com").isPresent();
        if (!companyUserExists) {
            User companyUser = new User();
            companyUser.setName("Usuario Empresa Demo");
            companyUser.setEmail("empresa@sistema.com");
            companyUser.setUsername("empresa");
            companyUser.setPassword(passwordEncoder.encode("empresa123"));
            companyUser.setRole(User.UserRole.COMPANY);
            companyUser.setCompany(company);
            companyUser.setActive(true);
            userRepository.save(companyUser);
        } else {
            log.debug("DataInitializer: usuario empresa ja existe (username/email), criacao ignorada");
        }
    }
}
