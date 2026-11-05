-- Local demonstration accounts. Passwords are PBKDF2-SHA256 hashes;
-- demo passwords are documented in the frontend README for the localhost demo.
INSERT INTO claimant_accounts (
    id, email, password_hash, first_name, last_name, phone
) VALUES (
    'a1000000-0000-4000-8000-000000000001',
    'john.tan@example.com',
    'pbkdf2-sha256$120000$0hVYsi7Wy4fsGJyBNoeRYg==$iaz8h8JdDnrIZriiHxjiDj6AYARTkbqR3xsFbK50TAY=',
    'John',
    'Tan',
    '+65 8123 4567'
);

INSERT INTO claims_staff_accounts (
    id, username, staff_number, email, password_hash, first_name, last_name, role
) VALUES
(
    'a2000000-0000-4000-8000-000000000001',
    'officer.a',
    'DEMO-OFF-001',
    'officer.a@chubb.com',
    'pbkdf2-sha256$120000$gagwjy4UK9jfciA4Sdx0dQ==$gydHoInv+VCbnzdBybQR8Q9HRhO5ZWEewe4Wq5amUwU=',
    'Alex',
    'Lim',
    'OFFICER'
),
(
    'a2000000-0000-4000-8000-000000000002',
    'manager',
    'DEMO-MGR-001',
    'manager@chubb.com',
    'pbkdf2-sha256$120000$hAijA0ltjJVyCwdworHzxA==$ncIXgPpU3rgsnT/5PI3EJMkxh9U1p90Ep9vQC3oqfOE=',
    'Michelle',
    'Wong',
    'MANAGER'
);
