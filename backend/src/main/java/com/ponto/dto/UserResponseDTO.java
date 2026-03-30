package com.ponto.dto;

import com.ponto.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private User.UserRole role;
    private Long companyId;
    private String companyName;
    private Boolean active;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
