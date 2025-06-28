package com.mediconnect.condition_api.service;

import com.mediconnect.condition_api.client.PatientClient;
import com.mediconnect.condition_api.dto.ConditionDto;
import com.mediconnect.condition_api.entity.ConditionEntity;
import com.mediconnect.condition_api.exception.ConditionNotFoundException;
import com.mediconnect.condition_api.exception.PatientNotFoundException;

import com.mediconnect.condition_api.mapper.ConditionMapper;
import com.mediconnect.condition_api.repository.ConditionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConditionService {
    
    private final ConditionRepository conditionRepository;
    private final ConditionMapper conditionMapper;
    private final PatientClient patientClient;
    

    public Condition createCondition(Condition condition) {
    log.info("Creating new condition for patient: {}", condition.getSubject().getReference());
    
    String patientReference = condition.getSubject().getReference();
    String patientId = extractPatientId(patientReference);
    
    Boolean patientExists = patientClient.patientExists(patientId).block();
    if (Boolean.FALSE.equals(patientExists)) {
        throw new PatientNotFoundException("Patient not found with ID: " + patientId);
    }
    
    if (condition.getId() == null || condition.getId().isEmpty()) {
        condition.setId(UUID.randomUUID().toString()); // Sets raw ID
    }
    
    ConditionEntity entity = conditionMapper.fromFhirCondition(condition);
    ConditionEntity savedEntity = conditionRepository.save(entity);
    
    log.info("Successfully created condition with ID: {}", savedEntity.getFhirId());
    return conditionMapper.toFhirCondition(savedEntity); // Returns with "Condition/" prefix
    }
    
    @Transactional(readOnly = true)
    public Condition getConditionById(String id) {
        log.debug("Fetching condition with ID: {}", id);
        
        ConditionEntity entity = conditionRepository.findByFhirId(id)
                .orElseThrow(() -> new ConditionNotFoundException("Condition not found with ID: " + id));
        
        return conditionMapper.toFhirCondition(entity);
    }
    
    @Transactional(readOnly = true)
    public List<Condition> getConditionsByPatientId(String patientId) {
        log.debug("Fetching conditions for patient: {}", patientId);
        
        // Validate patient exists
        Boolean patientExists = patientClient.patientExists(patientId).block();
        if (Boolean.FALSE.equals(patientExists)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }
        
        List<ConditionEntity> entities = conditionRepository.findByPatientId(patientId);
        
        return entities.stream()
                .map(conditionMapper::toFhirCondition)
                .collect(Collectors.toList());
    }
    
    public Condition updateCondition(String id, Condition condition) {
        log.info("Updating condition with ID: {}", id);
        
        ConditionEntity existingEntity = conditionRepository.findByFhirId(id)
                .orElseThrow(() -> new ConditionNotFoundException("Condition not found with ID: " + id));
        
        // Set the ID to maintain consistency
        condition.setId(id);
        
        // Validate patient exists if patient reference is being updated
        if (condition.getSubject() != null && condition.getSubject().getReference() != null) {
            String patientReference = condition.getSubject().getReference();
            String patientId = extractPatientId(patientReference);
            
            Boolean patientExists = patientClient.patientExists(patientId).block();
            if (Boolean.FALSE.equals(patientExists)) {
                throw new PatientNotFoundException("Patient not found with ID: " + patientId);
            }
        }
        
        // Convert to entity and update
        ConditionEntity updatedEntity = conditionMapper.fromFhirCondition(condition);
        updatedEntity.setId(existingEntity.getId()); // Keep the database ID
        updatedEntity.setCreatedAt(existingEntity.getCreatedAt()); // Keep creation timestamp
        
        ConditionEntity savedEntity = conditionRepository.save(updatedEntity);
        
        log.info("Successfully updated condition with ID: {}", id);
        return conditionMapper.toFhirCondition(savedEntity);
    }
    
    @Transactional(readOnly = true)
    public List<ConditionDto> getAllConditions() {
        log.debug("Fetching all conditions");
        
        return conditionRepository.findAll()
                .stream()
                .map(conditionMapper::toDto)
                .collect(Collectors.toList());
    }
    
    private String extractPatientId(String patientReference) {
        if (patientReference == null) {
            throw new IllegalArgumentException("Patient reference cannot be null");
        }
        
        // Handle both "Patient/123" and "123" formats
        if (patientReference.startsWith("Patient/")) {
            return patientReference.substring("Patient/".length());
        }
        return patientReference;
    }
}