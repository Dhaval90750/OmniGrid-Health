ALTER TABLE clinical_notes 
    RENAME COLUMN physical_examination TO objective_notes;

ALTER TABLE clinical_notes 
    ADD COLUMN family_history TEXT,
    ADD COLUMN social_history TEXT,
    ADD COLUMN review_of_systems TEXT,
    ADD COLUMN subjective_notes TEXT,
    ADD COLUMN assessment_notes TEXT,
    ADD COLUMN plan_notes TEXT,
    ADD COLUMN is_signed BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN signed_at TIMESTAMP,
    ADD COLUMN signed_by UUID,
    ADD COLUMN addendum_text TEXT,
    ADD COLUMN note_version INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN parent_note_id UUID;
