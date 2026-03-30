export interface AuthUser {
  id: number;
  username: string;
  name: string;
  email: string;
  role: 'ADMIN' | 'COMPANY' | 'EMPLOYEE';
  companyId?: number | null;
  active?: boolean;
}

export interface LoginResponse {
  token: string;
  user: AuthUser;
  expiresIn: number;
  message: string;
}
