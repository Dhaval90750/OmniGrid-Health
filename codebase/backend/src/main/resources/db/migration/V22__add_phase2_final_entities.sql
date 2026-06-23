-- Add status to prescriptions
ALTER TABLE prescriptions ADD COLUMN status VARCHAR(50) DEFAULT 'Draft';

-- Create shift_handovers table
CREATE TABLE shift_handovers (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0,
    patient_id UUID NOT NULL REFERENCES patients(id),
    handing_over_nurse UUID NOT NULL REFERENCES users(id),
    receiving_nurse UUID REFERENCES users(id),
    shift_date_time TIMESTAMP NOT NULL,
    sbar_situation TEXT,
    sbar_background TEXT,
    sbar_assessment TEXT,
    sbar_recommendation TEXT
);

-- Create who_checklists table
CREATE TABLE who_checklists (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0,
    booking_id UUID NOT NULL REFERENCES ot_bookings(id),
    sign_in_completed BOOLEAN DEFAULT FALSE,
    time_out_completed BOOLEAN DEFAULT FALSE,
    sign_out_completed BOOLEAN DEFAULT FALSE,
    notes TEXT
);

-- Create implant_logs table
CREATE TABLE implant_logs (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0,
    booking_id UUID NOT NULL REFERENCES ot_bookings(id),
    implant_name VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255),
    lot_number VARCHAR(255) NOT NULL,
    expiry_date DATE,
    used_quantity INTEGER DEFAULT 1
);
