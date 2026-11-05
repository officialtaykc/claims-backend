CREATE TABLE claim_attachment_contents (
    attachment_id UUID PRIMARY KEY
        REFERENCES claim_attachments (id) ON DELETE CASCADE,
    content       BYTEA NOT NULL
);

INSERT INTO claim_attachment_contents (attachment_id, content)
SELECT id, content
FROM claim_attachments;

ALTER TABLE claim_attachments
    DROP COLUMN content;
