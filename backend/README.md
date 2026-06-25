# School Finder — Backend

Kotlin + Spring Boot 3 REST API backed by PostgreSQL.

## Run with Docker (recommended — no local Java needed)

From the **repo root**:

```bash
docker compose up --build
```

Starts PostgreSQL + the API on http://localhost:8080. Flyway applies the schema and Cameroon sample
data on first boot.

> Requires a working Docker engine. On Windows this needs Docker Desktop with WSL2 / "Virtual Machine
> Platform" enabled and hardware virtualization turned on in the BIOS.

## Run locally without Docker

### Lightest option — in-memory H2 (only a JDK 21 needed, no PostgreSQL)

```bash
cd backend
gradle bootRun --args='--spring.profiles.active=local'
```

This starts the API on http://localhost:8080 with an in-memory H2 database, the schema built from
the entities, and the same Cameroon sample data. Nothing else to install. Browse the data at
http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:schoolfinder`, user `sa`, empty password).
Data resets on restart.

> No Gradle installed? Once the wrapper jar exists use `./gradlew`; otherwise install Gradle 8.9, or
> run `gradle wrapper` once. The first build downloads dependencies — slow on a poor connection, then
> cached.

### With PostgreSQL

Needs JDK 21 and a running PostgreSQL with a `schoolfinder` database (user/password `schoolfinder`).

```bash
cd backend
gradle bootRun
```

Override DB settings via `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
`SPRING_DATASOURCE_PASSWORD`.

## Authentication

- **Dev mode (default, `FIREBASE_ENABLED=false`)** — requests are identified by headers:
  `X-Debug-Uid` (required), optional `X-Debug-Name`, `X-Debug-Email`, `X-Debug-Role`
  (`STUDENT`|`ADMIN`). This lets the whole stack run without a Firebase project. The Android app
  uses this automatically.
- **Firebase mode (`FIREBASE_ENABLED=true`)** — set `FIREBASE_CREDENTIALS_PATH` to a service-account
  JSON. The API then verifies `Authorization: Bearer <firebase-id-token>`.

A user record is created automatically on first authenticated request.

## API reference (base path `/api/v1`)

| Method | Path                         | Auth     | Description                                  |
| ------ | ---------------------------- | -------- | -------------------------------------------- |
| GET    | `/meta`                      | public   | Region, currency, map centre, categories     |
| GET    | `/schools`                   | public   | Search/filter (`q,category,city,minRating,maxTuition,sort,page,size`) |
| GET    | `/schools/{id}`              | public   | School detail + programs + images            |
| GET    | `/schools/compare?ids=1,2`   | public   | Compare 2–4 schools                          |
| GET    | `/schools/{id}/reviews`      | public   | Approved reviews for a school                |
| POST   | `/schools/{id}/reviews`      | student  | Create/update your review `{rating,comment}` |
| GET    | `/favorites`                 | student  | Your favourite schools                       |
| POST   | `/favorites/{schoolId}`      | student  | Add favourite                                |
| DELETE | `/favorites/{schoolId}`      | student  | Remove favourite                             |
| GET    | `/me`                        | student  | Your profile                                 |
| GET    | `/me/reviews`                | student  | Your reviews                                 |
| DELETE | `/me/reviews/{id}`           | student  | Delete your review                           |
| POST   | `/admin/schools`             | admin    | Create school                                |
| PUT    | `/admin/schools/{id}`        | admin    | Update school                                |
| DELETE | `/admin/schools/{id}`        | admin    | Delete school                                |
| GET    | `/admin/users`               | admin    | List users                                   |
| PUT    | `/admin/users/{id}/active`   | admin    | Activate/deactivate a user (`?active=`)      |
| PUT    | `/admin/users/{id}/role`     | admin    | Change role (`?role=STUDENT|ADMIN`)          |
| GET    | `/admin/reviews/pending`     | admin    | Reviews awaiting moderation                  |
| PUT    | `/admin/reviews/{id}/status` | admin    | Moderate (`?status=APPROVED|REJECTED`)       |
| GET    | `/admin/analytics`           | admin    | Counts + schools-by-category                 |

### Examples (dev mode)

```bash
# Public discovery
curl http://localhost:8080/api/v1/schools
curl "http://localhost:8080/api/v1/schools?category=UNIVERSITY&q=buea"
curl "http://localhost:8080/api/v1/schools/compare?ids=1,5"

# As a student (dev header)
curl -X POST http://localhost:8080/api/v1/schools/1/reviews \
  -H "X-Debug-Uid: student-9" -H "X-Debug-Name: Test" \
  -H "Content-Type: application/json" -d '{"rating":5,"comment":"Great!"}'

# As an admin
curl http://localhost:8080/api/v1/admin/analytics \
  -H "X-Debug-Uid: admin-dev" -H "X-Debug-Role: ADMIN"
```

## Tech / layout

```
src/main/kotlin/com/schoolfinder/
  config/      AppProperties (region, firebase, cloudinary, maps)
  domain/      JPA entities + enums
  repository/  Spring Data JPA repositories
  service/     business logic (search, compare, reviews, favourites, admin)
  security/    Firebase token filter + dev fallback, security config
  api/         controllers, DTOs, error handling
src/main/resources/
  application.yml
  db/migration/  Flyway: V1 schema, V2 Cameroon seed data
```

External integrations (Firebase, Cloudinary, Google Maps) are wired as configuration with safe
local-dev fallbacks — drop real credentials into the documented env vars to enable them.
