package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamRequestDTO {
    @NotBlank(message = "Nome do time e obrigatorio")
    private String name;
    private String description;
    private Long departmentId;
}
