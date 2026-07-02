ALTER TABLE invoices 
    ADD COLUMN gst_amount DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN advance_applied DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN insurance_coverage DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN balance_due DECIMAL(10,2) DEFAULT 0;
