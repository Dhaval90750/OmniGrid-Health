-- PHARMACY MODULE SCHEMA

CREATE TABLE pharmacy_stock (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    drug_id UUID NOT NULL REFERENCES drugs(id),
    batch_number VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2),
    mrp DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(drug_id, batch_number)
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stock_id UUID NOT NULL REFERENCES pharmacy_stock(id),
    movement_type VARCHAR(50) NOT NULL, -- Receipt, Issue, Dispense, Adjustment
    quantity_change INTEGER NOT NULL, -- Positive for receipt, negative for dispense
    reference_number VARCHAR(100), -- Invoice number, Rx number etc
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE dispensing_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prescription_id UUID NOT NULL REFERENCES prescriptions(id),
    patient_id UUID NOT NULL REFERENCES patients(id),
    pharmacist_id UUID NOT NULL REFERENCES users(id),
    dispensing_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'Dispensed', -- Dispensed, Partially_Dispensed, Cancelled
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Note: In a full system, dispensing_records would have line items mapping to specific stock_id
-- For MVP, we simplify it.

-- Seed initial stock for testing (assuming Paracetamol drug_id is resolved by application, but here we'll just leave it empty and let the backend/frontend handle it dynamically or seed a dummy record)
-- Actually, we can't seed drug_id easily without knowing the UUID. 
-- So we will not seed pharmacy_stock in SQL, we'll let the user/service create it.

CREATE INDEX idx_pharmacy_stock_drug ON pharmacy_stock(drug_id);
CREATE INDEX idx_stock_movements_stock ON stock_movements(stock_id);
CREATE INDEX idx_dispensing_rx ON dispensing_records(prescription_id);
