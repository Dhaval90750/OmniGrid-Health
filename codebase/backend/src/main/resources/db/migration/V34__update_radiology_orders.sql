ALTER TABLE radiology_orders
    ADD COLUMN contrast_allergy_check BOOLEAN DEFAULT FALSE,
    ADD COLUMN pregnancy_check BOOLEAN DEFAULT FALSE;
