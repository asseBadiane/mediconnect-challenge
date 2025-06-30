package com.mediconnect.condition_api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionDto {
    
    private String id;
    private String patientReference;
    private String codeSystem;
    private String codeValue;
    private String codeDisplay;
    private LocalDateTime onsetDateTime;
    private String clinicalStatus;
    private String verificationStatus;
    private String severitySystem;
    private String severityCode;
    private String severityDisplay;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}