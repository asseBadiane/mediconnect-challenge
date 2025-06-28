package com.mediconnect.condition_api.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import com.mediconnect.condition_api.entity.ConditionEntity;
import com.mediconnect.condition_api.dto.ConditionDto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface ConditionMapper {
    
    ConditionMapper INSTANCE = Mappers.getMapper(ConditionMapper.class);
    
    @Mapping(source = "fhirId", target = "id")
    ConditionDto toDto(ConditionEntity entity);
    
    @Mapping(source = "id", target = "fhirId")
    ConditionEntity toEntity(ConditionDto dto);
    
    @Mapping(target = "id", source = "fhirId")
    @Mapping(target = "subject.reference", source = "patientReference")
    @Mapping(target = "code", source = ".", qualifiedByName = "mapCoding")
    @Mapping(target = "onset", source = "onsetDateTime", qualifiedByName = "mapOnsetDateTime")
    @Mapping(target = "clinicalStatus", source = "clinicalStatus", qualifiedByName = "mapClinicalStatus")
    @Mapping(target = "verificationStatus", source = "verificationStatus", qualifiedByName = "mapVerificationStatus")
    @Mapping(target = "severity", source = ".", qualifiedByName = "mapSeverity")
    @Mapping(target = "note", source = "note", qualifiedByName = "mapNote")
    Condition toFhirCondition(ConditionEntity entity);

    @Mapping(target = "fhirId", source = "id", qualifiedByName = "extractRawId")
    @Mapping(target = "patientReference", source = "subject.reference")
    @Mapping(target = "codeSystem", source = "code.coding", qualifiedByName = "extractCodeSystem")
    @Mapping(target = "codeValue", source = "code.coding", qualifiedByName = "extractCodeValue")
    @Mapping(target = "codeDisplay", source = "code.coding", qualifiedByName = "extractCodeDisplay")
    @Mapping(target = "onsetDateTime", source = "onset", qualifiedByName = "extractOnsetDateTime")
    @Mapping(target = "clinicalStatus", source = "clinicalStatus.coding", qualifiedByName = "extractClinicalStatus")
    @Mapping(target = "verificationStatus", source = "verificationStatus.coding", qualifiedByName = "extractVerificationStatus")
    @Mapping(target = "severitySystem", source = "severity.coding", qualifiedByName = "extractSeveritySystem")
    @Mapping(target = "severityCode", source = "severity.coding", qualifiedByName = "extractSeverityCode")
    @Mapping(target = "severityDisplay", source = "severity.coding", qualifiedByName = "extractSeverityDisplay")
    @Mapping(target = "note", source = "note", qualifiedByName = "extractNote")
    ConditionEntity fromFhirCondition(Condition condition);

    // Fixed: Return CodeableConcept directly instead of using @MappingTarget
    @Named("mapCoding")
    default CodeableConcept mapCoding(ConditionEntity entity) {
        if (entity.getCodeSystem() != null && entity.getCodeValue() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                .setSystem(entity.getCodeSystem())
                .setCode(entity.getCodeValue())
                .setDisplay(entity.getCodeDisplay());
            return codeableConcept;
        }
        return null;
    }

    @Named("mapOnsetDateTime")
    default DateTimeType mapOnsetDateTime(LocalDateTime onsetDateTime) {
        return onsetDateTime != null ? new DateTimeType(
            new Date(java.sql.Timestamp.valueOf(onsetDateTime).getTime())) : null;
    }

    // Fixed: Return CodeableConcept directly instead of using @MappingTarget
    @Named("mapClinicalStatus")
    default CodeableConcept mapClinicalStatus(String clinicalStatus) {
        if (clinicalStatus != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical")
                .setCode(clinicalStatus);
            return codeableConcept;
        }
        return null;
    }

    // Fixed: Return CodeableConcept directly instead of using @MappingTarget
    @Named("mapVerificationStatus")
    default CodeableConcept mapVerificationStatus(String verificationStatus) {
        if (verificationStatus != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status")
                .setCode(verificationStatus);
            return codeableConcept;
        }
        return null;
    }

    // Fixed: Return CodeableConcept directly instead of using @MappingTarget
    @Named("mapSeverity")
    default CodeableConcept mapSeverity(ConditionEntity entity) {
        if (entity.getSeveritySystem() != null && entity.getSeverityCode() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding()
                .setSystem(entity.getSeveritySystem())
                .setCode(entity.getSeverityCode())
                .setDisplay(entity.getSeverityDisplay());
            return codeableConcept;
        }
        return null;
    }

    @Named("mapNote")
    default List<Annotation> mapNote(String note) {
        if (note != null) {
            Annotation annotation = new Annotation();
            annotation.setText(note);
            return Collections.singletonList(annotation);
        }
        return Collections.emptyList();
    }

    @Named("extractRawId")
    default String extractRawId(String fullId) {
        if (fullId == null || fullId.isEmpty()) {
            return java.util.UUID.randomUUID().toString();
        }
        return fullId.replaceFirst("^Condition/", "");
    }

    @Named("extractCodeSystem")
    default String extractCodeSystem(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getSystem() : null;
    }

    @Named("extractCodeValue")
    default String extractCodeValue(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getCode() : null;
    }

    @Named("extractCodeDisplay")
    default String extractCodeDisplay(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getDisplay() : null;
    }

    @Named("extractOnsetDateTime")
    default LocalDateTime extractOnsetDateTime(org.hl7.fhir.r4.model.Type onset) {
        if (onset instanceof DateTimeType) {
            DateTimeType dateTimeType = (DateTimeType) onset;
            return dateTimeType.getValue() != null ? dateTimeType.getValue().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        }
        return null;
    }

    @Named("extractClinicalStatus")
    default String extractClinicalStatus(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getCode() : null;
    }

    @Named("extractVerificationStatus")
    default String extractVerificationStatus(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getCode() : null;
    }

    @Named("extractSeveritySystem")
    default String extractSeveritySystem(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getSystem() : null;
    }

    @Named("extractSeverityCode")
    default String extractSeverityCode(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getCode() : null;
    }

    @Named("extractSeverityDisplay")
    default String extractSeverityDisplay(List<Coding> codings) {
        return codings != null && !codings.isEmpty() ? codings.get(0).getDisplay() : null;
    }

    @Named("extractNote")
    default String extractNote(List<Annotation> notes) {
        return notes != null && !notes.isEmpty() ? notes.get(0).getText() : null;
    }


    // Helper methods
    default String extractPatientId(String patientReference) {
        if (patientReference == null) return null;
        return patientReference.startsWith("Patient/") ? 
            patientReference.substring("Patient/".length()) : patientReference;
    }
    
    default String extractSnomedCode(Condition condition) {
        if (condition.getCode() != null && condition.getCode().getCoding() != null 
            && !condition.getCode().getCoding().isEmpty()) {
            return condition.getCode().getCoding().get(0).getCode();
        }
        return null;
    }
    
    default String extractSnomedDisplay(Condition condition) {
        if (condition.getCode() != null && condition.getCode().getCoding() != null 
            && !condition.getCode().getCoding().isEmpty()) {
            return condition.getCode().getCoding().get(0).getDisplay();
        }
        return null;
    }
    
    default LocalDateTime extractOnsetDateTime(Condition condition) {
        if (condition.getOnsetDateTimeType() != null && condition.getOnsetDateTimeType().getValue() != null) {
            return condition.getOnsetDateTimeType().getValue().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }
    
    default Reference createPatientReference(String patientId) {
        Reference reference = new Reference();
        reference.setReference("Patient/" + patientId);
        return reference;
    }
    
    default CodeableConcept createCodeableConcept(String code, String display) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem("http://snomed.info/sct");
        coding.setCode(code);
        coding.setDisplay(display);
        codeableConcept.addCoding(coding);
        return codeableConcept;
    }
}