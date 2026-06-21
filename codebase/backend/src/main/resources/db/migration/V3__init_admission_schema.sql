-- ADMISSIONS SCHEMA

CREATE TABLE admissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    admitting_doctor_id UUID NOT NULL REFERENCES users(id),
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_id UUID NOT NULL REFERENCES beds(id),
    status VARCHAR(50) NOT NULL DEFAULT 'ADMITTED', -- ADMITTED, DISCHARGED, TRANSFERRED
    admission_reason TEXT NOT NULL,
    admission_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    discharge_date TIMESTAMP,
    discharge_summary TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_admissions_patient ON admissions(patient_id);
CREATE INDEX idx_admissions_doctor ON admissions(admitting_doctor_id);
CREATE INDEX idx_admissions_ward ON admissions(ward_id);
CREATE INDEX idx_admissions_status ON admissions(status);
