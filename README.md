# Claims Backend — Developer Guide

Spring Boot backend for the APAC claims demo. The Maven reactor contains:

| Module | Purpose | Local URL |
|---|---|---|
| `claims-backend-api` | Frontend-facing REST API and Kafka claim publisher | `http://localhost:8080` |
| `claims-backend-orchestration` | Claim processing, persistence and staff workflows | `http://localhost:8081` |
| `claims-common` | DTOs and enums shared between services | — |

## Requirements

- JDK 17
- Maven 3.9+
- Docker Desktop with Docker Compose

## Start local infrastructure

From the repository root, start PostgreSQL and Kafka:

```powershell
docker compose up -d postgres kafka
docker compose ps
```

The containers expose PostgreSQL on port `5432` and Kafka on port `9092`. The Compose file uses a named volume (`claims-pgdata`), so database records persist when containers are stopped.

Stop the containers but keep database data:

```powershell
docker compose down
```

To also delete the local database volume and all its data:

```powershell
docker compose down --volumes
```

## Local database connection

The default PostgreSQL connection is:

```text
JDBC URL: jdbc:postgresql://localhost:5432/claimsdb
Database: claimsdb
Username: claims
Password: claims
```

For a local SQL client, use host `localhost`, port `5432`, database `claimsdb`, user `claims`, and password `claims`.

The orchestration service reads these values from environment variables, with the defaults above:

| Variable | Default |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `claimsdb` |
| `DB_USER` | `claims` |
| `DB_PASSWORD` | `claims` |

Do not use the demo database credentials outside local development.

## Build and test

From the repository root:

```powershell
mvn clean verify
```

Build and install the shared module and both services into the local Maven repository:

```powershell
mvn clean install
```

Flyway migrations run automatically when the orchestration service starts. Hibernate uses `ddl-auto: validate`; update the entity model and add a new Flyway migration when changing the schema. Do not edit migrations that have already been applied.

## Run the services locally

Start Docker infrastructure first, then build/install the Maven modules with `mvn clean install`.

In separate terminals from the repository root, run the orchestration service:

```powershell
mvn -f claims-backend-orchestration/pom.xml spring-boot:run "-Dspring-boot.run.profiles=local"
```

Then run the API service:

```powershell
mvn -f claims-backend-api/pom.xml spring-boot:run "-Dspring-boot.run.profiles=local"
```

The local profile is declared in `claims-backend-orchestration/src/main/resources/application.yml` and enables additional application debug logging. It does not replace the default datasource, Kafka, or port settings. The API service has no profile-specific settings currently; activating `local` there is harmless.

Default service addresses:

- Frontend API: `http://localhost:8080`
- Orchestration service: `http://localhost:8081`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

The API forwards most claim and staff operations to the orchestration service. Claim submission is asynchronous: the API publishes the request to Kafka, and orchestration consumes and persists it.

### Override local settings

Set environment variables before starting a service to override the defaults. For example, in PowerShell:

```powershell
$env:DB_PASSWORD = "your-local-password"
$env:KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
$env:ORCHESTRATION_BASE_URL = "http://localhost:8081"
```

The API service uses `SERVER_PORT`, `KAFKA_BOOTSTRAP_SERVERS`, `CLAIMS_TOPIC`, and `ORCHESTRATION_BASE_URL`. The orchestration service uses `SERVER_PORT`, the `DB_*` variables above, `KAFKA_BOOTSTRAP_SERVERS`, `CLAIMS_TOPIC`, and the `CLAIMS_PAYMENT_*` variables defined in its application configuration.

## Demo accounts

Flyway seeds local accounts for interactive frontend demonstrations. Current demo credentials are listed in the frontend repository's README. Passwords are stored as salted PBKDF2 hashes in the database; these accounts and credentials are for local demonstration only.

## Claim form options

The API reads JSON option files from `claims-backend-api/src/main/resources/config/`. Edit the relevant JSON file and restart the API service to apply changes. These options are application configuration, not database records or migrations.

## Supporting attachments

Flyway V8 adds `claim_attachments`, linking multiple files to each claim and
optionally to an information request. V9 stores file bytes in the separate
PostgreSQL `claim_attachment_contents` `BYTEA` table to avoid loading file data
when claim metadata is read. The API accepts PDF, JPEG, PNG, text and Word
files, up to 5 files per upload (10 MB each, 20 MB combined). Claim-submission
files are uploaded after the asynchronous Kafka consumer creates the claim.
Officer request files and claimant response files are saved in the same
transaction as their information request or response. Files are available
through the claim detail API and its attachment-content endpoint.

## Health checks and troubleshooting

Actuator health endpoints:

- API: `http://localhost:8080/actuator/health`
- Orchestration: `http://localhost:8081/actuator/health`

If a service does not start, check that PostgreSQL and Kafka are healthy with `docker compose ps`, that ports `5432`, `9092`, `8080`, and `8081` are available, and that both Spring Boot processes are running. For database startup or schema errors, inspect the orchestration logs; Flyway applies migrations during its startup.
