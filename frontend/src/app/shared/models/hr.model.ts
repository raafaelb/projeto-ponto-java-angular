export interface Department {
  id?: number;
  name: string;
  description?: string | null;
  companyId?: number;
}

export interface Team {
  id?: number;
  name: string;
  description?: string | null;
  companyId?: number;
  departmentId?: number | null;
  departmentName?: string | null;
}

export interface OrgChartNode {
  employeeId: number;
  employeeName: string;
  position: string;
  department?: string | null;
  team?: string | null;
  managerEmployeeId?: number | null;
  managerName?: string | null;
}
