# School Finder — Android App

A self-contained Android application for discovering, comparing, rating, and
navigating to educational institutions. Built from the School Finder SRS as a
fully offline, runnable Jetpack Compose app — no backend server, API keys, or
internet connection required to build and run.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Navigation:** Navigation Compose
- **Local database:** Room (SQLite)
- **Min SDK:** 24 · **Target/Compile SDK:** 35 · **JDK:** 17
- **Gradle:** 8.9 · **Android Gradle Plugin:** 8.7.3 · **Kotlin:** 2.0.21

## How to run

1. Open **Android Studio** (latest version).
2. Choose **File → Open** and select this `SchoolFinder` folder.
3. Let Gradle sync finish (it will download dependencies on first sync — this
   needs an internet connection the first time only).
4. Pick an emulator or a connected device and press **Run ▶**.

The database is seeded automatically on first launch with 10 institutions in
Cameroon and a few sample reviews.

## Demo accounts

| Role          | How to access                                              |
|---------------|-----------------------------------------------------------|
| Administrator | Log in with **admin@schoolfinder.com** / **admin123**     |
| Student       | Tap **Register** and create any account                   |
| Guest         | Tap **Continue as Guest** on the login screen             |

Administrators get the dashboard (stats, review moderation, add/edit/delete
schools). Students can save favorites and write reviews. Guests can browse,
search, and compare only.

## Features

- Email/password authentication and guest mode (stored locally with Room).
- Search, category filters, and sorting (rating, tuition, name).
- School detail pages with programs, facilities, ratings, and reviews.
- "Open in Maps" launches the device map app via a `geo:` intent (falls back to
  Google Maps in the browser), plus call and website shortcuts.
- Side-by-side comparison of any two schools.
- Favorites for registered students.
- Admin dashboard with overview stats, review moderation, and full school CRUD.

## What is mocked vs. the SRS

The SRS specifies a cloud architecture. To make the project run immediately
with zero setup, those services are represented locally:

| SRS component             | In this build                                    |
|---------------------------|--------------------------------------------------|
| PostgreSQL + Spring Boot  | Room (local SQLite) via a `Repository` layer     |
| Firebase Authentication   | Local accounts in the Room `users` table         |
| Cloudinary image hosting  | Generated colored placeholder tiles with initials|
| Google Maps API           | `geo:` map intents (no API key needed)           |

The `Repository` class is the single data access point, so swapping Room for
real REST API calls later only touches that one layer.

## Project structure

```
app/src/main/java/com/schoolfinder/app/
├── data/          Room entities, DAOs, database, repository, seed data
├── session/       SessionManager (current user / role)
├── ui/
│   ├── components/ Reusable UI (cards, rating stars, placeholders)
│   ├── navigation/ Route definitions
│   ├── screens/    Login, Register, Home, Compare, Favorites, Profile,
│   │               Detail, Admin Dashboard, Manage Schools
│   └── theme/      Material 3 theme
├── MainActivity.kt
└── SchoolFinderApplication.kt
```

## Troubleshooting

- If Gradle reports a wrapper problem, run **File → Sync Project with Gradle
  Files**, or from a terminal in the project root: `./gradlew :app:assembleDebug`.
- First sync must be online so dependencies can download; after that the app
  runs fully offline.
