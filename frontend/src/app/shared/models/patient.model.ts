// ===== MODELS =====

// src/app/shared/models/patient.model.ts
export interface Patient {
  id?: number;
  fhirId: string;
  identifierSystem?: string;
  identifierValue?: string;
  givenName: string;
  familyName: string;
  birthDate?: string;
  gender?: Gender;
  phone?: string;
  email?: string;
  addressLine1?: string;
  addressCity?: string;
  addressPostalCode?: string;
  addressCountry?: string;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface PatientRequest {
  identifierSystem?: string;
  identifierValue?: string;
  givenName: string;
  familyName: string;
  birthDate?: string;
  gender?: Gender;
  phone?: string;
  email?: string;
  addressLine1?: string;
  addressCity?: string;
  addressPostalCode?: string;
  addressCountry?: string;
}

export interface PatientResponse {
  id: number;
  fhirId: string;
  identifierSystem?: string;
  identifierValue?: string;
  givenName: string;
  familyName: string;
  birthDate?: string;
  gender?: Gender;
  phone?: string;
  email?: string;
  addressLine1?: string;
  addressCity?: string;
  addressPostalCode?: string;
  addressCountry?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
  UNKNOWN = 'UNKNOWN'
}
