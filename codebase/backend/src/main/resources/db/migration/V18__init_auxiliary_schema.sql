-- AUXILIARY CLINICAL MODULES (BLOOD BANK & INFECTION CONTROL)

CREATE TABLE blood_inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    blood_group VARCHAR(10) NOT NULL, -- A+, A-, B+, B-, AB+, AB-, O+, O-
    component_type VARCHAR(50) NOT NULL, -- Whole Blood, PRBC, FFP, Platelets
    unit_number VARCHAR(50) NOT NULL UNIQUE,
    collection_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Available', -- Available, Cross-matched, Issued, Discarded
    donor_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE infection_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    ward_id VARCHAR(100) NOT NULL,
    infection_type VARCHAR(100) NOT NULL, -- CAUTI, CLABSI, SSI, VAP
    identified_date DATE NOT NULL,
    organism_identified VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'Active', -- Active, Resolved
    reported_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
