CREATE TABLE claim_attachments (
    id                    UUID PRIMARY KEY,
    claim_id              UUID         NOT NULL REFERENCES claims (id),
    information_request_id UUID         REFERENCES claim_information_requests (id),
    file_name             VARCHAR(255) NOT NULL,
    content_type          VARCHAR(255) NOT NULL,
    content               BYTEA        NOT NULL,
    uploaded_by           VARCHAR(255) NOT NULL,
    uploaded_at           TIMESTAMP    NOT NULL
);

CREATE INDEX idx_attachments_claim_id
    ON claim_attachments (claim_id);

CREATE INDEX idx_attachments_information_request_id
    ON claim_attachments (information_request_id);
