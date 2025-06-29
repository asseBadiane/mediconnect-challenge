import { Component, OnInit } from '@angular/core';
import { PatientService } from '../../../../core/services/patient.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { PatientResponse } from '../../../../shared/models/patient.model';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';

import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-patient-list',
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatSpinner,
    MatTableModule,
    MatTooltipModule,
    MatButtonModule,
    MatChipsModule,
  ]
})
export class PatientListComponent implements OnInit {
  displayedColumns: string[] = ['name', 'identifier', 'gender', 'birthDate', 'phone', 'email', 'actions'];
  dataSource = new MatTableDataSource<PatientResponse>();
  
  isLoading = false;
  totalElements = 0;
  searchTerm = '';

  constructor(
    private patientService: PatientService,
    private notificationService: NotificationService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.isLoading = true;
    
    this.patientService.getAllPatients().subscribe({
      next: (response) => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError('Failed to load patients');
        this.isLoading = false;
        console.error('Error loading patients:', error);
      }
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  viewPatient(patient: PatientResponse): void {
    this.router.navigate(['/patients', patient.fhirId]);
  }

  editPatient(patient: PatientResponse): void {
    this.router.navigate(['/patients', patient.fhirId, 'edit']);
  }

  deletePatient(patient: PatientResponse): void {
    if (confirm(`Are you sure you want to delete patient ${patient.givenName} ${patient.familyName}?`)) {
      this.patientService.deletePatient(patient.fhirId).subscribe({
        next: () => {
          this.notificationService.showSuccess('Patient deleted successfully');
          this.loadPatients();
        },
        error: (error) => {
          this.notificationService.showError('Failed to delete patient');
          console.error('Error deleting patient:', error);
        }
      });
    }
  }

  addNewPatient(): void {
    this.router.navigate(['/patients/new']);
  }

  getGenderDisplay(gender: string): string {
    switch (gender) {
      case 'MALE': return 'Male';
      case 'FEMALE': return 'Female';
      case 'OTHER': return 'Other';
      default: return 'Unknown';
    }
  }

  getAge(birthDate: string): number {
    if (!birthDate) return 0;
    
    const birth = new Date(birthDate);
    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    
    return age;
  }
}