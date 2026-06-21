-- LABORATORY INFORMATION SYSTEM SCHEMA

CREATE TABLE lab_tests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    test_code VARCHAR(50) UNIQUE NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL, -- Hematology, Biochemistry, etc.
    reference_range_low DECIMAL(10,2),
    reference_range_high DECIMAL(10,2),
    unit VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE lab_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    doctor_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL DEFAULT 'Ordered', -- Ordered, Sample_Collected, Processing, Completed
    priority VARCHAR(50) NOT NULL DEFAULT 'Routine', -- Routine, Urgent, Stat
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE lab_samples (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES lab_orders(id) ON DELETE CASCADE,
    test_id UUID NOT NULL REFERENCES lab_tests(id),
    barcode VARCHAR(100) UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'Pending_Collection', -- Pending_Collection, Collected, Received, Rejected
    collected_at TIMESTAMP,
    collected_by UUID REFERENCES users(id),
    received_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE lab_results (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sample_id UUID NOT NULL REFERENCES lab_samples(id) ON DELETE CASCADE,
    result_value DECIMAL(10,2),
    result_text VARCHAR(255),
    is_abnormal BOOLEAN NOT NULL DEFAULT FALSE,
    comments TEXT,
    authorized_by UUID REFERENCES users(id),
    authorized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Seed basic lab tests
INSERT INTO lab_tests (test_code, test_name, category, reference_range_low, reference_range_high, unit) VALUES
('HEM-001', 'Hemoglobin (Hb)', 'Hematology', 13.0, 17.0, 'g/dL'),
('HEM-002', 'Total WBC Count', 'Hematology', 4000, 11000, 'cells/cumm'),
('BIO-001', 'Fasting Blood Sugar (FBS)', 'Biochemistry', 70, 100, 'mg/dL'),
('BIO-002', 'Serum Creatinine', 'Biochemistry', 0.6, 1.2, 'mg/dL'),
('LIP-001', 'Total Cholesterol', 'Biochemistry', NULL, 200, 'mg/dL');

CREATE INDEX idx_lab_orders_patient ON lab_orders(patient_id);
CREATE INDEX idx_lab_samples_order ON lab_samples(order_id);
CREATE INDEX idx_lab_results_sample ON lab_results(sample_id);
