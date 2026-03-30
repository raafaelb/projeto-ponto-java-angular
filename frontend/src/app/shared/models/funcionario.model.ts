export interface Employee {
  id?: number;
  name: string;
  email: string;
  employeeCode: string;
  position: string;
  hiringDate: string;
  birthDate?: string | null;
  phone?: string | null;
  address?: string | null;
  emergencyContactName?: string | null;
  emergencyContactPhone?: string | null;
  contractType?: string | null;
  contractStartDate?: string | null;
  contractEndDate?: string | null;
  departmentId?: number | null;
  departmentName?: string | null;
  teamId?: number | null;
  teamName?: string | null;
  managerEmployeeId?: number | null;
  managerName?: string | null;
  username: string;
  password?: string;
  active?: boolean;
  companyId?: number;
  companyName?: string;
  userId?: number;
}

export interface WorkdayCurrentStatus {
  clockedIn: boolean;
  clockInAt: string | null;
  workedMinutesUntilNow: number;
}

export interface WorkdayRecord {
  id: number;
  clockIn: string;
  clockOut: string | null;
  workedMinutes: number;
  observacao?: string;
}

export interface WorkdaySummary {
  records: WorkdayRecord[];
  totalWorkedMinutes: number;
}
