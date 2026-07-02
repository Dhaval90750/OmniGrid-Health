CREATE TABLE drug_interactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    primary_drug_generic VARCHAR(255) NOT NULL,
    secondary_drug_generic VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_drug_interaction_primary ON drug_interactions(primary_drug_generic);
CREATE INDEX idx_drug_interaction_secondary ON drug_interactions(secondary_drug_generic);

INSERT INTO drug_interactions (primary_drug_generic, secondary_drug_generic, severity, description) VALUES
('paracetamol', 'ibuprofen', 'CAUTION', 'Concurrent use of Paracetamol and Ibuprofen may increase risk of hepatotoxicity if overdosed.'),
('warfarin', 'aspirin', 'HIGH RISK', 'Concurrent use of Warfarin and Aspirin increases bleeding risk significantly.');
