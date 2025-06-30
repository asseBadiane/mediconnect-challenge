-- Create conditions table
CREATE TABLE conditions (
    id BIGSERIAL PRIMARY KEY,
    fhir_id VARCHAR(255) UNIQUE NOT NULL,
    patient_reference VARCHAR(255) NOT NULL,
    code_system VARCHAR(255),
    code_value VARCHAR(255),
    code_display VARCHAR(500),
    onset_date_time TIMESTAMP,
    clinical_status VARCHAR(50),
    verification_status VARCHAR(50),
    severity_system VARCHAR(255),
    severity_code VARCHAR(100),
    severity_display VARCHAR(255),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_conditions_fhir_id ON conditions(fhir_id);
CREATE INDEX idx_conditions_patient_reference ON conditions(patient_reference);
CREATE INDEX idx_conditions_onset_date_time ON conditions(onset_date_time);
CREATE INDEX idx_conditions_clinical_status ON conditions(clinical_status);

-- Add comments for documentation
COMMENT ON TABLE conditions IS 'FHIR Condition resources storage';
COMMENT ON COLUMN conditions.fhir_id IS 'FHIR resource identifier';
COMMENT ON COLUMN conditions.patient_reference IS 'Reference to Patient resource';
COMMENT ON COLUMN conditions.code_system IS 'Coding system (e.g., SNOMED CT)';
COMMENT ON COLUMN conditions.code_value IS 'Condition code';
COMMENT ON COLUMN conditions.code_display IS 'Human-readable condition name';
COMMENT ON COLUMN conditions.onset_date_time IS 'When condition was first observed';
COMMENT ON COLUMN conditions.clinical_status IS 'active | recurrence | relapse | inactive | remission | resolved';
COMMENT ON COLUMN conditions.verification_status IS 'unconfirmed | provisional | differential | confirmed | refuted | entered-in-error';