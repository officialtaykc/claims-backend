# Claim form configuration and claim details

The API service reads `_index.json` from
`claims-backend-api/src/main/resources/config/` during `ClaimsApiApplication`
startup. The manifest maps claim types to their option files
(`motor_claims.json`, `property_claims.json`), which are loaded into an
in-memory cache and served directly at
`GET http://localhost:8080/api/config/motor-claims` and
`GET http://localhost:8080/api/config/property-claims`. Edit a JSON file and
restart the API service to apply changes; these form options are application
configuration, not database migrations.

Claim submissions can include optional `motorDetails` or `propertyDetails`
objects. The orchestration service persists them in the one-to-one
`motor_claim_details` and `property_claim_details` tables. Flyway migration V3
creates these tables on startup.

Claimant-owned policy records are stored in `claimant_policies` and seeded for
the local demo claimant by Flyway V5. The API exposes
`GET /api/claimant/policies?claimantEmail=john.tan@example.com`; each record
includes its active status and the claimant's name, email and phone from the
account database. The frontend displays these policies on the claimant
dashboard and uses the selected active policy to populate the claim form.

Claim submissions are consumed by the orchestration service, persisted with
an initial `NEW` status, assigned in round-robin order to active staff accounts
with the `OFFICER` role, and transitioned to `IN_REVIEW`. Flyway V6 seeds a
second demo officer and creates the persisted assignment cursor; adding or
removing active officers automatically changes the round-robin pool. The
claimant's claim returns to `IN_REVIEW` after all outstanding information
requests have been answered. The second local demo officer signs in as
`officer.b` with password `OfficerDemo!26`.