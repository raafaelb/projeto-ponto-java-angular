// UserDTO.java
package com.ponto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data  
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    private String username;
    
    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    private String email;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;
    
    private String role; // ADMIN, COMPANY, EMPLOYEE
    private Long companyId;
    private String companyName; // Para mostrar no frontend
    
    // Campos específicos para criação/atualização
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password; // Só usado na criação
    
    private String confirmPassword; // Para validação
}