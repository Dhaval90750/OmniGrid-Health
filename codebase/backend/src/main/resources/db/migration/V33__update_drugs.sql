ALTER TABLE drugs 
    ADD COLUMN is_lasa BOOLEAN DEFAULT FALSE,
    ADD COLUMN schedule_category VARCHAR(50),
    ADD COLUMN hsn_code VARCHAR(50),
    ADD COLUMN gst_percentage DECIMAL(5,2);
