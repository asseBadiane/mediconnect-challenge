package com.mediconnect.condition_api.controller;

import com.mediconnect.condition_api.dto.ConditionDto;
import com.mediconnect.condition_api.service.ConditionService;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import org.hl7.fhir.r4.model.Condition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/fhir")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Condition API", description = "FHIR Condition resource management")
public class ConditionController {
    
    private final ConditionService conditionService;
    @Autowired
    private FhirContext fhirContext; // Inject FhirContext

    @PostMapping("/Condition")
    @Operation(summary = "Create a new condition", description = "Creates a new FHIR Condition resource")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Condition created successfully",
                content = @Content(mediaType = "application/fhir+json", schema = @Schema(implementation = Condition.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid condition data or patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Condition> createCondition(
            HttpServletRequest request) throws IOException {
        
        // Read the raw request body
        String conditionJson = new String(request.getInputStream().readAllBytes());
        log.info("Creating condition from JSON: {}", conditionJson);
        
        // Parse the JSON into a Condition object using HAPI FHIR parser
        IParser parser = fhirContext.newJsonParser();
        Condition condition;
        try {
            condition = parser.parseResource(Condition.class, conditionJson);
        } catch (Exception e) {
            log.error("Failed to parse Condition JSON: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid Condition JSON format", e);
        }
        
        Condition createdCondition = conditionService.createCondition(condition);
        return new ResponseEntity<>(createdCondition, HttpStatus.CREATED);
    }

    
    @GetMapping("/Condition/{id}")
    @Operation(summary = "Get condition by ID", description = "Retrieves a FHIR Condition resource by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Condition found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Condition.class))),
        @ApiResponse(responseCode = "404", description = "Condition not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Condition> getConditionById(
            @Parameter(description = "Condition ID", required = true)
            @PathVariable String id) {
        
        log.debug("Fetching condition with ID: {}", id);
        
        Condition condition = conditionService.getConditionById(id);
        return ResponseEntity.ok(condition);
    }
    
    @GetMapping("/Condition")
    @Operation(summary = "Search conditions by patient", description = "Retrieves all conditions for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conditions found",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Bad request - Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Condition>> getConditionsByPatient(
            @Parameter(description = "Patient ID to search conditions for", required = true)
            @RequestParam(name = "patient") String patientId) {
        
        log.debug("Fetching conditions for patient: {}", patientId);
        
        List<Condition> conditions = conditionService.getConditionsByPatientId(patientId);
        return ResponseEntity.ok(conditions);
    }
    
    @PutMapping("/Condition/{id}")
    @Operation(summary = "Update condition", description = "Updates an existing FHIR Condition resource")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Condition updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Condition.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid condition data or patient not found"),
        @ApiResponse(responseCode = "404", description = "Condition not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Condition> updateCondition(
            @Parameter(description = "Condition ID", required = true)
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated FHIR Condition resource",
                required = true,
                content = @Content(schema = @Schema(implementation = Condition.class))
            )
            @RequestBody Condition condition) {
        
        log.info("Updating condition with ID: {}", id);
        
        Condition updatedCondition = conditionService.updateCondition(id, condition);
        return ResponseEntity.ok(updatedCondition);
    }
    
    @GetMapping("/Condition/all")
    @Operation(summary = "Get all conditions", description = "Retrieves all conditions (for admin purposes)")
    @ApiResponse(responseCode = "200", description = "All conditions retrieved",
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<ConditionDto>> getAllConditions() {
        log.debug("Fetching all conditions");
        
        List<ConditionDto> conditions = conditionService.getAllConditions();
        return ResponseEntity.ok(conditions);
    }
}