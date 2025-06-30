package com.mediconnect.patient_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mediconnect.patient_api.model.PatientEntity;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    
    Optional<PatientEntity> findByFhirId(String fhirId);
    
    Optional<PatientEntity> findByIdentifierValue(String identifierValue);
              
    boolean existsByIdentifierValue(String identifierValue);
    
    boolean existsByFhirId(String fhirId);
    
    @Query("SELECT COUNT(p) FROM PatientEntity p WHERE p.active = true")
    long countActivePatients();
}