export type GoalStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
export type ReviewStatus = 'DRAFT' | 'SUBMITTED' | 'CLOSED';
export type PromotionStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface PerformanceGoal {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  title: string;
  description?: string | null;
  weight: number;
  dueDate?: string | null;
  status?: GoalStatus;
  createdAt?: string;
}

export interface PerformanceReview {
  id?: number;
  employeeId: number;
  employeeName?: string;
  periodStart: string;
  periodEnd: string;
  selfScore?: number | null;
  managerScore?: number | null;
  selfComment?: string | null;
  managerFeedback?: string | null;
  status?: ReviewStatus;
  reviewedBy?: string | null;
  reviewedAt?: string | null;
  createdAt?: string;
}

export interface SalaryAdjustment {
  id?: number;
  employeeId: number;
  employeeName?: string;
  previousSalary?: number;
  newSalary: number;
  effectiveDate: string;
  reason?: string | null;
  status?: ApprovalStatus;
  reviewComment?: string | null;
  reviewedBy?: string | null;
  reviewedAt?: string | null;
  createdAt?: string;
}

export interface BonusRequest {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  amount: number;
  referenceDate: string;
  reason?: string | null;
  status?: ApprovalStatus;
  reviewComment?: string | null;
  reviewedBy?: string | null;
  reviewedAt?: string | null;
  createdAt?: string;
}

export interface CareerLevel {
  id?: number;
  name: string;
  rankOrder: number;
  description?: string | null;
  minSalary?: number | null;
  maxSalary?: number | null;
}

export interface SkillAssessment {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  skillName: string;
  currentLevel: number;
  targetLevel?: number | null;
  lastAssessedDate?: string | null;
  notes?: string | null;
  updatedAt?: string;
}

export interface PromotionRequest {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  fromLevelId?: number | null;
  fromLevelName?: string | null;
  toLevelId: number;
  toLevelName?: string | null;
  justification?: string | null;
  effectiveDate?: string | null;
  status?: PromotionStatus;
  reviewComment?: string | null;
  reviewedBy?: string | null;
  reviewedAt?: string | null;
  createdAt?: string;
}
