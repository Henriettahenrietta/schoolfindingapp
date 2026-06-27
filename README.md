# UniMatch Cameroon

**Find Your Future University** — a smart educational-institution discovery system: search, compare,
evaluate, and navigate to schools and universities. Built from the project SRS
(`Saturated_School_Finder_SRS_Final_Corrected.pdf`).

> Note: the display/brand name is **UniMatch Cameroon**; the internal code package/namespace
> remains `com.schoolfinder` to avoid a disruptive rename.

This is a **monorepo**:

| Folder      | What it is                                            | Stack                              |
| ----------- | ----------------------------------------------------- | ---------------------------------- |
| `backend/`  | REST API + data layer                                 | Kotlin, Spring Boot 3, PostgreSQL  |
| `android/`  | Mobile client                                         | Kotlin, Jetpack Compose            |

## Roles
- **Guest** — browse and search schools.
- **Registered Student** — rate, review, and save favourites.
- **Administrator** — manage schools, users, reviews, and view analytics.

## ▶ Run the app (easiest — only needs Node.js)

The full UI runs as a web app served by a lightweight Node server (API + UI on one port).
On any machine with **[Node.js 18+](https://nodejs.org)** installed:

```bash
npm start
```

Then open **http://localhost:8080**. That's it — one command, one URL.

> Open **localhost** (not `127.0.0.1`) so Google sign-in works.

### Signing in
- **Demo (always works):** Sign in → *Use a demo account* → **Continue as admin / student**.
- **Real Firebase (Google + email/password):** already wired (`web/firebase-config.js`). In the
  [Firebase console](https://console.firebase.google.com) for project `unimatch-42d8b`, enable
  **Authentication → Sign-in method → Google + Email/Password**. Admin email: `superadmin@unimatch.cm`.

If Google sign-in ever errors, just use **Use a demo account → Continue as admin** — authentication
always works via the demo path.

## 📱 Use it on your phone (installable app / PWA)

The web app is a **PWA**, so it installs to your home screen and runs full-screen like a native app.

1. **Deploy it** so your phone can reach it: render.com → **New ▸ Blueprint** ▸ pick this repo ▸
   **Apply** (uses `render.yaml`). You get a public URL like `https://unimatch-cameroon.onrender.com`.
2. **Open that URL on your phone's browser.**
3. **Install it:**
   - **Android (Chrome):** menu **⋮ → Add to Home screen / Install app**.
   - **iPhone (Safari):** **Share → Add to Home Screen**.
4. A **UniMatch** icon appears on your home screen — tap it to open the app.

> After deploying, add your Render domain to **Firebase → Authentication → Settings →
> Authorized domains** so Google sign-in works there (demo sign-in works regardless).

## Quick start (real backend, no local Java needed)

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
