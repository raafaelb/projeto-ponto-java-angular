package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequestDTO {
    @NotBlank(message = "Nome do departamento e obrigatorio")
    private String name;
    private String description;
}
