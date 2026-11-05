ALTER TABLE claims
    ADD COLUMN recommendation_outcome VARCHAR(255),
    ADD COLUMN recommendation_amount NUMERIC(15, 2),
    ADD COLUMN recommendation_reason TEXT,
    ADD COLUMN recommendation_by VARCHAR(255),
    ADD COLUMN recommendation_date TIMESTAMP;
