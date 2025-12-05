import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Company {
  id?: number;
  cnpj: string;
  razaoSocial: string;
  nomeFantasia: string;
  dataCriacao?: string;
  dataAtualizacao?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private apiUrl = 'http://localhost:8080/api/companies';

  constructor(private http: HttpClient) { }

  listarTodos(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/${id}`);
  }

  criar(company: Company): Observable<Company> {
    return this.http.post<Company>(this.apiUrl, company);
  }

  atualizar(id: number, company: Company): Observable<Company> {
    return this.http.put<Company>(`${this.apiUrl}/${id}`, company);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}