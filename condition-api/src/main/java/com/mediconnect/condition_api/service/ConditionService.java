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
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

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
        
        // Try to find by both raw ID and full FHIR ID
        String fhirId = id.startsWith("Condition/") ? id : "Condition/" + id;
        String rawId = id.startsWith("Condition/") ? id.substring("Condition/".length()) : id;
        
        log.debug("Searching for condition with fhirId: {} or rawId: {}", fhirId, rawId);
        
        ConditionEntity entity = conditionRepository.findByFhirId(fhirId)
            .or(() -> conditionRepository.findByFhirId(rawId))
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
    
    // Handle both formats: "cond-456" and "Condition/cond-456"
    String fhirId = id.startsWith("Condition/") ? id : "Condition/" + id;
    ConditionEntity existingEntity = conditionRepository.findByFhirId(fhirId)
        .orElseThrow(() -> new ConditionNotFoundException("Condition not found with ID: " + id));
    
    // Mettre à jour les champs de l'entité existante
    updateEntityFromFhirCondition(existingEntity, condition);
    
    // Vérifier le patient si nécessaire
    if (condition.getSubject() != null && condition.getSubject().getReference() != null) {
        String patientReference = condition.getSubject().getReference();
        String patientId = extractPatientId(patientReference);
        
        Boolean patientExists = patientClient.patientExists(patientId).block();
        if (Boolean.FALSE.equals(patientExists)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }
        existingEntity.setPatientReference(patientReference);
    }
    
    // Mettre à jour le code
    if (condition.getCode() != null && !condition.getCode().getCoding().isEmpty()) {
        Coding coding = condition.getCode().getCodingFirstRep();
        existingEntity.setCodeSystem(coding.getSystem());
        existingEntity.setCodeValue(coding.getCode());
        existingEntity.setCodeDisplay(coding.getDisplay());
    }
    
    // Mettre à jour la date d'apparition
    if (condition.getOnsetDateTimeType() != null) {
        existingEntity.setOnsetDateTime(condition.getOnsetDateTimeType().getValue().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
    }
    
    // Mettre à jour les statuts
    if (condition.getClinicalStatus() != null) {
        existingEntity.setClinicalStatus(condition.getClinicalStatus().getCodingFirstRep().getCode());
    }
    if (condition.getVerificationStatus() != null) {
        existingEntity.setVerificationStatus(condition.getVerificationStatus().getCodingFirstRep().getCode());
    }
    
    // Sauvegarder les modifications
    ConditionEntity savedEntity = conditionRepository.save(existingEntity);
    
    log.info("Successfully updated condition with ID: {}", id);
    return conditionMapper.toFhirCondition(savedEntity);
}

    private void updateEntityFromFhirCondition(ConditionEntity entity, Condition condition) {
    // Mettre à jour uniquement les champs fournis
    if (condition.getSubject() != null && condition.getSubject().getReference() != null) {
        entity.setPatientReference(condition.getSubject().getReference());
    }
    
    if (condition.getCode() != null && !condition.getCode().getCoding().isEmpty()) {
        Coding coding = condition.getCode().getCodingFirstRep();
        entity.setCodeSystem(coding.getSystem());
        entity.setCodeValue(coding.getCode());
        entity.setCodeDisplay(coding.getDisplay());
    }
    
    if (condition.getOnsetDateTimeType() != null) {
        entity.setOnsetDateTime(condition.getOnsetDateTimeType().getValue().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
    }
    
    if (condition.getClinicalStatus() != null) {
        entity.setClinicalStatus(condition.getClinicalStatus().getCodingFirstRep().getCode());
    }
    
    if (condition.getVerificationStatus() != null) {
        entity.setVerificationStatus(condition.getVerificationStatus().getCodingFirstRep().getCode());
    }
    
    if (condition.getSeverity() != null && !condition.getSeverity().getCoding().isEmpty()) {
        Coding severityCoding = condition.getSeverity().getCodingFirstRep();
        entity.setSeveritySystem(severityCoding.getSystem());
        entity.setSeverityCode(severityCoding.getCode());
        entity.setSeverityDisplay(severityCoding.getDisplay());
    }
    
    if (condition.getNote() != null && !condition.getNote().isEmpty()) {
        entity.setNote(condition.getNoteFirstRep().getText());
        }
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