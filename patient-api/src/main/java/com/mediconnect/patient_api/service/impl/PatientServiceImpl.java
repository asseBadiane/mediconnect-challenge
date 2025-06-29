package com.mediconnect.patient_api.service.impl;


import com.mediconnect.patient_api.dto.PatientRequestDto;
import com.mediconnect.patient_api.dto.PatientResponseDto;
import com.mediconnect.patient_api.dto.PatientSearchDto;
import com.mediconnect.patient_api.mapper.PatientMapper;
import com.mediconnect.patient_api.model.PatientEntity;
import com.mediconnect.patient_api.service.PatientService;
import com.mediconnect.patient_api.utils.exception.PatientAlreadyExistsException;
import com.mediconnect.patient_api.utils.exception.PatientNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediconnect.patient_api.repository.PatientRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {
    
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    @Override
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {
        log.debug("Creating patient with name: {} {}", requestDto.getGivenName(), requestDto.getFamilyName());
        
        // Check if patient with same identifier already exists
        if (requestDto.getIdentifierValue() != null && 
            patientRepository.existsByIdentifierValue(requestDto.getIdentifierValue())) {
            throw new PatientAlreadyExistsException(
                "Patient with identifier " + requestDto.getIdentifierValue() + " already exists");
        }
        
        PatientEntity entity = patientMapper.toEntity(requestDto);
        PatientEntity savedEntity = patientRepository.save(entity);
        
        log.info("Created patient with FHIR ID: {}", savedEntity.getFhirId());
        return patientMapper.toResponseDto(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientByFhirId(String fhirId) {
        log.debug("Retrieving patient with FHIR ID: {}", fhirId);
        
        PatientEntity entity = patientRepository.findByFhirId(fhirId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with FHIR ID: " + fhirId));
        
        return patientMapper.toResponseDto(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientByIdentifier(String identifier) {
        log.debug("Retrieving patient with identifier: {}", identifier);
        
        PatientEntity entity = patientRepository.findByIdentifierValue(identifier)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with identifier: " + identifier));
        
        return patientMapper.toResponseDto(entity);
    }
    
    
    @Override
    public PatientResponseDto updatePatient(String fhirId, PatientRequestDto requestDto) {
        log.debug("Updating patient with FHIR ID: {}", fhirId);
        
        PatientEntity existingEntity = patientRepository.findByFhirId(fhirId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with FHIR ID: " + fhirId));
        
        // Check if identifier is being changed and if new identifier already exists
        if (requestDto.getIdentifierValue() != null && 
            !requestDto.getIdentifierValue().equals(existingEntity.getIdentifierValue()) &&
            patientRepository.existsByIdentifierValue(requestDto.getIdentifierValue())) {
            throw new PatientAlreadyExistsException(
                "Patient with identifier " + requestDto.getIdentifierValue() + " already exists");
        }
        
        patientMapper.updateEntityFromDto(requestDto, existingEntity);
        PatientEntity savedEntity = patientRepository.save(existingEntity);
        
        log.info("Updated patient with FHIR ID: {}", fhirId);
        return patientMapper.toResponseDto(savedEntity);
    }
    
    @Override
    public void deletePatient(String fhirId) {
        log.debug("Soft deleting patient with FHIR ID: {}", fhirId);
        
        PatientEntity entity = patientRepository.findByFhirId(fhirId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with FHIR ID: " + fhirId));
        
        entity.setActive(false);
        patientRepository.save(entity);
        
        log.info("Soft deleted patient with FHIR ID: {}", fhirId);
    }
    
    
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentifier(String identifier) {
        return patientRepository.existsByIdentifierValue(identifier);
    }
    
    private Pageable createPageable(PatientSearchDto searchDto) {
        Sort sort = Sort.by("familyName").ascending();
        
        if (searchDto.getSort() != null && !searchDto.getSort().isEmpty()) {
            String[] sortParts = searchDto.getSort().split(",");
            if (sortParts.length == 2) {
                Sort.Direction direction = sortParts[1].equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
                sort = Sort.by(direction, sortParts[0]);
            }
        }
        
        return PageRequest.of(
            Math.max(0, searchDto.getPage()),
            Math.min(100, Math.max(1, searchDto.getSize())),
            sort);
    }

    @Override
    public Page<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll(createPageable(new PatientSearchDto())).map(patientMapper::toResponseDto);
    }

}