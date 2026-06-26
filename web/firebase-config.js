// Real Firebase Authentication config for the web app.
//
// To ENABLE real Firebase Auth:
//   1. Firebase console → create/select a project.
//   2. Authentication → Sign-in method → enable "Email/Password" AND "Google".
//   3. Project settings → Your apps → Web app → "SDK setup and configuration" → Config.
//   4. Paste the values below (the web apiKey is NOT a secret — safe to commit).
//
// Leave apiKey empty to keep the lightweight dev sign-in (X-Debug headers).
window.FIREBASE_CONFIG = {
  apiKey: "AIzaSyDMq6_SNzQwwlc_h_bBUITtcVS6XOl298E",
  authDomain: "unimatch-42d8b.firebaseapp.com",
  projectId: "unimatch-42d8b",
  storageBucket: "unimatch-42d8b.firebasestorage.app",
  messagingSenderId: "386455509232",
  appId: "1:386455509232:web:43702311fec5802c538a35",
  measurementId: "G-320LHZ2DWS",
};

// Emails treated as admins by the local Node dev server when they sign in via Firebase.
// (The server reads its own ADMIN_EMAILS; this is documentation of the default.)
window.ADMIN_EMAILS = ["superadmin@unimatch.cm"];
