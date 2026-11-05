# Claim form configuration and claim details

The orchestration service reads `_index.json` from
`claims-backend-orchestration/src/main/resources/config/` at startup. The
manifest maps claim types to their option files (`motor_claims.json`,
`property_claims.json`), which are loaded into an in-memory cache and served at
`GET http://localhost:8080/api/config/motor-claims` and
`GET http://localhost:8080/api/config/property-claims` through the API service.
The orchestration service exposes the corresponding internal endpoints at
`GET http://localhost:8081/internal/config/motor-claims` and
`GET http://localhost:8081/internal/config/property-claims`. Edit a JSON file
and restart the service to apply changes; these form options are application
configuration, not database migrations.

Claim submissions can include optional `motorDetails` or `propertyDetails`
objects. The orchestration service persists them in the one-to-one
`motor_claim_details` and `property_claim_details` tables. Flyway migration V3
creates these tables on startup.