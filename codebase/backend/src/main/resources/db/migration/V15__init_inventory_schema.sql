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
