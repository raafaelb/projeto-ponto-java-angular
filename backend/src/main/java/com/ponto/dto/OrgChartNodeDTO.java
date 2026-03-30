package com.ponto.dto;

import lombok.Data;

@Data
public class OrgChartNodeDTO {
    private Long employeeId;
    private String employeeName;
    private String position;
    private String department;
    private String team;
    private Long managerEmployeeId;
    private String managerName;
}
