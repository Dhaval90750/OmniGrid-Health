ALTER TABLE admissions 
    ADD COLUMN admission_type VARCHAR(50),
    ADD COLUMN room_type VARCHAR(50),
    ADD COLUMN insurance VARCHAR(100),
    ADD COLUMN pre_auth_number VARCHAR(50),
    ADD COLUMN consent_for_treatment BOOLEAN DEFAULT FALSE,
    ADD COLUMN mlc_flag BOOLEAN DEFAULT FALSE,
    ADD COLUMN provisional_diagnosis VARCHAR(255);
