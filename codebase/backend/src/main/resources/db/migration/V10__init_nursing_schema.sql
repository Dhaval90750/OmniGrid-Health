-- NURSING MODULE SCHEMA

CREATE TABLE patient_vitals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    recorded_by UUID NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    temperature DECIMAL(5,2), -- Fahrenheit or Celsius
    heart_rate INTEGER, -- bpm
    blood_pressure_systolic INTEGER, -- mmHg
    blood_pressure_diastolic INTEGER, -- mmHg
    respiratory_rate INTEGER, -- breaths/min
    spo2 INTEGER, -- percentage
    pain_score INTEGER, -- 0-10
    news_score INTEGER, -- National Early Warning Score
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE med_administrations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prescription_id UUID NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    administered_by UUID NOT NULL REFERENCES users(id),
    administered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_time TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'Given', -- Given, Missed, Refused
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_patient_vitals_patient ON patient_vitals(patient_id);
CREATE INDEX idx_med_admin_rx ON med_administrations(prescription_id);
CREATE INDEX idx_med_admin_patient ON med_administrations(patient_id);
