ALTER TABLE lab_tests 
    ADD COLUMN loinc_code VARCHAR(20),
    ADD COLUMN sample_type VARCHAR(50),
    ADD COLUMN tube_color VARCHAR(50),
    ADD COLUMN tat_target_hours INTEGER;
