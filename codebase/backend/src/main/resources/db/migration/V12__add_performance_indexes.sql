-- V12: Add Performance Indexes for Analytics & Common Queries
-- Description: Adds composite and single-column indexes on high-traffic tables to improve reporting performance.

-- 1. Patients Table: Frequently searched by UHID, Mobile, and Name
CREATE INDEX IF NOT EXISTS idx_patients_uhid ON patients(uhid);
CREATE INDEX IF NOT EXISTS idx_patients_mobile_number ON patients(mobile_number);
CREATE INDEX IF NOT EXISTS idx_patients_name_dob ON patients(first_name, last_name, date_of_birth);

-- 2. Invoices Table: Crucial for Revenue Analytics by Date and Status
CREATE INDEX IF NOT EXISTS idx_invoices_created_at ON invoices(created_at);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoices_visit_id ON invoices(visit_id);
CREATE INDEX IF NOT EXISTS idx_invoices_admission_id ON invoices(admission_id);

-- 3. Admissions Table: Crucial for Occupancy and ALOS (Average Length of Stay) Metrics
CREATE INDEX IF NOT EXISTS idx_admissions_status ON admissions(status);
CREATE INDEX IF NOT EXISTS idx_admissions_dates ON admissions(admission_date, discharge_date);
CREATE INDEX IF NOT EXISTS idx_admissions_ward_id ON admissions(ward_id);

-- 4. Visits Table: Important for OPD Department Revenue
CREATE INDEX IF NOT EXISTS idx_visits_department_id ON visits(department_id);
CREATE INDEX IF NOT EXISTS idx_visits_status ON visits(status);
CREATE INDEX IF NOT EXISTS idx_visits_visit_date ON visits(visit_date);
