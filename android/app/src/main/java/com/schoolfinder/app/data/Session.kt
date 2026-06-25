package com.schoolfinder.app.data

/** The signed-in user. In dev-auth mode this is sent to the backend via X-Debug-* headers. */
data class Session(
    val uid: String,
    val displayName: String,
    val role: String, // "STUDENT" or "ADMIN"
) {
    val isAdmin: Boolean get() = role == "ADMIN"
}

/** Process-wide holder so the OkHttp interceptor can read the current session. */
object SessionHolder {
    @Volatile
    var current: Session? = null
}
