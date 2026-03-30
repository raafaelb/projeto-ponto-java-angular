package com.ponto.controller;

import com.ponto.dto.LoginRequest;
import com.ponto.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> authResponse = authService.autenticar(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = Map.of(
                    "error", "Falha na autenticacao",
                    "message", e.getMessage(),
                    "timestamp", String.valueOf(System.currentTimeMillis())
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

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
        return ResponseEntity.ok("API Auth esta funcionando!");
    }
}
