// Real Firebase Authentication config for the web app.
//
// To ENABLE real Firebase Auth:
//   1. Firebase console → create/select a project.
//   2. Authentication → Sign-in method → enable "Email/Password".
//   3. Project settings → Your apps → Web app → "SDK setup and configuration" → Config.
//   4. Paste the values below (the web apiKey is NOT a secret — safe to commit).
//
// Leave apiKey empty to keep the lightweight dev sign-in (X-Debug headers).
window.FIREBASE_CONFIG = {
  apiKey: "",
  authDomain: "",
  projectId: "",
  appId: "",
};

// Emails treated as admins by the local Node dev server when they sign in via Firebase.
window.ADMIN_EMAILS = ["admin@unimatch.cm"];
