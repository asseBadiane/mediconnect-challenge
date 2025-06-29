package com.mediconnect.patient_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSearchDto {
    
    private String name;
    private String identifier;
    private String givenName;
    private String familyName;
    private String email;
    private String phone;
    private Boolean active;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 20;
    private String sort = "familyName,asc";
    public String getSearchTerm() {
        return name;
    }
}