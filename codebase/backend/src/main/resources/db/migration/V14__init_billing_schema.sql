-- BILLING AND INSURANCE MODULE SCHEMA

CREATE TABLE insurance_policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    tpa_name VARCHAR(255),
    policy_number VARCHAR(100) NOT NULL UNIQUE,
    coverage_limit DECIMAL(12,2),
    valid_until DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE insurance_claims (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_id UUID NOT NULL REFERENCES visits(id) ON DELETE CASCADE,
    policy_id UUID NOT NULL REFERENCES insurance_policies(id),
    status VARCHAR(50) NOT NULL DEFAULT 'PreAuth_Pending', -- PreAuth_Pending, PreAuth_Approved, Claim_Submitted, Settled, Rejected
    preauth_amount DECIMAL(12,2),
    claimed_amount DECIMAL(12,2),
    approved_amount DECIMAL(12,2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE ipd_bills (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_id UUID NOT NULL UNIQUE REFERENCES visits(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL REFERENCES patients(id),
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    insurance_coverage DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    patient_payable DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    amount_paid DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) NOT NULL DEFAULT 'Draft', -- Draft, Finalized, Paid, Partially_Paid
    generated_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE bill_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bill_id UUID NOT NULL REFERENCES ipd_bills(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL, -- Room, Pharmacy, OT, Consultation, Lab, Radiology
    description VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_insurance_policies_patient ON insurance_policies(patient_id);
CREATE INDEX idx_insurance_claims_visit ON insurance_claims(visit_id);
CREATE INDEX idx_ipd_bills_visit ON ipd_bills(visit_id);
