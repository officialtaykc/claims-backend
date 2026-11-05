INSERT INTO claims_staff_accounts (
    id, username, staff_number, email, password_hash, first_name, last_name, role
) VALUES (
    'a2000000-0000-4000-8000-000000000003',
    'officer.b',
    'DEMO-OFF-002',
    'officer.b@chubb.com',
    'pbkdf2-sha256$120000$Y2xhaW1zLWRlbW8tb2ZmaWNlci1iLXYx$f9b9GDhe53IMAysRrJwR007DucmDBBCL+3PKVxaLdQk=',
    'Jamie',
    'Chen',
    'OFFICER'
);

CREATE TABLE claim_assignment_state (
    allocation_key VARCHAR(100) PRIMARY KEY,
    next_index     BIGINT NOT NULL CHECK (next_index >= 0)
);

INSERT INTO claim_assignment_state (allocation_key, next_index)
VALUES ('CLAIMS_OFFICERS', 0);
