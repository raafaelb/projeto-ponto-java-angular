package com.ponto.controller;

import com.ponto.dto.LoginRequest;
import com.ponto.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("=== TENTATIVA DE LOGIN ===");
        System.out.println("Username: " + loginRequest.getUsername());
        
        try {
            Map<String, Object> authResponse = authService.autenticar(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            System.out.println("=== LOGIN BEM-SUCEDIDO ===");
            System.out.println("Role retornado: " + ((Map)authResponse.get("user")).get("role"));
            
            return ResponseEntity.ok(authResponse);
            
        } catch (RuntimeException e) {
            System.err.println("=== ERRO NO LOGIN: " + e.getMessage() + " ===");
            
            // Retorna mensagem de erro mais detalhada
            Map<String, String> errorResponse = Map.of(
                "error", "Falha na autenticação",
                "message", e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    // Endpoint para criar dados de teste (apenas desenvolvimento)
    @PostMapping("/setup-test-data")
    public ResponseEntity<String> setupTestData() {
        try {
            authService.criarUsuariosTeste();
            return ResponseEntity.ok("Dados de teste criados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao criar dados de teste: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API Auth está funcionando!");
    }
}