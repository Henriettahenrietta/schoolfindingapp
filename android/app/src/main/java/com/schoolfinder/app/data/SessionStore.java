package com.schoolfinder.app.data;

import android.content.Context;
import android.content.SharedPreferences;

/** Persists the dev-auth session in SharedPreferences and mirrors it into SessionHolder. */
public class SessionStore {

    private final SharedPreferences prefs;

    public SessionStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    /** Loads any saved session into SessionHolder; returns it (or null). */
    public Session load() {
        String uid = prefs.getString("uid", null);
        if (uid == null) return null;
        Session session = new Session(
                uid,
                prefs.getString("name", uid),
                prefs.getString("role", "STUDENT"));
        SessionHolder.current = session;
        return session;
    }

    public void save(Session session) {
        prefs.edit()
                .putString("uid", session.uid)
                .putString("name", session.displayName)
                .putString("role", session.role)
                .apply();
        SessionHolder.current = session;
    }

    public void clear() {
        prefs.edit().clear().apply();
        SessionHolder.current = null;
    }
}
