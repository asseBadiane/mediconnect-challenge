package com.mediconnect.patient_api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDto {
    
    @NotBlank(message = "Given name is required")
    @Size(max = 255, message = "Given name must not exceed 255 characters")
    private String givenName;
    
    @NotBlank(message = "Family name is required")
    @Size(max = 255, message = "Family name must not exceed 255 characters")
    private String familyName;
    
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String addressLine1;
    private String addressCity;
    private String addressPostalCode;
    private String addressCountry;
    
    @Size(max = 255, message = "Identifier system must not exceed 255 characters")
    private String identifierSystem;
    
    @Size(max = 255, message = "Identifier value must not exceed 255 characters")
    private String identifierValue;
    
    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }
}