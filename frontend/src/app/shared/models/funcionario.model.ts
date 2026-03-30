export interface Employee {
  id?: number;
  name: string;
  email: string;
  position: string;
  hiringDate: string;
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
