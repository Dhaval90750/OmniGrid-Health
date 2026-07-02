CREATE TABLE break_the_glass_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    patient_id UUID,
    justification_type VARCHAR(100) NOT NULL,
    justification_text TEXT NOT NULL,
    access_granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    access_expires_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(45)
);
