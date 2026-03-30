package com.ponto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequestDTO {
    @NotBlank(message = "Nome e obrigatorio")
    private String name;

    @Email(message = "Email invalido")
    @NotBlank(message = "Email e obrigatorio")
    private String email;

    @NotBlank(message = "Matricula e obrigatoria")
    private String employeeCode;

    @NotBlank(message = "Cargo e obrigatorio")
    private String position;

    @NotNull(message = "Data de admissao e obrigatoria")
    private LocalDate hiringDate;

    private LocalDate birthDate;
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String contractType;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    private Long departmentId;
    private Long teamId;
    private Long managerEmployeeId;

    @NotBlank(message = "Username e obrigatorio")
    private String username;

    private String password;
    private Boolean active;
}
