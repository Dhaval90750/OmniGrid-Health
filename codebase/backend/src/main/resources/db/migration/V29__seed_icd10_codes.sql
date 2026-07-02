-- Create index for full-text search
ALTER TABLE icd10_codes ADD COLUMN text_search tsvector GENERATED ALWAYS AS (to_tsvector('english', coalesce(code, '') || ' ' || coalesce(description, ''))) STORED;

CREATE INDEX idx_icd10_text_search ON icd10_codes USING GIN(text_search);

-- Seed ICD-10 data
INSERT INTO icd10_codes (id, code, description) VALUES
(gen_random_uuid(), 'A00', 'Cholera'),
(gen_random_uuid(), 'A01', 'Typhoid and paratyphoid fevers'),
(gen_random_uuid(), 'A09', 'Infectious gastroenteritis and colitis, unspecified'),
(gen_random_uuid(), 'E10', 'Type 1 diabetes mellitus'),
(gen_random_uuid(), 'E11', 'Type 2 diabetes mellitus'),
(gen_random_uuid(), 'I10', 'Essential (primary) hypertension'),
(gen_random_uuid(), 'I20', 'Angina pectoris'),
(gen_random_uuid(), 'I21', 'Acute myocardial infarction'),
(gen_random_uuid(), 'J00', 'Acute nasopharyngitis [common cold]'),
(gen_random_uuid(), 'J01', 'Acute sinusitis'),
(gen_random_uuid(), 'J09', 'Influenza due to certain identified influenza viruses'),
(gen_random_uuid(), 'J45', 'Asthma'),
(gen_random_uuid(), 'K21', 'Gastro-esophageal reflux disease'),
(gen_random_uuid(), 'K35', 'Acute appendicitis'),
(gen_random_uuid(), 'N17', 'Acute kidney failure'),
(gen_random_uuid(), 'N20', 'Calculus of kidney and ureter'),
(gen_random_uuid(), 'R07.9', 'Chest pain, unspecified'),
(gen_random_uuid(), 'R50.9', 'Fever, unspecified'),
(gen_random_uuid(), 'S02.0', 'Fracture of vault of skull'),
(gen_random_uuid(), 'Z00.0', 'Encounter for general adult medical examination')
ON CONFLICT (code) DO NOTHING;
