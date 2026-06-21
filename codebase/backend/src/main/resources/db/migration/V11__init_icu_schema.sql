-- ICU MANAGEMENT MODULE SCHEMA

CREATE TABLE icu_charting (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    recorded_by UUID NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Vitals
    heart_rate INTEGER,
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    mean_arterial_pressure INTEGER, -- MAP
    temperature DECIMAL(5,2),
    spo2 INTEGER,
    -- Ventilator
    ventilator_mode VARCHAR(50), -- AC, SIMV, CPAP, PSV, Room Air
    fio2 DECIMAL(5,2), -- Percentage
    peep DECIMAL(5,2), -- cmH2O
    -- Fluid Balance
    intake_volume INTEGER, -- ml
    output_volume INTEGER, -- ml
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE icu_scores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    recorded_by UUID NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    score_type VARCHAR(50) NOT NULL, -- GCS, APACHE_II, SOFA
    -- Specific to GCS for MVP
    gcs_eye INTEGER, -- 1-4
    gcs_verbal INTEGER, -- 1-5
    gcs_motor INTEGER, -- 1-6
    total_score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_icu_charting_patient ON icu_charting(patient_id);
CREATE INDEX idx_icu_scores_patient ON icu_scores(patient_id);
