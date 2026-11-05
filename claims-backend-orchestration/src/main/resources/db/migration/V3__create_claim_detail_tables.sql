CREATE TABLE motor_claim_details (
    claim_id                UUID PRIMARY KEY REFERENCES claims (id) ON DELETE CASCADE,
    vehicle_registration    VARCHAR(50),
    vehicle_model           VARCHAR(255),
    driver_name             VARCHAR(255),
    driver_licence_number   VARCHAR(100),
    third_party_involved    BOOLEAN,
    police_report_number    VARCHAR(100),
    repair_workshop         VARCHAR(255),
    claim_category          VARCHAR(100)
);

CREATE TABLE property_claim_details (
    claim_id                  UUID PRIMARY KEY REFERENCES claims (id) ON DELETE CASCADE,
    property_type             VARCHAR(50),
    property_address          TEXT,
    affected_areas            TEXT,
    damage_to_building        BOOLEAN,
    damage_to_contents        BOOLEAN,
    cause_of_loss             VARCHAR(100),
    estimated_repair_cost     NUMERIC(15,2),
    estimated_contents_loss   NUMERIC(15,2)
);
