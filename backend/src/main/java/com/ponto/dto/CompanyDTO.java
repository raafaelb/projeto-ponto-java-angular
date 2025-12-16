package com.ponto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CompanyDTO {
    private Long id;
    
    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", 
             message = "CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX")
    private String cnpj;
    
    @NotBlank(message = "Razão social é obrigatória")
    @Size(min = 3, max = 200, message = "Razão social deve ter entre 3 e 200 caracteres")
    private String razaoSocial;
    
    @Size(max = 200, message = "Nome fantasia não pode ultrapassar 200 caracteres")
    private String nomeFantasia;
}