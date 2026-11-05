CREATE TABLE claimant_policies (
    id                  UUID PRIMARY KEY,
    claimant_account_id UUID NOT NULL REFERENCES claimant_accounts (id),
    policy_number       VARCHAR(100) NOT NULL UNIQUE,
    claim_type          VARCHAR(20) NOT NULL,
    product_name        VARCHAR(255) NOT NULL,
    market              VARCHAR(100) NOT NULL,
    currency            VARCHAR(3) NOT NULL,
    insured_item        VARCHAR(255),
    coverage_amount     NUMERIC(15,2),
    effective_from      DATE NOT NULL,
    effective_to        DATE NOT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_claimant_policies_claim_type
        CHECK (claim_type IN ('MOTOR', 'PROPERTY')),
    CONSTRAINT chk_claimant_policies_dates
        CHECK (effective_to >= effective_from)
);

CREATE INDEX idx_claimant_policies_account_active
    ON claimant_policies (claimant_account_id, is_active);

INSERT INTO claimant_policies (
    id, claimant_account_id, policy_number, claim_type, product_name, market,
    currency, insured_item, coverage_amount, effective_from, effective_to, is_active
) VALUES
(
    'a3000000-0000-4000-8000-000000000001',
    'a1000000-0000-4000-8000-000000000001',
    'MTR-SG-2026-001',
    'MOTOR',
    'Comprehensive Motor Protect',
    'Singapore',
    'SGD',
    'Toyota Camry · SGP 1234A',
    100000.00,
    '2026-01-01',
    '2026-12-31',
    TRUE
),
(
    'a3000000-0000-4000-8000-000000000002',
    'a1000000-0000-4000-8000-000000000001',
    'PRP-SG-2026-001',
    'PROPERTY',
    'Home Protect Plus',
    'Singapore',
    'SGD',
    '18 Orchard View, Singapore',
    500000.00,
    '2026-01-01',
    '2026-12-31',
    TRUE
);
