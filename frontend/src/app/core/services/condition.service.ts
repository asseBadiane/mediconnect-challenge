import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Condition, ConditionDto, FhirCondition } from '../../shared/models/condition.model';
import { environment } from '../../../environments/ environment';

@Injectable({
  providedIn: 'root'
})
export class ConditionService {
  private baseUrl = environment.conditionApiUrl;

  constructor(private apiService: ApiService) {}

  // Create a new condition
  createCondition(condition: FhirCondition): Observable<FhirCondition> {
    return this.apiService.post<FhirCondition>(`${this.baseUrl}/Condition`, condition);
  }

  // Get condition by ID
  getConditionById(id: string): Observable<FhirCondition> {
    return this.apiService.get<FhirCondition>(`${this.baseUrl}/Condition/${id}`);
  }

  // Get conditions by patient ID
  getConditionsByPatientId(patientId: string): Observable<FhirCondition[]> {
    return this.apiService.get<FhirCondition[]>(`${this.baseUrl}/Condition`, { patient: patientId });
  }

  // Update condition
  updateCondition(id: string, condition: FhirCondition): Observable<FhirCondition> {
    return this.apiService.put<FhirCondition>(`${this.baseUrl}/Condition/${id}`, condition);
  }

  // Get all conditions (admin)
  getAllConditions(): Observable<ConditionDto[]> {
    return this.apiService.get<ConditionDto[]>(`${this.baseUrl}/Condition/all`);
  }

  // Helper method to create FHIR Condition object
  createFhirCondition(
    patientReference: string,
    codeSystem: string,
    codeValue: string,
    codeDisplay: string,
    onsetDateTime?: string,
    note?: string,
    severity?: { system?: string; code?: string; display?: string }
  ): FhirCondition {
    const condition: FhirCondition = {
      resourceType: 'Condition',
      subject: {
        reference: `Patient/${patientReference}`
      },
      code: {
        coding: [{
          system: codeSystem,
          code: codeValue,
          display: codeDisplay
        }]
      }
    };

    if (onsetDateTime) {
      condition.onsetDateTime = onsetDateTime;
    }

    if (note) {
      condition.note = [{ text: note }];
    }

    if (severity) {
      condition.severity = {
        coding: [severity]
      };
    }

    // Set default clinical status
    condition.clinicalStatus = {
      coding: [{
        system: 'http://terminology.hl7.org/CodeSystem/condition-clinical',
        code: 'active'
      }]
    };

    // Set default verification status
    condition.verificationStatus = {
      coding: [{
        system: 'http://terminology.hl7.org/CodeSystem/condition-ver-status',
        code: 'confirmed'
      }]
    };

    return condition;
  }
}