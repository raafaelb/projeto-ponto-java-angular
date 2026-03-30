package com.ponto.config;

import com.ponto.entity.Company;
import com.ponto.entity.User;
import com.ponto.repository.CompanyRepository;
import com.ponto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
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

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@sistema.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("empresa").isEmpty()) {
            User companyUser = new User();
            companyUser.setName("Usuario Empresa Demo");
            companyUser.setEmail("empresa@sistema.com");
            companyUser.setUsername("empresa");
            companyUser.setPassword(passwordEncoder.encode("empresa123"));
            companyUser.setRole(User.UserRole.COMPANY);
            companyUser.setCompany(company);
            companyUser.setActive(true);
            userRepository.save(companyUser);
        }
    }
}
