package com.schoolfinder.app.data;

/** The signed-in user. In dev-auth mode this is sent to the backend via X-Debug-* headers. */
public class Session {
    public final String uid;
    public final String displayName;
    public final String role; // "STUDENT" or "ADMIN"

    public Session(String uid, String displayName, String role) {
        this.uid = uid;
        this.displayName = displayName;
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
