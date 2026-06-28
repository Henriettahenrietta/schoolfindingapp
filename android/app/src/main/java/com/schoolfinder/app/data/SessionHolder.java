package com.schoolfinder.app.data;

/** Process-wide holder so the OkHttp interceptor can read the current session. */
public final class SessionHolder {
    private SessionHolder() {}

    public static volatile Session current;
}
