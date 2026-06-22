-- --- MIGRATE: V1__init_core_schema.sql --- --
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- AUTHENTICATION & AUTHORIZATION SCHEMA
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- MASTER DATA SCHEMA
CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    parent_id UUID REFERENCES departments(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE wards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL, -- e.g., ICU, General, Private
    department_id UUID REFERENCES departments(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE beds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_number VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OCCUPIED, CLEANING, MAINTENANCE
    category VARCHAR(50) NOT NULL, -- e.g., Ventilator, Oxygen, Standard
    daily_rate DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(ward_id, bed_number)
);

-- PATIENT SCHEMA
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    uhid VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    blood_group VARCHAR(10),
    marital_status VARCHAR(50),
    nationality VARCHAR(50),
    primary_language VARCHAR(50),
    national_id VARCHAR(50) UNIQUE, -- Aadhaar / SSN
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(150),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    zip_code VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    emergency_contact_relation VARCHAR(50),
    emergency_contact_phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE patient_allergies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    allergen VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL, -- Mild, Moderate, Severe
    reaction VARCHAR(255),
    identified_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, RESOLVED
    verified_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- INDEXES
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_patients_uhid ON patients(uhid);
CREATE INDEX idx_patients_mobile ON patients(mobile_number);
CREATE INDEX idx_patients_name ON patients(first_name, last_name);
CREATE INDEX idx_beds_status ON beds(status);



-- --- MIGRATE: V2__init_clinical_schema.sql --- --
-- CLINICAL SCHEMA

CREATE TABLE visits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id UUID REFERENCES users(id),
    department_id UUID REFERENCES departments(id),
    visit_type VARCHAR(50) NOT NULL DEFAULT 'OPD', -- OPD, IPD, ER
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, WAITING, IN_CONSULTATION, COMPLETED, CANCELLED
    visit_date TIMESTAMP NOT NULL,
    chief_complaint VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE clinical_notes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_id UUID NOT NULL REFERENCES visits(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES users(id),
    note_type VARCHAR(50) NOT NULL DEFAULT 'OPD_CONSULT', -- OPD_CONSULT, PROGRESS_NOTE, DISCHARGE_SUMMARY
    history_of_present_illness TEXT,
    past_medical_history TEXT,
    physical_examination TEXT,
    treatment_plan TEXT,
    is_finalized BOOLEAN NOT NULL DEFAULT FALSE,
    finalized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_visits_patient ON visits(patient_id);
CREATE INDEX idx_visits_doctor ON visits(doctor_id);
CREATE INDEX idx_visits_date ON visits(visit_date);
CREATE INDEX idx_clinical_notes_visit ON clinical_notes(visit_id);



-- --- MIGRATE: V3__init_admission_schema.sql --- --
-- ADMISSIONS SCHEMA

CREATE TABLE admissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    admitting_doctor_id UUID NOT NULL REFERENCES users(id),
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_id UUID NOT NULL REFERENCES beds(id),
    status VARCHAR(50) NOT NULL DEFAULT 'ADMITTED', -- ADMITTED, DISCHARGED, TRANSFERRED
    admission_reason TEXT NOT NULL,
    admission_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    discharge_date TIMESTAMP,
    discharge_summary TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_admissions_patient ON admissions(patient_id);
CREATE INDEX idx_admissions_doctor ON admissions(admitting_doctor_id);
CREATE INDEX idx_admissions_ward ON admissions(ward_id);
CREATE INDEX idx_admissions_status ON admissions(status);



-- --- MIGRATE: V4__init_billing_schema.sql --- --
-- BILLING SCHEMA

CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id UUID NOT NULL REFERENCES patients(id),
    visit_id UUID REFERENCES visits(id),
    admission_id UUID REFERENCES admissions(id),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, PARTIAL, PAID, CANCELLED
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    net_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    amount_paid DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE invoice_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    department_id UUID REFERENCES departments(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    receipt_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- CASH, CARD, UPI, INSURANCE
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_reference VARCHAR(100),
    collected_by UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoices_patient ON invoices(patient_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoice_items_invoice ON invoice_items(invoice_id);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);



-- --- MIGRATE: V5__add_base_columns.sql --- --
-- ADD MISSING BASE ENTITY COLUMNS TO BILLING TABLES

ALTER TABLE invoice_items 
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE payments 
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN created_by VARCHAR(100),
ADD COLUMN updated_by VARCHAR(100);



-- --- MIGRATE: V6__init_rx_schema.sql --- --
-- DIAGNOSES & PRESCRIPTIONS SCHEMA

CREATE TABLE diagnoses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    diagnosis_name VARCHAR(255) NOT NULL,
    icd10_code VARCHAR(20),
    type VARCHAR(50) NOT NULL DEFAULT 'Provisional', -- Provisional, Confirmed, Differential
    status VARCHAR(50) NOT NULL DEFAULT 'Active', -- Active, Resolved
    diagnosed_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE drugs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    generic_name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255),
    dosage_form VARCHAR(50) NOT NULL, -- Tablet, Syrup, Injection
    strength VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE prescriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID NOT NULL REFERENCES visits(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE prescription_lines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prescription_id UUID NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    drug_id UUID REFERENCES drugs(id),
    custom_drug_name VARCHAR(255), -- If drug not in master
    dosage VARCHAR(100) NOT NULL,
    route VARCHAR(50) NOT NULL, -- Oral, IV
    frequency VARCHAR(50) NOT NULL, -- OD, BD, TDS
    duration_days INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    instructions TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Seed some dummy drugs for MVP testing
INSERT INTO drugs (generic_name, brand_name, dosage_form, strength) VALUES
('Paracetamol', 'Crocin', 'Tablet', '650mg'),
('Amoxicillin + Clavulanate', 'Augmentin', 'Tablet', '625mg'),
('Pantoprazole', 'Pan 40', 'Tablet', '40mg'),
('Azithromycin', 'Azee', 'Tablet', '500mg'),
('Ceftriaxone', 'Monocef', 'Injection', '1g');

CREATE INDEX idx_diagnoses_patient ON diagnoses(patient_id);
CREATE INDEX idx_prescriptions_visit ON prescriptions(visit_id);
CREATE INDEX idx_prescription_lines_rx ON prescription_lines(prescription_id);



-- --- MIGRATE: V7__init_lab_schema.sql --- --
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



-- --- MIGRATE: V8__init_radiology_schema.sql --- --
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



-- --- MIGRATE: V9__init_pharmacy_schema.sql --- --
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



-- --- MIGRATE: V10__init_nursing_schema.sql --- --
-- NURSING MODULE SCHEMA

CREATE TABLE patient_vitals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    recorded_by UUID NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    temperature DECIMAL(5,2), -- Fahrenheit or Celsius
    heart_rate INTEGER, -- bpm
    blood_pressure_systolic INTEGER, -- mmHg
    blood_pressure_diastolic INTEGER, -- mmHg
    respiratory_rate INTEGER, -- breaths/min
    spo2 INTEGER, -- percentage
    pain_score INTEGER, -- 0-10
    news_score INTEGER, -- National Early Warning Score
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE med_administrations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prescription_id UUID NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    administered_by UUID NOT NULL REFERENCES users(id),
    administered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_time TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'Given', -- Given, Missed, Refused
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_patient_vitals_patient ON patient_vitals(patient_id);
CREATE INDEX idx_med_admin_rx ON med_administrations(prescription_id);
CREATE INDEX idx_med_admin_patient ON med_administrations(patient_id);



-- --- MIGRATE: V11__init_icu_schema.sql --- --
-- ICU MANAGEMENT MODULE SCHEMA

CREATE TABLE icu_charting (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    visit_id UUID REFERENCES visits(id),
    recorded_by UUID NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Vitals
    heart_rate INTEGER,
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    mean_arterial_pressure INTEGER, -- MAP
    temperature DECIMAL(5,2),
    spo2 INTEGER,
    -- Ventilator
    ventilator_mode VARCHAR(50), -- AC, SIMV, CPAP, PSV, Room Air
    fio2 DECIMAL(5,2), -- Percentage
    peep DECIMAL(5,2), -- cmH2O
    -- Fluid Balance
    intake_volume INTEGER, -- ml
    output_volume INTEGER, -- ml
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE icu_scores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    recorded_by UUID NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    score_type VARCHAR(50) NOT NULL, -- GCS, APACHE_II, SOFA
    -- Specific to GCS for MVP
    gcs_eye INTEGER, -- 1-4
    gcs_verbal INTEGER, -- 1-5
    gcs_motor INTEGER, -- 1-6
    total_score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_icu_charting_patient ON icu_charting(patient_id);
CREATE INDEX idx_icu_scores_patient ON icu_scores(patient_id);



-- --- MIGRATE: V12__init_ot_schema.sql --- --
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



-- --- MIGRATE: V13__init_discharge_schema.sql --- --
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



-- --- MIGRATE: V14__init_billing_schema.sql --- --
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



-- --- MIGRATE: V15__init_inventory_schema.sql --- --
-- INVENTORY MANAGEMENT MODULE SCHEMA

CREATE TABLE inventory_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_code VARCHAR(50) NOT NULL UNIQUE,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL, -- Consumables, Pharmaceuticals, Implants, Assets
    unit_of_measure VARCHAR(50) NOT NULL, -- Box, Vial, Piece
    reorder_level INTEGER NOT NULL DEFAULT 10,
    current_stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE inventory_vendors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vendor_code VARCHAR(50) NOT NULL UNIQUE,
    vendor_name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(100),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(50),
    rating DECIMAL(3,2), -- out of 5.0
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    po_number VARCHAR(50) NOT NULL UNIQUE,
    vendor_id UUID NOT NULL REFERENCES inventory_vendors(id),
    status VARCHAR(50) NOT NULL DEFAULT 'Draft', -- Draft, Approved, Dispatched, Delivered
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    expected_delivery_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Note: PO Items, GRN, and detailed stock movements are deferred for MVP



-- --- MIGRATE: V16__init_operations_schema.sql --- --
-- OPERATIONS MANAGEMENT MODULE SCHEMA

CREATE TABLE housekeeping_tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    zone VARCHAR(100) NOT NULL, -- Ward A, OT 1, Lobby
    description VARCHAR(255) NOT NULL,
    priority VARCHAR(50) NOT NULL DEFAULT 'Routine', -- Routine, Urgent, Emergency
    status VARCHAR(50) NOT NULL DEFAULT 'Pending', -- Pending, In_Progress, Completed
    assigned_to VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE work_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL, -- HVAC, Electrical, Plumbing, IT
    location VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(50) NOT NULL DEFAULT 'Medium', -- Low, Medium, High, Critical
    status VARCHAR(50) NOT NULL DEFAULT 'Open', -- Open, Assigned, In_Progress, Resolved, Closed
    assigned_technician VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transport_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID REFERENCES patients(id), -- Nullable for non-patient transport
    from_location VARCHAR(100) NOT NULL,
    to_location VARCHAR(100) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    priority VARCHAR(50) NOT NULL DEFAULT 'Routine',
    status VARCHAR(50) NOT NULL DEFAULT 'Requested', -- Requested, Dispatched, In_Transit, Completed
    assigned_porter VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);



-- --- MIGRATE: V17__init_staff_schema.sql --- --
-- DOCTOR & STAFF MANAGEMENT MODULE SCHEMA

CREATE TABLE staff_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL, -- Consultant, Resident, Intern, Nurse, Admin
    department VARCHAR(100) NOT NULL,
    contact_number VARCHAR(50),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE duty_rosters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    staff_id UUID NOT NULL REFERENCES staff_profiles(id) ON DELETE CASCADE,
    shift_date DATE NOT NULL,
    shift_type VARCHAR(50) NOT NULL, -- Morning, Evening, Night, On-Call
    location VARCHAR(100) NOT NULL, -- Ward A, ICU, ER
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE leave_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    staff_id UUID NOT NULL REFERENCES staff_profiles(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Pending', -- Pending, Approved, Denied
    approved_by UUID REFERENCES staff_profiles(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE cross_consultations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    requesting_doctor_id UUID NOT NULL REFERENCES staff_profiles(id),
    target_department VARCHAR(100) NOT NULL,
    reason_for_consult TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Requested', -- Requested, Accepted, Completed
    consulting_doctor_id UUID REFERENCES staff_profiles(id),
    consultation_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);



-- --- MIGRATE: V18__init_auxiliary_schema.sql --- --
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



