package com.ponto.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String employeeCode;
    private String position;
    private LocalDate hiringDate;
    private LocalDate birthDate;
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String contractType;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Boolean active;

    private Long companyId;
    private String companyName;

    private Long departmentId;
    private String departmentName;
    private Long teamId;
    private String teamName;
    private Long managerEmployeeId;
    private String managerName;

    private Long userId;
    private String username;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
