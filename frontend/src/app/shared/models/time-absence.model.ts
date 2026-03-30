export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type AbsenceType = 'VACATION' | 'SICK_LEAVE' | 'PERSONAL';

export interface AbsenceRequest {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  type: AbsenceType;
  startDate: string;
  endDate: string;
  reason?: string;
  status?: ApprovalStatus;
  reviewComment?: string;
  reviewedBy?: string;
  reviewedAt?: string;
  createdAt?: string;
}

export interface Holiday {
  id?: number;
  holidayDate: string;
  name: string;
  optionalHoliday?: boolean;
}

export interface OvertimeRequest {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  workDate: string;
  requestedMinutes: number;
  reason?: string;
  status?: ApprovalStatus;
  reviewComment?: string;
  reviewedBy?: string;
  reviewedAt?: string;
  createdAt?: string;
}

export type AnomalyType = 'LATE' | 'MISSING_CLOCK_OUT' | 'ABSENCE';

export interface AttendanceAnomaly {
  id: number;
  employeeId: number;
  employeeName: string;
  occurrenceDate: string;
  type: AnomalyType;
  description: string;
  resolved: boolean;
  resolvedBy?: string;
  resolvedAt?: string;
}

export interface TimeReportRow {
  employeeId: number;
  employeeName: string;
  department: string;
  team: string;
  workedMinutes: number;
  approvedOvertimeMinutes: number;
  absenceDays: number;
  anomalyCount: number;
}

export interface TimeReport {
  rows: TimeReportRow[];
  totalWorkedMinutes: number;
  totalApprovedOvertimeMinutes: number;
  totalAbsenceDays: number;
  totalAnomalies: number;
}
