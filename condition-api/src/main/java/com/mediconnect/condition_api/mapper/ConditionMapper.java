package com.mediconnect.condition_api.mapper;

import com.mediconnect.condition_api.entity.ConditionEntity;
import com.mediconnect.condition_api.dto.ConditionDto;
import org.hl7.fhir.r4.model.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConditionMapper {
    
    ConditionMapper INSTANCE = Mappers.getMapper(ConditionMapper.class);
    
    @Mapping(source = "fhirId", target = "id")
    ConditionDto toDto(ConditionEntity entity);
    
    @Mapping(source = "id", target = "fhirId")
    ConditionEntity toEntity(ConditionDto dto);
    
    // Custom mapping methods for FHIR Condition
    default Condition toFhirCondition(ConditionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Condition condition = new Condition();
        condition.setId(entity.getFhirId());
        
        // Set patient reference
        if (entity.getPatientReference() != null) {
            condition.getSubject().setReference(entity.getPatientReference());
        }
        
        // Set code
        if (entity.getCodeSystem() != null && entity.getCodeValue() != null) {
            condition.getCode()
                .addCoding()
                .setSystem(entity.getCodeSystem())
                .setCode(entity.getCodeValue())
                .setDisplay(entity.getCodeDisplay());
        }
        
        // Set onset
        if (entity.getOnsetDateTime() != null) {
            condition.setOnset(new org.hl7.fhir.r4.model.DateTimeType(
                java.sql.Timestamp.valueOf(entity.getOnsetDateTime())
            ));
        }
        
        // Set clinical status
        if (entity.getClinicalStatus() != null) {
            condition.getClinicalStatus()
                .addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical")
                .setCode(entity.getClinicalStatus());
        }
        
        // Set verification status
        if (entity.getVerificationStatus() != null) {
            condition.getVerificationStatus()
                .addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status")
                .setCode(entity.getVerificationStatus());
        }
        
        // Set severity
        if (entity.getSeveritySystem() != null && entity.getSeverityCode() != null) {
            condition.getSeverity()
                .addCoding()
                .setSystem(entity.getSeveritySystem())
                .setCode(entity.getSeverityCode())
                .setDisplay(entity.getSeverityDisplay());
        }
        
        // Set note
        if (entity.getNote() != null) {
            condition.addNote().setText(entity.getNote());
        }
        
        return condition;
    }
    
    default ConditionEntity fromFhirCondition(Condition condition) {
        if (condition == null) {
            return null;
        }
        
        ConditionEntity entity = new ConditionEntity();
        entity.setFhirId(condition.getId());
        
        // Set patient reference
        if (condition.getSubject() != null && condition.getSubject().getReference() != null) {
            entity.setPatientReference(condition.getSubject().getReference());
        }
        
        // Set code
        if (condition.getCode() != null && !condition.getCode().getCoding().isEmpty()) {
            var coding = condition.getCode().getCoding().get(0);
            entity.setCodeSystem(coding.getSystem());
            entity.setCodeValue(coding.getCode());
            entity.setCodeDisplay(coding.getDisplay());
        }
        
        // Set onset
        if (condition.getOnset() != null && condition.getOnset() instanceof org.hl7.fhir.r4.model.DateTimeType) {
            org.hl7.fhir.r4.model.DateTimeType onsetDateTime = (org.hl7.fhir.r4.model.DateTimeType) condition.getOnset();
            if (onsetDateTime.getValue() != null) {
                entity.setOnsetDateTime(new java.sql.Timestamp(onsetDateTime.getValue().getTime()).toLocalDateTime());
            }
        }
        
        // Set clinical status
        if (condition.getClinicalStatus() != null && !condition.getClinicalStatus().getCoding().isEmpty()) {
            entity.setClinicalStatus(condition.getClinicalStatus().getCoding().get(0).getCode());
        }
        
        // Set verification status
        if (condition.getVerificationStatus() != null && !condition.getVerificationStatus().getCoding().isEmpty()) {
            entity.setVerificationStatus(condition.getVerificationStatus().getCoding().get(0).getCode());
        }
        
        // Set severity
        if (condition.getSeverity() != null && !condition.getSeverity().getCoding().isEmpty()) {
            var severityCoding = condition.getSeverity().getCoding().get(0);
            entity.setSeveritySystem(severityCoding.getSystem());
            entity.setSeverityCode(severityCoding.getCode());
            entity.setSeverityDisplay(severityCoding.getDisplay());
        }
        
        // Set note
        if (!condition.getNote().isEmpty()) {
            entity.setNote(condition.getNote().get(0).getText());
        }
        
        return entity;
    }
}