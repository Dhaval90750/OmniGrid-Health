-- OT MANAGEMENT MODULE SCHEMA

CREATE TABLE ot_bookings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    surgeon_id UUID NOT NULL REFERENCES users(id),
    anesthesiologist_id UUID REFERENCES users(id),
    ot_room_number VARCHAR(50) NOT NULL,
    procedure_name VARCHAR(255) NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    estimated_duration INTEGER, -- in minutes
    status VARCHAR(50) NOT NULL DEFAULT 'Scheduled', -- Scheduled, In_Progress, Completed, Cancelled
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE ot_surgery_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID NOT NULL UNIQUE REFERENCES ot_bookings(id) ON DELETE CASCADE,
    
    -- WHO Checklist
    sign_in_completed BOOLEAN NOT NULL DEFAULT FALSE,
    time_out_completed BOOLEAN NOT NULL DEFAULT FALSE,
    sign_out_completed BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Operative Note
    pre_op_diagnosis VARCHAR(255),
    post_op_diagnosis VARCHAR(255),
    procedure_performed VARCHAR(255),
    findings TEXT,
    estimated_blood_loss INTEGER, -- ml
    specimens_sent BOOLEAN NOT NULL DEFAULT FALSE,
    complications TEXT,
    
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_ot_bookings_patient ON ot_bookings(patient_id);
CREATE INDEX idx_ot_bookings_surgeon ON ot_bookings(surgeon_id);
