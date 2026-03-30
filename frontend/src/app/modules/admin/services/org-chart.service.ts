import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrgChartNode } from '../../../shared/models/hr.model';

@Injectable({ providedIn: 'root' })
export class OrgChartService {
  private readonly apiUrl = `${environment.apiUrl}/api/orgchart`;

  constructor(private http: HttpClient) {}

  list(): Observable<OrgChartNode[]> {
    return this.http.get<OrgChartNode[]>(this.apiUrl);
  }
}
