# School Finder

A smart educational-institution discovery system: search, compare, evaluate, and navigate to
schools and universities. Built from the project SRS (`Saturated_School_Finder_SRS_Final_Corrected.pdf`).

This is a **monorepo**:

| Folder      | What it is                                            | Stack                              |
| ----------- | ----------------------------------------------------- | ---------------------------------- |
| `backend/`  | REST API + data layer                                 | Kotlin, Spring Boot 3, PostgreSQL  |
| `android/`  | Mobile client                                         | Kotlin, Jetpack Compose            |

## Roles
- **Guest** — browse and search schools.
- **Registered Student** — rate, review, and save favourites.
- **Administrator** — manage schools, users, reviews, and view analytics.

## Quick start (backend, no local Java needed)

The backend runs entirely in Docker — you only need Docker Desktop.

```bash
docker compose up --build
```

This starts PostgreSQL and the API. The API listens on http://localhost:8080.
Schema and Cameroon sample data are applied automatically via Flyway migrations.

Try it:

```bash
curl http://localhost:8080/api/v1/schools
curl "http://localhost:8080/api/v1/schools?q=university&city=Buea"
curl "http://localhost:8080/api/v1/schools/compare?ids=1,2"
```

See [backend/README.md](backend/README.md) for the full API reference and auth notes.

## Android app

Open the `android/` folder in Android Studio (Hedgehog or newer). It targets the backend at
`http://10.0.2.2:8080` (the emulator's alias for the host machine). See
[android/README.md](android/README.md).

## External services

Per the SRS the system integrates Firebase Authentication, Cloudinary, and Google Maps.
Everything is wired with **placeholder configuration and local-dev fallbacks**, so the stack
runs end-to-end without real keys. Drop real credentials into the documented env vars when ready.
