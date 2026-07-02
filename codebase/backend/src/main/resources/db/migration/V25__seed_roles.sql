-- Create temp table to hold our predefined roles
CREATE TEMP TABLE tmp_roles (
    name VARCHAR(100),
    description VARCHAR(255)
);

INSERT INTO tmp_roles (name, description) VALUES
('ROLE_SUPER_ADMIN', 'Super Administrator'),
('ROLE_HOSPITAL_ADMIN', 'Hospital Administrator'),
('ROLE_DOCTOR', 'Doctor / Physician'),
('ROLE_NURSE', 'Nursing Staff'),
('ROLE_LAB_TECH', 'Laboratory Technician'),
('ROLE_PATHOLOGIST', 'Pathologist'),
('ROLE_RADIOLOGIST', 'Radiologist'),
('ROLE_PHARMACIST', 'Pharmacist'),
('ROLE_RECEPTIONIST', 'Front Desk Receptionist'),
('ROLE_BILLING_EXEC', 'Billing Executive'),
('ROLE_INVENTORY_MGR', 'Inventory Manager'),
('ROLE_OPERATIONS_MGR', 'Operations Manager'),
('ROLE_DIETITIAN', 'Dietitian'),
('ROLE_MANAGEMENT', 'Hospital Management');

-- Insert missing roles
INSERT INTO roles (name, description, created_by, updated_by)
SELECT t.name, t.description, 'system', 'system'
FROM tmp_roles t
WHERE NOT EXISTS (
    SELECT 1 FROM roles r WHERE r.name = t.name
);

-- Delete any roles that are not in the predefined list, but only if they are not currently assigned to users.
-- We might just deactivate them instead of deleting to avoid foreign key violations.
UPDATE roles
SET is_active = FALSE
WHERE name NOT IN (SELECT name FROM tmp_roles);

DROP TABLE tmp_roles;
