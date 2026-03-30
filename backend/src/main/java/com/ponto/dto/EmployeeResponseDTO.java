package com.ponto.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String position;
    private LocalDate hiringDate;
    private Boolean active;
    private Long companyId;
    private String companyName;
    private Long userId;
    private String username;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
