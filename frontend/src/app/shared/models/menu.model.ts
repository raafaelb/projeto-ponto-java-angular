export interface MenuItem {
  label: string;
  icon: string;
  route: string;
  permission?: string[]; // ['ADMIN', 'COMPANY', 'EMPLOYEE']
  children?: MenuItem[];
}

export interface UserInfo {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'COMPANY' | 'EMPLOYEE';
  companyId?: number; // Para COMPANY e EMPLOYEE
  avatar?: string;
}