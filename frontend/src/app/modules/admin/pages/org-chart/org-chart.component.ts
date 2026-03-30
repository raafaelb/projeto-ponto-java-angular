import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { OrgChartService } from '../../services/org-chart.service';
import { OrgChartNode } from '../../../../shared/models/hr.model';

@Component({
  selector: 'app-org-chart',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './org-chart.component.html',
  styleUrls: ['./org-chart.component.scss']
})
export class OrgChartComponent implements OnInit {
  nodes: OrgChartNode[] = [];
  grouped: { leader: string; items: OrgChartNode[] }[] = [];

  constructor(private orgChartService: OrgChartService) {}

  ngOnInit(): void {
    this.orgChartService.list().subscribe((nodes) => {
      this.nodes = nodes;
      this.grouped = this.groupByManager(nodes);
    });
  }

  private groupByManager(nodes: OrgChartNode[]): { leader: string; items: OrgChartNode[] }[] {
    const map = new Map<string, OrgChartNode[]>();

    nodes.forEach((node) => {
      const key = node.managerName || 'Sem gestor';
      const current = map.get(key) || [];
      current.push(node);
      map.set(key, current);
    });

    return Array.from(map.entries())
      .map(([leader, items]) => ({ leader, items: items.sort((a, b) => a.employeeName.localeCompare(b.employeeName)) }))
      .sort((a, b) => a.leader.localeCompare(b.leader));
  }
}
