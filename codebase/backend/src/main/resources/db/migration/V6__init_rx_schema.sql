-- DIAGNOSES & PRESCRIPTIONS SCHEMA

CREATE TABLE diagnoses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    diagnosis_name VARCHAR(255) NOT NULL,
    icd10_code VARCHAR(20),
    type VARCHAR(50) NOT NULL DEFAULT 'Provisional', -- Provisional, Confirmed, Differential
    status VARCHAR(50) NOT NULL DEFAULT 'Active', -- Active, Resolved
    diagnosed_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE drugs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    generic_name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255),
    dosage_form VARCHAR(50) NOT NULL, -- Tablet, Syrup, Injection
    strength VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE prescriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID NOT NULL REFERENCES visits(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE prescription_lines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prescription_id UUID NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    drug_id UUID REFERENCES drugs(id),
    custom_drug_name VARCHAR(255), -- If drug not in master
    dosage VARCHAR(100) NOT NULL,
    route VARCHAR(50) NOT NULL, -- Oral, IV
    frequency VARCHAR(50) NOT NULL, -- OD, BD, TDS
    duration_days INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    instructions TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Seed some dummy drugs for MVP testing
INSERT INTO drugs (generic_name, brand_name, dosage_form, strength) VALUES
('Paracetamol', 'Crocin', 'Tablet', '650mg'),
('Amoxicillin + Clavulanate', 'Augmentin', 'Tablet', '625mg'),
('Pantoprazole', 'Pan 40', 'Tablet', '40mg'),
('Azithromycin', 'Azee', 'Tablet', '500mg'),
('Ceftriaxone', 'Monocef', 'Injection', '1g');

CREATE INDEX idx_diagnoses_patient ON diagnoses(patient_id);
CREATE INDEX idx_prescriptions_visit ON prescriptions(visit_id);
CREATE INDEX idx_prescription_lines_rx ON prescription_lines(prescription_id);
