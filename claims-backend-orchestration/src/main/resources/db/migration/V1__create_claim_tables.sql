-- Claims Management schema (PostgreSQL)
-- Mirrors the JPA entities in claims-backend-orchestration.

CREATE TABLE claims (
    id                  UUID PRIMARY KEY,
    event_id            UUID         NOT NULL UNIQUE,
    claim_reference     VARCHAR(255) NOT NULL UNIQUE,
    claimant_name       VARCHAR(255) NOT NULL,
    claimant_email      VARCHAR(255) NOT NULL,
    claimant_phone      VARCHAR(255),
    market              VARCHAR(255) NOT NULL,
    claim_type          VARCHAR(255) NOT NULL,
    policy_number       VARCHAR(255) NOT NULL,
    incident_date       DATE         NOT NULL,
    incident_location   VARCHAR(255),
    incident_category   VARCHAR(255),
    incident_description TEXT,
    asset_reference     VARCHAR(255),
    estimated_loss      NUMERIC(15,2),
    currency            VARCHAR(3),
    status              VARCHAR(255) NOT NULL,
    priority            VARCHAR(255) NOT NULL,
    assigned_to         VARCHAR(255),
    decision_amount     NUMERIC(15,2),
    decision_reason     TEXT,
    decision_date       TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL
);

CREATE INDEX idx_claims_claim_reference ON claims (claim_reference);
CREATE INDEX idx_claims_claimant_email  ON claims (claimant_email);
CREATE INDEX idx_claims_status          ON claims (status);
CREATE INDEX idx_claims_assigned_to     ON claims (assigned_to);

CREATE TABLE claim_status_history (
    id              UUID PRIMARY KEY,
    claim_id        UUID         NOT NULL REFERENCES claims (id),
    previous_status VARCHAR(255),
    new_status      VARCHAR(255) NOT NULL,
    changed_by      VARCHAR(255) NOT NULL,
    comment         TEXT,
    changed_at      TIMESTAMP    NOT NULL
);

CREATE INDEX idx_history_claim_id ON claim_status_history (claim_id);

CREATE TABLE claim_information_requests (
    id               UUID PRIMARY KEY,
    claim_id         UUID         NOT NULL REFERENCES claims (id),
    request_message  TEXT         NOT NULL,
    requested_by     VARCHAR(255) NOT NULL,
    due_date         DATE,
    request_status   VARCHAR(255) NOT NULL,
    response_message TEXT,
    requested_at     TIMESTAMP    NOT NULL,
    responded_at     TIMESTAMP
);

CREATE INDEX idx_info_requests_claim_id ON claim_information_requests (claim_id);

CREATE TABLE claim_documents (
    id            UUID PRIMARY KEY,
    claim_id      UUID         NOT NULL REFERENCES claims (id),
    file_name     VARCHAR(255) NOT NULL,
    content_type  VARCHAR(255),
    storage_path  VARCHAR(255),
    uploaded_by   VARCHAR(255),
    uploaded_at   TIMESTAMP    NOT NULL
);

CREATE INDEX idx_documents_claim_id ON claim_documents (claim_id);
