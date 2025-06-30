package com.mediconnect.patient_api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fhir_id", unique = true, nullable = false)
    private String fhirId;
    
    @Column(name = "identifier_system")
    private String identifierSystem;
    
    @Column(name = "identifier_value", unique = true)
    private String identifierValue;
    
    @Column(name = "given_name", nullable = false)
    private String givenName;
    
    @Column(name = "family_name", nullable = false)
    private String familyName;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_city")
    private String addressCity;
    
    @Column(name = "address_postal_code")
    private String addressPostalCode;
    
    @Column(name = "address_country")
    private String addressCountry;
    
    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }
}