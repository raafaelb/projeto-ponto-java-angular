package com.ponto.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";  // Padrão
    private UserDTO user;
    private long expiresIn;  // Em segundos
    private String message;
    
    // Construtor para facilitar
    public LoginResponse(String token, UserDTO user, long expiresIn) {
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
        this.message = "Login realizado com sucesso";
    }
}