package com.mediconnect.condition_api.repository;

import com.mediconnect.condition_api.entity.ConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConditionRepository extends JpaRepository<ConditionEntity, Long> {
    
    Optional<ConditionEntity> findByFhirId(String fhirId);
    
    @Query("SELECT c FROM ConditionEntity c WHERE c.patientReference = :patientReference ORDER BY c.onsetDateTime DESC")
    List<ConditionEntity> findByPatientReference(@Param("patientReference") String patientReference);
    
    @Query("SELECT c FROM ConditionEntity c WHERE c.patientReference LIKE %:patientId% ORDER BY c.onsetDateTime DESC")
    List<ConditionEntity> findByPatientId(@Param("patientId") String patientId);
    
    boolean existsByFhirId(String fhirId);
}