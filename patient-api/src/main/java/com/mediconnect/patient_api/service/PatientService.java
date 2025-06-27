package com.mediconnect.patient_api.service;

import com.mediconnect.patient_api.dto.PatientRequestDto;
import com.mediconnect.patient_api.dto.PatientSearchDto;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.data.domain.Page;

import com.mediconnect.patient_api.dto.PatientResponseDto;

public interface PatientService {
    
    // CRUD methods
    Page<PatientResponseDto> getAllPatients();
    PatientResponseDto createPatient(PatientRequestDto requestDto);
    
    PatientResponseDto getPatientByFhirId(String fhirId);
    
    PatientResponseDto getPatientByIdentifier(String identifier);
    
    PatientResponseDto updatePatient(String fhirId, PatientRequestDto requestDto);
    
    void deletePatient(String fhirId);

    Page<PatientResponseDto> searchPatients(PatientSearchDto searchDto);

    long getActivePatientCount();
    

}