
// src/app/shared/models/condition.model.ts
export interface Condition {
  id?: number;
  fhirId: string;
  patientReference: string;
  codeSystem?: string;
  codeValue?: string;
  codeDisplay?: string;
  onsetDateTime?: string;
  clinicalStatus?: string;
  verificationStatus?: string;
  severitySystem?: string;
  severityCode?: string;
  severityDisplay?: string;
  note?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ConditionDto {
  id: number;
  fhirId: string;
  patientReference: string;
  codeSystem?: string;
  codeValue?: string;
  codeDisplay?: string;
  onsetDateTime?: string;
  clinicalStatus?: string;
  verificationStatus?: string;
  severitySystem?: string;
  severityCode?: string;
  severityDisplay?: string;
  note?: string;
  createdAt: string;
  updatedAt: string;
}

export interface FhirCondition {
  resourceType: 'Condition';
  id?: string;
  subject: {
    reference: string;
  };
  code: {
    coding: Array<{
      system?: string;
      code?: string;
      display?: string;
    }>;
  };
  onsetDateTime?: string;
  clinicalStatus?: {
    coding: Array<{
      system: string;
      code: string;
    }>;
  };
  verificationStatus?: {
    coding: Array<{
      system: string;
      code: string;
    }>;
  };
  severity?: {
    coding: Array<{
      system?: string;
      code?: string;
      display?: string;
    }>;
  };
  note?: Array<{
    text: string;
  }>;
}