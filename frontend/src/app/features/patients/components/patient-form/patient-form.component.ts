import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PatientRequest, PatientResponse } from '../../../../shared/models/patient.model';
import { PatientService } from '../../../../core/services/patient.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { Router, ActivatedRoute } from '@angular/router';

import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-patient-form',
  templateUrl: './patient-form.component.html',
  styleUrls: ['./patient-form.component.scss'],
  imports: [
   ReactiveFormsModule,
   CommonModule
]
})
export class PatientFormComponent implements OnInit {
  @Input() editMode = false;
  @Input() patientData?: PatientResponse;
  @Output() formSubmitted = new EventEmitter<PatientRequest>();
  @Output() formCancelled = new EventEmitter<void>();

  patientForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  fhirId = '';

  genderOptions = [
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
    { value: 'OTHER', label: 'Other' }
  ];

  constructor(
    private fb: FormBuilder,
    private patientService: PatientService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.patientForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['fhirId'] && params['fhirId'] !== 'new') {
        this.fhirId = params['fhirId'];
        this.editMode = true;
        this.loadPatient(this.fhirId);
      }
    });

    if (this.patientData && this.editMode) {
      this.populateForm(this.patientData);
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      givenName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      familyName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      identifier: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\-\.]{1,64}$/)]],
      gender: ['', Validators.required],
      birthDate: ['', Validators.required],
      phone: ['', [Validators.pattern(/^[\+]?[1-9][\d]{0,15}$/)]],
      email: ['', [Validators.email]],
      address: this.fb.group({
        line: [''],
        city: [''],
        state: [''],
        postalCode: ['', [Validators.pattern(/^[0-9]{5}(-[0-9]{4})?$/)]],
        country: ['']
      })
    });
  }

  private loadPatient(fhirId: string): void {
    this.isLoading = true;
    
    this.patientService.getPatientByFhirId(fhirId).subscribe({
      next: (patient) => {
        this.patientData = patient;
        this.populateForm(patient);
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError('Failed to load patient data');
        console.error('Error loading patient:', error);
        this.isLoading = false;
        this.router.navigate(['/patients']);
      }
    });
  }

  private populateForm(patient: PatientResponse): void {
    this.patientForm.patchValue({
      givenName: patient.givenName,
      familyName: patient.familyName,
      identifier: patient.identifierValue || '',
      gender: patient.gender,
      birthDate: patient.birthDate,
      phone: patient.phone,
      email: patient.email,
      address: {
        line: patient.addressLine1?.trim() || '',
        city: patient.addressCity?.trim() || '',
        postalCode: patient.addressPostalCode?.trim() || '',
        country: patient.addressCountry?.trim() || ''
      }
    });
  }

  onSubmit(): void {
    if (this.patientForm.valid) {
      this.isSubmitting = true;
      const formData = this.patientForm.value;
      
      const patientRequest: PatientRequest = {
        givenName: formData.givenName,
        familyName: formData.familyName,
        identifierValue: formData.identifier,
        gender: formData.gender,
        birthDate: formData.birthDate,
        phone: formData.phone || undefined,
        email: formData.email || undefined,
        addressCity: this.hasAddressData(formData.address) ? formData.address.city : undefined,
        addressLine1: this.hasAddressData(formData.address) ? formData.address.line : undefined,
        addressPostalCode: this.hasAddressData(formData.address) ? formData.address.postalCode : undefined,
        addressCountry: this.hasAddressData(formData.address) ? formData.address.country : undefined
      };

      if (this.editMode && this.fhirId) {
        this.updatePatient(patientRequest);
      } else {
        this.createPatient(patientRequest);
      }
    } else {
      this.markFormGroupTouched();
      this.notificationService.showError('Please fill in all required fields correctly');
    }
  }

  private createPatient(patientRequest: PatientRequest): void {
    this.patientService.createPatient(patientRequest).subscribe({
      next: (response) => {
        this.notificationService.showSuccess('Patient created successfully');
        this.formSubmitted.emit(patientRequest);
        this.router.navigate(['/patients', response.fhirId]);
        this.isSubmitting = false;
      },
      error: (error) => {
        this.handleSubmitError(error);
      }
    });
  }

  private updatePatient(patientRequest: PatientRequest): void {
    if (!this.fhirId) return;

    this.patientService.updatePatient(this.fhirId, patientRequest).subscribe({
      next: (response) => {
        this.notificationService.showSuccess('Patient updated successfully');
        this.formSubmitted.emit(patientRequest);
        this.router.navigate(['/patients', response.fhirId]);
        this.isSubmitting = false;
      },
      error: (error) => {
        this.handleSubmitError(error);
      }
    });
  }

  private handleSubmitError(error: any): void {
    this.isSubmitting = false;
    
    if (error.status === 409) {
      this.notificationService.showError('A patient with this identifier already exists');
    } else if (error.status === 400) {
      this.notificationService.showError('Invalid patient data. Please check your inputs');
    } else {
      this.notificationService.showError('Failed to save patient. Please try again');
    }
    
    console.error('Error saving patient:', error);
  }

  private hasAddressData(address: any): boolean {
    return address.line || address.city || address.state || address.postalCode || address.country;
  }

  private markFormGroupTouched(): void {
    Object.keys(this.patientForm.controls).forEach(key => {
      const control = this.patientForm.get(key);
      if (control) {
        control.markAsTouched();
        
        if (control instanceof FormGroup) {
          Object.keys(control.controls).forEach(nestedKey => {
            control.get(nestedKey)?.markAsTouched();
          });
        }
      }
    });
  }

  onCancel(): void {
    this.formCancelled.emit();
    this.router.navigate(['/patients']);
  }

  onReset(): void {
    if (this.editMode && this.patientData) {
      this.populateForm(this.patientData);
    } else {
      this.patientForm.reset();
    }
    this.notificationService.showInfo('Form has been reset');
  }

  // Helper methods for template
  getFieldError(fieldName: string): string {
    const field = this.patientForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) return `${this.getFieldLabel(fieldName)} is required`;
      if (field.errors['minlength']) return `${this.getFieldLabel(fieldName)} must be at least ${field.errors['minlength'].requiredLength} characters`;
      if (field.errors['maxlength']) return `${this.getFieldLabel(fieldName)} must not exceed ${field.errors['maxlength'].requiredLength} characters`;
      if (field.errors['email']) return 'Please enter a valid email address';
      if (field.errors['pattern']) return `Please enter a valid ${this.getFieldLabel(fieldName)}`;
    }
    return '';
  }

  getAddressFieldError(fieldName: string): string {
    const field = this.patientForm.get(`address.${fieldName}`);
    if (field?.errors && field.touched) {
      if (field.errors['pattern']) return `Please enter a valid ${this.getFieldLabel(fieldName)}`;
    }
    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      givenName: 'First Name',
      familyName: 'Last Name',
      identifier: 'Identifier',
      gender: 'Gender',
      birthDate: 'Birth Date',
      phone: 'Phone Number',
      email: 'Email',
      postalCode: 'Postal Code'
    };
    return labels[fieldName] || fieldName;
  }

  checkIdentifierAvailability(): void {
    const identifier = this.patientForm.get('identifier')?.value;
    if (identifier && identifier.length >= 3) {
      this.patientService.existsByIdentifier(identifier).subscribe({
        next: (exists) => {
          if (exists && (!this.editMode || this.patientData?.identifierValue !== identifier)) {
            this.patientForm.get('identifier')?.setErrors({ 'identifierTaken': true });
            this.notificationService.showWarning('This identifier is already taken');
          }
        },
        error: (error) => {
          console.error('Error checking identifier:', error);
        }
      });
    }
  }
}