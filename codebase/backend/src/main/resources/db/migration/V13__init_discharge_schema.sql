-- DISCHARGE MODULE SCHEMA

CREATE TABLE discharges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID NOT NULL UNIQUE REFERENCES visits(id) ON DELETE CASCADE,
    discharged_by UUID NOT NULL REFERENCES users(id),
    
    discharge_type VARCHAR(50) NOT NULL, -- Normal, DAMA, LAMA, Death
    final_summary TEXT,
    follow_up_date TIMESTAMP,
    discharge_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_discharges_patient ON discharges(patient_id);
