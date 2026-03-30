package com.ponto.dto;

import com.ponto.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Username e obrigatorio")
    private String username;

    @NotBlank(message = "Nome e obrigatorio")
    private String name;

    @Email(message = "Email invalido")
    @NotBlank(message = "Email e obrigatorio")
    private String email;

    private String password;

    @NotNull(message = "Role e obrigatoria")
    private User.UserRole role;

    private Long companyId;
    private Boolean active;
}
