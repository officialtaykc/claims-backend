-- Authentication-ready account records. Store only salted password hashes
-- produced by the application's password encoder; never store raw passwords.
CREATE TABLE claimant_accounts (
    id              UUID PRIMARY KEY,
    email           VARCHAR(320) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(50),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at   TIMESTAMP,
    CONSTRAINT chk_claimant_accounts_email
        CHECK (BTRIM(email) <> ''),
    CONSTRAINT chk_claimant_accounts_first_name
        CHECK (BTRIM(first_name) <> ''),
    CONSTRAINT chk_claimant_accounts_last_name
        CHECK (BTRIM(last_name) <> ''),
    CONSTRAINT chk_claimant_accounts_password_hash
        CHECK (BTRIM(password_hash) <> '')
);

CREATE UNIQUE INDEX uq_claimant_accounts_email_lower
    ON claimant_accounts (LOWER(email));

CREATE TABLE claims_staff_accounts (
    id              UUID PRIMARY KEY,
    username        VARCHAR(100) NOT NULL,
    staff_number    VARCHAR(100),
    email           VARCHAR(320) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(50) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at   TIMESTAMP,
    CONSTRAINT uq_claims_staff_accounts_staff_number UNIQUE (staff_number),
    CONSTRAINT chk_claims_staff_accounts_email
        CHECK (BTRIM(email) <> ''),
    CONSTRAINT chk_claims_staff_accounts_username
        CHECK (BTRIM(username) <> ''),
    CONSTRAINT chk_claims_staff_accounts_first_name
        CHECK (BTRIM(first_name) <> ''),
    CONSTRAINT chk_claims_staff_accounts_last_name
        CHECK (BTRIM(last_name) <> ''),
    CONSTRAINT chk_claims_staff_accounts_role
        CHECK (BTRIM(role) <> ''),
    CONSTRAINT chk_claims_staff_accounts_password_hash
        CHECK (BTRIM(password_hash) <> '')
);

CREATE UNIQUE INDEX uq_claims_staff_accounts_username_lower
    ON claims_staff_accounts (LOWER(username));

CREATE UNIQUE INDEX uq_claims_staff_accounts_email_lower
    ON claims_staff_accounts (LOWER(email));

-- Existing claims remain valid; account registration can link them by email.
ALTER TABLE claims
    ADD COLUMN claimant_account_id UUID
        REFERENCES claimant_accounts (id) ON DELETE SET NULL;

CREATE INDEX idx_claims_claimant_account_id
    ON claims (claimant_account_id);
