-- CLINICAL SCHEMA

CREATE TABLE visits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id UUID REFERENCES users(id),
    department_id UUID REFERENCES departments(id),
    visit_type VARCHAR(50) NOT NULL DEFAULT 'OPD', -- OPD, IPD, ER
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, WAITING, IN_CONSULTATION, COMPLETED, CANCELLED
    visit_date TIMESTAMP NOT NULL,
    chief_complaint VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE clinical_notes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_id UUID NOT NULL REFERENCES visits(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES users(id),
    note_type VARCHAR(50) NOT NULL DEFAULT 'OPD_CONSULT', -- OPD_CONSULT, PROGRESS_NOTE, DISCHARGE_SUMMARY
    history_of_present_illness TEXT,
    past_medical_history TEXT,
    physical_examination TEXT,
    treatment_plan TEXT,
    is_finalized BOOLEAN NOT NULL DEFAULT FALSE,
    finalized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_visits_patient ON visits(patient_id);
CREATE INDEX idx_visits_doctor ON visits(doctor_id);
CREATE INDEX idx_visits_date ON visits(visit_date);
CREATE INDEX idx_clinical_notes_visit ON clinical_notes(visit_id);
