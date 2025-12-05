package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRequestDTO {
    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    private String cnpj;
    
    @NotBlank(message = "Razão social é obrigatória")
    @Size(min = 3, max = 200)
    private String razaoSocial;
    
    @Size(max = 200)
    private String nomeFantasia;
}