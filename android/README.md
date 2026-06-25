# School Finder — Android app

Kotlin + Jetpack Compose client for the School Finder backend.

## Run

1. Start the backend first (see [../backend/README.md](../backend/README.md)). The app expects it at
   `http://10.0.2.2:8080/` — the Android **emulator's** alias for your host machine.
2. Open the `android/` folder in **Android Studio** (Hedgehog or newer) and let it sync Gradle.
   Android Studio supplies the Android SDK and a JDK; no separate install is needed.
3. Run the `app` configuration on an emulator (API 24+).

> Testing on a **physical device**? Change `API_BASE_URL` in [app/build.gradle.kts](app/build.gradle.kts)
> to your computer's LAN IP, e.g. `http://192.168.1.10:8080/`.

## Authentication (dev vs Firebase)

To keep the app runnable without any cloud setup, it ships with **dev auth**: the login screen
identifies you to the backend via `X-Debug-Uid` / `X-Debug-Name` / `X-Debug-Role` headers, which the
backend trusts while `app.firebase.enabled=false`. Use the "demo student" / "demo admin" buttons, or
type a name and pick a role.

### Switching to real Firebase Authentication
1. Create a Firebase project and add an Android app with package `com.schoolfinder.app`.
2. Put the generated `google-services.json` in `app/`.
3. In [app/build.gradle.kts](app/build.gradle.kts), apply the `com.google.gms.google-services`
   plugin and uncomment the `firebase-bom` / `firebase-auth-ktx` dependencies.
4. Replace the dev login UI with a Firebase sign-in flow, and change the OkHttp interceptor in
   [Network.kt](app/src/main/java/com/schoolfinder/app/data/remote/Network.kt) to send
   `Authorization: Bearer <firebase-id-token>` instead of the `X-Debug-*` headers.
5. Enable token verification on the backend (`FIREBASE_ENABLED=true` + a service-account file).

## Structure

```
app/src/main/java/com/schoolfinder/app/
  data/            session + repository
  data/remote/     Retrofit service, models, OkHttp/Moshi setup
  di/              ServiceLocator (manual DI)
  ui/              theme, shared components, helpers
  ui/screens/      Login, Home, Detail, Compare, Favourites, Profile (+ ViewModels)
  MainActivity.kt  hosts Compose + navigation
```

## Features
- Search & category filter (Discover tab)
- School detail: programs, rating, reviews, favourite toggle, **Navigate** (opens the device map app)
- Side-by-side **compare** of 2–4 schools (select with the checkboxes, then the Compare button)
- Submit ratings & reviews
- Favourites
- Profile with your reviews and sign-out

## Notes
- Maps navigation uses a `geo:` intent handled by the device's map app, so **no Google Maps API key
  is required** for the build. To embed an in-app map later, add the Maps Compose SDK + an API key.
- The Gradle wrapper `jar` is not committed. Android Studio sets it up on sync; for command-line
  builds run `gradle wrapper` once (or use a local Gradle 8.9).
