import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { FhirCondition } from '../../../../shared/models/condition.model';
import { PatientResponse } from '../../../../shared/models/patient.model';
import { PatientService } from '../../../../core/services/patient.service';
import { ConditionService } from '../../../../core/services/condition.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-patient-profile-page',
  templateUrl: './patient-profile-page.component.html',
  styleUrls: ['./patient-profile-page.component.scss'],
  standalone: true,
  imports: [CommonModule], // Only CommonModule needed here
})
export class PatientProfilePageComponent implements OnInit, OnDestroy {
  patient?: PatientResponse;
  conditions: FhirCondition[] = [];
  isLoading = false;
  isLoadingConditions = false;
  fhirId!: string;
  
  private destroy$ = new Subject<void>();

  // Tab management
  activeTab = 'overview';
  
  // Action states
  isDeleting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private conditionService: ConditionService,
    private notificationService: NotificationService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

 ngOnInit(): void {
  this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
    this.fhirId = params['fhirId'];
    // console.log('Received FHIR ID from route:', this.fhirId, 'Type:', typeof this.fhirId); // Enhanced debug log
    if (this.fhirId && typeof this.fhirId === 'string') {
      this.loadPatientData();
    } else {
      console.warn('No valid FHIR ID found in route params:', params);
      this.notificationService.showError('No valid patient ID provided');
      this.router.navigate(['/patients']);
    }
  });
}

  private loadPatientData(): void {
    this.isLoading = true;
    
    forkJoin({
      patient: this.patientService.getPatientByFhirId(this.fhirId),
      conditions: this.conditionService.getConditionsByPatient(this.fhirId)
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (data) => {
        // console.log('ForkJoin data received:', data); // Add debug log
        this.patient = data.patient;
        this.conditions = data.conditions;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading patient data:', error); // Add detailed error log
        this.notificationService.showError('Failed to load patient data');
        this.isLoading = false;
        this.router.navigate(['/patients']);
      }
    });
  }

  // Navigation methods
  onEditPatient(): void {
    this.router.navigate(['/patients', this.fhirId, 'edit']);
  }

  onDeletePatient(): void {
    if (!this.patient) return;

    const confirmMessage = `Are you sure you want to delete patient "${this.getFullName()}"? This action cannot be undone.`;
    
    if (confirm(confirmMessage)) {
      this.isDeleting = true;
      
      this.patientService.deletePatient(this.fhirId).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => {
          this.notificationService.showSuccess('Patient deleted successfully');
          this.router.navigate(['/patients']);
        },
        error: (error) => {
          console.error('Error deleting patient:', error);
          this.notificationService.showError('Failed to delete patient');
          this.isDeleting = false;
        }
      });
    }
  }

  onAddCondition(): void {
    this.router.navigate(['/patients', this.fhirId, 'conditions', 'new']);
  }

  onViewCondition(conditionId: string): void {
    this.router.navigate(['/patients', this.fhirId, 'conditions', conditionId]);
  }

  onEditCondition(conditionId: string): void {
    this.router.navigate(['/patients', this.fhirId, 'conditions', conditionId, 'edit']);
  }

  onBackToList(): void {
    this.router.navigate(['/patients']);
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  // TrackBy function for ngFor performance
  trackByConditionId(index: number, condition: FhirCondition): string {
    return condition.id || index.toString();
  }

  // Utility methods for template
  getAge(): number | null {
    if (!this.patient?.birthDate) return null;
    
    const birthDate = new Date(this.patient.birthDate);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
  }

  getFullName(): string {
    if (!this.patient) return '';
    return `${this.patient.givenName || ''} ${this.patient.familyName || ''}`.trim();
  }

  getFullAddress(): string {
    if (!this.patient) return '';
    
    const addressParts = [
      this.patient.addressLine1,
      this.patient.addressCity,
      this.patient.addressPostalCode,
      this.patient.addressCountry
    ].filter(part => part && part.trim());
    
    return addressParts.join(', ');
  }

  getGenderDisplay(): string {
    if (!this.patient?.gender) return 'Not specified';
    
    const genderMap: { [key: string]: string } = {
      'MALE': 'Male',
      'FEMALE': 'Female',
      'OTHER': 'Other'
    };
    
    return genderMap[this.patient.gender] || this.patient.gender;
  }

  // Fixed condition-related methods to work with FhirCondition model
  private isConditionActive(condition: FhirCondition): boolean {
    return condition.clinicalStatus?.coding?.some(c => c.code === 'active') ?? false;
  }

  getActiveConditionsCount(): number {
    return this.conditions.filter(condition => this.isConditionActive(condition)).length;
  }

  getInactiveConditionsCount(): number {
    return this.conditions.filter(condition => !this.isConditionActive(condition)).length;
  }

  // Fixed to work with FhirCondition structure
  getConditionDisplay(condition: FhirCondition): string {
    if (condition.code?.coding?.length > 0) {
      return condition.code.coding[0].display || condition.code.coding[0].code || 'Unknown Condition';
    }
    return 'Unknown Condition';
  }
// Fixed to work with FhirCondition structure

  getConditionStatusDisplay(condition: FhirCondition): string {
    if (condition.clinicalStatus?.coding && condition.clinicalStatus.coding.length > 0) {
      return condition.clinicalStatus.coding[0].code || 'Unknown';
    }
    return 'Unknown';
  }

  getConditionSeverityDisplay(condition: FhirCondition): string {
    if (condition.severity?.coding && condition.severity.coding.length > 0) {
      return condition.severity.coding[0].display || condition.severity.coding[0].code || '';
    }
    return '';
  }

  getConditionCategoryDisplay(condition: FhirCondition): string {
    // Note: Category is not in your FhirCondition model
    // You might need to add it or remove this from template
    return 'Not specified';
  }

  getConditionNoteDisplay(condition: FhirCondition): string {
    if (condition.note && condition.note.length > 0) {
      return condition.note[0].text || '';
    }
    return '';
  }
  formatDate(dateString: string): string {
    if (!dateString) return '';
    
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return dateString;
    }
  }

  getConditionSeverityClass(condition: FhirCondition): string {
    const severity = this.getConditionSeverityDisplay(condition).toLowerCase();
    
    const severityMap: { [key: string]: string } = {
      'mild': 'severity-mild',
      'moderate': 'severity-moderate',
      'severe': 'severity-severe'
    };
    
    return severityMap[severity] || 'severity-unknown';
  }

  getConditionStatusClass(condition: FhirCondition): string {
    const status = this.getConditionStatusDisplay(condition).toLowerCase();
    
    const statusMap: { [key: string]: string } = {
      'active': 'status-active',
      'inactive': 'status-inactive',
      'resolved': 'status-resolved',
      'remission': 'status-remission'
    };
    
    return statusMap[status] || 'status-unknown';
  }
}