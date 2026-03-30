package com.ponto.dto;

import lombok.Data;

@Data
public class TeamResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long companyId;
    private Long departmentId;
    private String departmentName;
}
