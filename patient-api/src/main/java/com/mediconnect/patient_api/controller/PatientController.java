package com.mediconnect.patient_api.controller;

import com.mediconnect.patient_api.dto.PatientRequestDto;
import com.mediconnect.patient_api.dto.PatientResponseDto;
import com.mediconnect.patient_api.dto.PatientSearchDto;
import com.mediconnect.patient_api.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/fhir")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Patient API", description = "FHIR Patient Resource Management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/Patients")
    public ResponseEntity<Page<PatientResponseDto>> getAllPatients() {
        log.info("Retrieving all patients");
        
        Page<PatientResponseDto> response = patientService.getAllPatients();
        return ResponseEntity.ok(response);
    }
    

    @Operation(summary = "Create a new patient", description = "Creates a new patient resource")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid patient data"),
        @ApiResponse(responseCode = "409", description = "Patient already exists")
    })
    @PostMapping("/Patient")
    public ResponseEntity<PatientResponseDto> createPatient(
            @Valid @RequestBody PatientRequestDto requestDto) {
        log.info("Creating patient: {} {}", requestDto.getGivenName(), requestDto.getFamilyName());
        
        PatientResponseDto response = patientService.createPatient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get patient by FHIR ID", description = "Retrieves a patient by their FHIR ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/Patient/{fhirId}")
    public ResponseEntity<PatientResponseDto> getPatientByFhirId(
            @Parameter(description = "FHIR ID of the patient")
            @PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9\\-\\.]{1,64}$") String fhirId) {
        log.info("Retrieving patient by FHIR ID: {}", fhirId);
        
        PatientResponseDto response = patientService.getPatientByFhirId(fhirId);
        return ResponseEntity.ok(response);
    }

    


    @Operation(summary = "Search patients", description = "Search patients with various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/Patient")
    public ResponseEntity<Page<PatientResponseDto>> searchPatients(
            @Parameter(description = "Patient full name") 
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Patient given name") 
            @RequestParam(required = false) String given,
            
            @Parameter(description = "Patient family name") 
            @RequestParam(required = false) String family,
            
            @Parameter(description = "Patient identifier") 
            @RequestParam(required = false) String identifier,
            
            @Parameter(description = "Patient email") 
            @RequestParam(required = false) String email,
            
            @Parameter(description = "Patient phone") 
            @RequestParam(required = false) String phone,
            
            @Parameter(description = "Patient active status") 
            @RequestParam(required = false) Boolean active,
            
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort criteria (field,direction)") 
            @RequestParam(required = false) String sort) {
        
        log.info("Searching patients with criteria - name: {}, given: {}, family: {}, identifier: {}", 
                name, given, family, identifier);

        PatientSearchDto searchDto = PatientSearchDto.builder()
                .name(name)
                .givenName(given)
                .familyName(family)
                .identifier(identifier)
                .email(email)
                .phone(phone)
                .active(active)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        Page<PatientResponseDto> response = patientService.searchPatients(searchDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update patient", description = "Updates an existing patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "400", description = "Invalid patient data")
    })
    @PutMapping("/Patient/{fhirId}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @Parameter(description = "FHIR ID of the patient")
            @PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9\\-\\.]{1,64}$") String fhirId,
            @Valid @RequestBody PatientRequestDto requestDto) {
        log.info("Updating patient with FHIR ID: {}", fhirId);
        
        PatientResponseDto response = patientService.updatePatient(fhirId, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete patient", description = "Soft deletes a patient (sets active to false)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/Patient/{fhirId}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "FHIR ID of the patient")
            @PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9\\-\\.]{1,64}$") String fhirId) {
        log.info("Deleting patient with FHIR ID: {}", fhirId);
        
        patientService.deletePatient(fhirId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Health check endpoint")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Patient API is running");
    }
}