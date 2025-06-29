import { Component, OnInit } from '@angular/core';

import { forkJoin } from 'rxjs';
import { PatientResponse } from '../../../../shared/models/patient.model';
import { ConditionDto } from '../../../../shared/models/condition.model';
import { PatientService } from '../../../../core/services/patient.service';
import { ConditionService } from '../../../../core/services/condition.service';

import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
// import { HttpClientModule } from '@angular/common/http';
// 




interface DashboardStats {
  totalPatients: number;
  totalConditions: number;
  recentPatients: PatientResponse[];
  recentConditions: ConditionDto[];
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
   imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatTooltipModule,
    MatBadgeModule,
    MatSidenavModule,
    MatListModule,
    MatExpansionModule,
    MatDividerModule,
    MatCardModule,
    MatProgressSpinnerModule,
    // HttpClientModule
  ],
  standalone: true // Set to false if you are using NgModule
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalPatients: 0,
    totalConditions: 0,
    recentPatients: [],
    recentConditions: []
  };
  
  isLoading = true;
  error: string | null = null;

  // Chart data for conditions by month
  conditionChartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [{
      label: 'New Conditions',
      data: [12, 19, 3, 5, 2, 3],
      borderColor: '#2196F3',
      backgroundColor: 'rgba(33, 150, 243, 0.1)',
      tension: 0.4
    }]
  };

  constructor(
    private patientService: PatientService,
    private conditionService: ConditionService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    this.isLoading = true;
    this.error = null;

    forkJoin({
      patients: this.patientService.getAllPatients(),
      conditions: this.conditionService.getAllConditions()
    }).subscribe({
      next: (data) => {
        this.stats.totalPatients = data.patients.totalElements;
        this.stats.totalConditions = data.conditions.length;
        this.stats.recentPatients = data.patients.content.slice(0, 5);
        this.stats.recentConditions = data.conditions.slice(0, 5);
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load dashboard data';
        this.isLoading = false;
        console.error('Dashboard error:', error);
      }
    });
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}