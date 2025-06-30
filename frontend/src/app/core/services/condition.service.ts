import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/ environment';
import { ConditionDto, FhirCondition } from '../../shared/models/condition.model';


@Injectable({
  providedIn: 'root'
})
export class ConditionService {
  private readonly baseUrl = `${environment.conditionApiUrl}`;
  private conditionsSubject = new BehaviorSubject<ConditionDto[]>([]);
  public conditions$ = this.conditionsSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getHttpOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/fhir+json',
        'Accept': 'application/fhir+json'
      })
    };
  }

  /**
   * Create a new condition
   */
  createCondition(condition: FhirCondition): Observable<FhirCondition> {
    return this.http.post<FhirCondition>(
      `${this.baseUrl}/Condition`,
      condition,
      this.getHttpOptions()
    ).pipe(
      tap(() => this.refreshConditions())
    );
  }

  /**
   * Get condition by ID
   */
  getConditionById(id: string): Observable<FhirCondition> {
    return this.http.get<FhirCondition>(
      `${this.baseUrl}/Condition/${id}`,
      this.getHttpOptions()
    );
  }

  /**
   * Get conditions by patient ID
   */
  getConditionsByPatient(patientId: string): Observable<FhirCondition[]> {
    const params = new HttpParams().set('patient', patientId);
    return this.http.get<FhirCondition[]>(
      `${this.baseUrl}/Condition`,
      { ...this.getHttpOptions(), params }
    );
  }

  /**
   * Update an existing condition
   */
  updateCondition(id: string, condition: FhirCondition): Observable<FhirCondition> {
    return this.http.put<FhirCondition>(
      `${this.baseUrl}/Condition/${id}`,
      condition,
      this.getHttpOptions()
    ).pipe(
      tap(() => this.refreshConditions())
    );
  }

  /**
   * Get all conditions (for admin/list view)
   */
  getAllConditions(): Observable<ConditionDto[]> {
    return this.http.get<ConditionDto[]>(
      `${this.baseUrl}/Condition/all`,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        })
      }
    ).pipe(
      tap(conditions => this.conditionsSubject.next(conditions))
    );
  }

  /**
   * Refresh conditions list
   */
  refreshConditions(): void {
    this.getAllConditions().subscribe();
  }

  /**
   * Helper method to convert ConditionDto to FhirCondition
   */
  convertToFhirCondition(conditionDto: ConditionDto): FhirCondition {
    return {
      resourceType: 'Condition',
      id: conditionDto.fhirId,
      subject: {
        reference: conditionDto.patientReference
      },
      code: {
        coding: [{
          system: conditionDto.codeSystem,
          code: conditionDto.codeValue,
          display: conditionDto.codeDisplay
        }]
      },
      onsetDateTime: conditionDto.onsetDateTime,
      clinicalStatus: conditionDto.clinicalStatus ? {
        coding: [{
          system: 'http://terminology.hl7.org/CodeSystem/condition-clinical',
          code: conditionDto.clinicalStatus
        }]
      } : undefined,
      verificationStatus: conditionDto.verificationStatus ? {
        coding: [{
          system: 'http://terminology.hl7.org/CodeSystem/condition-ver-status',
          code: conditionDto.verificationStatus
        }]
      } : undefined,
      severity: (conditionDto.severitySystem || conditionDto.severityCode || conditionDto.severityDisplay) ? {
        coding: [{
          system: conditionDto.severitySystem,
          code: conditionDto.severityCode,
          display: conditionDto.severityDisplay
        }]
      } : undefined,
      note: conditionDto.note ? [{
        text: conditionDto.note
      }] : undefined
    };
  }

  /**
   * Helper method to create a basic FhirCondition structure
   */
  createBasicFhirCondition(patientReference: string): FhirCondition {
    return {
      resourceType: 'Condition',
      subject: {
        reference: patientReference
      },
      code: {
        coding: [{
          system: 'http://snomed.info/sct',
          code: '',
          display: ''
        }]
      },
      clinicalStatus: {
        coding: [{
          system: 'http://terminology.hl7.org/CodeSystem/condition-clinical',
          code: 'active'
        }]
      },
      verificationStatus: {
        coding: [{
          system: 'http://terminology.hl7.org/CodeSystem/condition-ver-status',
          code: 'confirmed'
        }]
      }
    };
  }

  /**
   * Get common condition codes (for dropdown/autocomplete)
   */
  getCommonConditionCodes(): Array<{code: string, display: string, system: string}> {
    return [
      { code: '38341003', display: 'Hypertensive disorder', system: 'http://snomed.info/sct' },
      { code: '73211009', display: 'Diabetes mellitus', system: 'http://snomed.info/sct' },
      { code: '195967001', display: 'Asthma', system: 'http://snomed.info/sct' },
      { code: '13645005', display: 'Chronic obstructive lung disease', system: 'http://snomed.info/sct' },
      { code: '53741008', display: 'Coronary arteriosclerosis', system: 'http://snomed.info/sct' },
      { code: '44054006', display: 'Diabetes mellitus type 2', system: 'http://snomed.info/sct' },
      { code: '427089005', display: 'Diabetes mellitus type 1', system: 'http://snomed.info/sct' },
      { code: '370247008', display: 'Facial laceration', system: 'http://snomed.info/sct' },
      { code: '39065001', display: 'Burn of skin', system: 'http://snomed.info/sct' },
      { code: '125605004', display: 'Fracture of bone', system: 'http://snomed.info/sct' }
    ];
  }

  /**
   * Get severity options
   */
  getSeverityOptions(): Array<{code: string, display: string, system: string}> {
    return [
      { code: '24484000', display: 'Severe', system: 'http://snomed.info/sct' },
      { code: '6736007', display: 'Moderate', system: 'http://snomed.info/sct' },
      { code: '255604002', display: 'Mild', system: 'http://snomed.info/sct' }
    ];
  }

  /**
   * Get clinical status options
   */
  getClinicalStatusOptions(): Array<{code: string, display: string}> {
    return [
      { code: 'active', display: 'Active' },
      { code: 'recurrence', display: 'Recurrence' },
      { code: 'relapse', display: 'Relapse' },
      { code: 'inactive', display: 'Inactive' },
      { code: 'remission', display: 'Remission' },
      { code: 'resolved', display: 'Resolved' }
    ];
  }

  /**
   * Get verification status options
   */
  getVerificationStatusOptions(): Array<{code: string, display: string}> {
    return [
      { code: 'unconfirmed', display: 'Unconfirmed' },
      { code: 'provisional', display: 'Provisional' },
      { code: 'differential', display: 'Differential' },
      { code: 'confirmed', display: 'Confirmed' },
      { code: 'refuted', display: 'Refuted' },
      { code: 'entered-in-error', display: 'Entered in Error' }
    ];
  }
}