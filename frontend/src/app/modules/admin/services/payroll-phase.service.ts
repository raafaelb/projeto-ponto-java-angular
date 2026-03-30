import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PayrollCycle, Payslip } from '../../../shared/models/phase4.model';

@Injectable({ providedIn: 'root' })
export class PayrollPhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/payroll`;

  constructor(private http: HttpClient) {}

  createCycle(payload: PayrollCycle): Observable<PayrollCycle> {
    return this.http.post<PayrollCycle>(`${this.apiUrl}/cycles`, payload);
  }

  listCycles(): Observable<PayrollCycle[]> {
    return this.http.get<PayrollCycle[]>(`${this.apiUrl}/cycles`);
  }

  closeCycle(id: number): Observable<PayrollCycle> {
    return this.http.post<PayrollCycle>(`${this.apiUrl}/cycles/${id}/close`, {});
  }

  createPayslip(payload: Payslip): Observable<Payslip> {
    return this.http.post<Payslip>(`${this.apiUrl}/payslips`, payload);
  }

  listCompanyPayslips(): Observable<Payslip[]> {
    return this.http.get<Payslip[]>(`${this.apiUrl}/payslips/company`);
  }

  listOwnPayslips(): Observable<Payslip[]> {
    return this.http.get<Payslip[]>(`${this.apiUrl}/payslips/me`);
  }
}
