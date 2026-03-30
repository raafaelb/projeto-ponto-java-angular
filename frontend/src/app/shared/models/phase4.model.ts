export type PayrollCycleStatus = 'DRAFT' | 'CLOSED';
export type BenefitEnrollmentStatus = 'PENDING' | 'ENROLLED' | 'CANCELLED';
export type PolicyAckStatus = 'REQUIRED' | 'ACKNOWLEDGED';

export interface PayrollCycle {
  id?: number;
  periodStart: string;
  periodEnd: string;
  paymentDate?: string | null;
  status?: PayrollCycleStatus;
  notes?: string | null;
  createdAt?: string;
}

export interface Payslip {
  id?: number;
  payrollCycleId: number;
  employeeId: number;
  employeeName?: string;
  grossPay: number;
  deductions: number;
  taxWithheld: number;
  overtimePay: number;
  bonusPay: number;
  netPay?: number;
  createdAt?: string;
}

export interface BenefitPlan {
  id?: number;
  name: string;
  description?: string | null;
  monthlyEmployerCost: number;
  monthlyEmployeeCost: number;
  active?: boolean;
  createdAt?: string;
}

export interface BenefitEnrollment {
  id?: number;
  benefitPlanId: number;
  benefitPlanName?: string;
  employeeId?: number;
  employeeName?: string;
  status?: BenefitEnrollmentStatus;
  startDate?: string | null;
  endDate?: string | null;
  reviewComment?: string | null;
  reviewedBy?: string | null;
  reviewedAt?: string | null;
  createdAt?: string;
}

export interface PolicyDocument {
  id?: number;
  title: string;
  version: string;
  effectiveDate: string;
  contentSummary?: string | null;
  active?: boolean;
  createdAt?: string;
}

export interface PolicyAcknowledgment {
  id?: number;
  policyDocumentId: number;
  policyTitle?: string;
  policyVersion?: string;
  status?: PolicyAckStatus;
  acknowledgedAt?: string | null;
  createdAt?: string;
}

export interface ComplianceAuditEvent {
  id: number;
  eventType: string;
  message: string;
  severity: string;
  entityType?: string | null;
  entityId?: number | null;
  createdBy?: string | null;
  createdAt?: string;
}
