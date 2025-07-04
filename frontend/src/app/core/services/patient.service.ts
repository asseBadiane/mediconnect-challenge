import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { ApiService } from './api.service';

import { Patient, PatientRequest, PatientResponse } from '../../shared/models/patient.model';
import { environment } from '../../../environments/ environment';
import { PagedResponse } from '../../shared/models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private baseUrl = environment.patientApiUrl;

  constructor(private apiService: ApiService) {}

  // Get all patients with pagination
  getAllPatients(): Observable<PagedResponse<PatientResponse>> {
    
    return this.apiService.get<PagedResponse<PatientResponse>>(`${this.baseUrl}/Patients`).pipe(
      // tap(data => console.log('Patients fetched successfully:', data))
    );
  }

  // Create a new patient
  createPatient(patient: PatientRequest): Observable<PatientResponse> {
    return this.apiService.post<PatientResponse>(`${this.baseUrl}/Patient`, patient).pipe(
      // tap(data => console.log('Patient created successfully:', data))
    );
  }


  getPatientByFhirId(fhirId: string): Observable<PatientResponse> {
    // console.log('Fetching patient with FHIR ID:', fhirId); // Add debug log
    return this.apiService.get<PatientResponse>(`${this.baseUrl}/Patient/${fhirId}`)
    // .pipe(
    //   tap(data => console.log('Patient fetched successfully by FHIR ID:', data)),
    //   catchError(error => {
    //     console.error('Error fetching patient:', error);
    //     return throwError(() => new Error('Failed to fetch patient'));
    //   })
    // );
  }

  // Update patient
  updatePatient(fhirId: string, patient: PatientRequest): Observable<PatientResponse> {
    return this.apiService.put<PatientResponse>(`${this.baseUrl}/Patient/${fhirId}`, patient);
  }

  // Delete patient (soft delete)
  deletePatient(fhirId: string): Observable<void> {
    return this.apiService.delete<void>(`${this.baseUrl}/Patient/${fhirId}`);
  }

  // Check if patient exists by identifier
  existsByIdentifier(identifier: string): Observable<boolean> {
    return this.apiService.get<boolean>(`${this.baseUrl}/Patient/identifier/${identifier}/exists`);
  }

  // Health check
  healthCheck(): Observable<string> {
    return this.apiService.get<string>(`${this.baseUrl}/health`);
  }

  // Search patients (you may need to implement this endpoint in your backend)
  searchPatients(searchTerm: string): Observable<PatientResponse[]> {
    return this.apiService.get<PatientResponse[]>(`${this.baseUrl}/Patient/search`, { q: searchTerm });
  }
}