package com.mediconnect.condition_api.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mediconnect.condition_api.dto.PatientResponseDto;

import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${patient-api.base-url}")
    private String patientApiBaseUrl;
    
    public Mono<PatientResponseDto> getPatientById(String patientId) {
        log.debug("Fetching patient with ID: {}", patientId);
        
        return webClientBuilder.build()
                .get()
                .uri(patientApiBaseUrl + "/Patient/{id}", patientId)
                .retrieve()
                .bodyToMono(PatientResponseDto.class)
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(patient -> log.debug("Successfully fetched patient: {}", patientId))
                .doOnError(error -> log.error("Error fetching patient {}: {}", patientId, error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Patient {} not found or error occurred, returning empty", patientId);
                    return Mono.empty();
                });
    }
    
    public Mono<Boolean> patientExists(String patientId) {
        log.debug("Checking if patient exists with ID: {}", patientId);
        
        return getPatientById(patientId)
                .map(patient -> patient != null && patient.getFhirId() != null && patient.getFhirId().equals(patientId))
                .defaultIfEmpty(false)
                .doOnNext(exists -> log.debug("Patient {} exists: {}", patientId, exists));
    }
}