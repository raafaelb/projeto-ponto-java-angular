package com.ponto.dto;

import lombok.Data;

@Data
public class DepartmentResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long companyId;
}
