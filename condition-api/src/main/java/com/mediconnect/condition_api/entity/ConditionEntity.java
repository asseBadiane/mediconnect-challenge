package com.mediconnect.condition_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fhir_id", unique = true, nullable = false)
    private String fhirId;
    
    @Column(name = "patient_reference", nullable = false)
    private String patientReference;
    
    @Column(name = "code_system")
    private String codeSystem;
    
    @Column(name = "code_value")
    private String codeValue;
    
    @Column(name = "code_display")
    private String codeDisplay;
    
    @Column(name = "onset_date_time")
    private LocalDateTime onsetDateTime;
    
    @Column(name = "clinical_status")
    private String clinicalStatus;
    
    @Column(name = "verification_status")
    private String verificationStatus;
    
    @Column(name = "severity_system")
    private String severitySystem;
    
    @Column(name = "severity_code")
    private String severityCode;
    
    @Column(name = "severity_display")
    private String severityDisplay;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}