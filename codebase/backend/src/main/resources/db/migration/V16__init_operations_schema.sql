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
