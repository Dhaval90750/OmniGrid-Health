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
