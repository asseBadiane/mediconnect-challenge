package com.mediconnect.patient_api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {
    
    private String fhirId;
    private String identifierSystem;
    private String identifierValue;
    private String givenName;
    private String familyName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    private Gender gender;
    private String phone;
    private String email;
    private String addressLine1;
    private String addressCity;
    private String addressPostalCode;
    private String addressCountry;
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }
}