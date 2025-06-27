package com.mediconnect.patient_api.mapper;


import com.mediconnect.patient_api.dto.PatientRequestDto;
import com.mediconnect.patient_api.dto.PatientResponseDto;
import com.mediconnect.patient_api.model.PatientEntity;
import org.hl7.fhir.r4.model.Patient;

import org.mapstruct.*;

import java.util.List;
import java.util.UUID;


@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fhirId", expression = "java(generateFhirId())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    PatientEntity toEntity(PatientRequestDto dto);
    
    PatientResponseDto toResponseDto(PatientEntity entity);
    
    List<PatientResponseDto> toResponseDtoList(List<PatientEntity> entities);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fhirId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(PatientRequestDto dto, @MappingTarget PatientEntity entity);
    
   
}