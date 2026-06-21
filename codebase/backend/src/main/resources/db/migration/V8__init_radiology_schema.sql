-- RADIOLOGY MODULE SCHEMA

CREATE TABLE radiology_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_name VARCHAR(255) NOT NULL,
    modality VARCHAR(50) NOT NULL, -- X-Ray, CT, MRI, USG
    body_part VARCHAR(100) NOT NULL,
    content_template TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE radiology_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    doctor_id UUID NOT NULL REFERENCES users(id),
    modality VARCHAR(50) NOT NULL,
    study_description VARCHAR(255) NOT NULL,
    clinical_indication TEXT,
    urgency VARCHAR(50) NOT NULL DEFAULT 'Routine', -- Routine, Urgent, Stat
    status VARCHAR(50) NOT NULL DEFAULT 'Scheduled', -- Scheduled, Completed, Reported
    scheduled_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE radiology_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL UNIQUE REFERENCES radiology_orders(id) ON DELETE CASCADE,
    radiologist_id UUID NOT NULL REFERENCES users(id),
    findings TEXT NOT NULL,
    impression TEXT NOT NULL,
    is_critical BOOLEAN NOT NULL DEFAULT FALSE,
    dicom_study_uid VARCHAR(255), -- Placeholder for future PACS integration
    status VARCHAR(50) NOT NULL DEFAULT 'Draft', -- Draft, Final
    finalized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Seed some templates
INSERT INTO radiology_templates (template_name, modality, body_part, content_template) VALUES
('Chest X-Ray Normal', 'X-Ray', 'Chest', 'Lungs are clear. Heart size is normal. No pleural effusion.'),
('MRI Brain Normal', 'MRI', 'Brain', 'Normal ventricles and sulci. No acute infarct or hemorrhage.'),
('USG Whole Abdomen Normal', 'USG', 'Abdomen', 'Liver, gallbladder, pancreas, spleen, and bilateral kidneys are normal in size and echotexture.');

CREATE INDEX idx_rad_orders_patient ON radiology_orders(patient_id);
CREATE INDEX idx_rad_reports_order ON radiology_reports(order_id);
