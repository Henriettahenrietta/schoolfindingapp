package com.schoolfinder.app.session

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Represents who is currently using the app. role can be STUDENT, ADMIN or GUEST. */
data class UserSession(
    val email: String,
    val name: String,
    val role: String
) {
    val isGuest: Boolean get() = role == "GUEST"
    val isAdmin: Boolean get() = role == "ADMIN"
    val isStudent: Boolean get() = role == "STUDENT"
}

class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("school_finder_session", Context.MODE_PRIVATE)

    private val _current = MutableStateFlow(load())
    val current: StateFlow<UserSession?> = _current.asStateFlow()

    private fun load(): UserSession? {
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val name = prefs.getString(KEY_NAME, "User") ?: "User"
        val role = prefs.getString(KEY_ROLE, "GUEST") ?: "GUEST"
        return UserSession(email, name, role)
    }

    fun signIn(session: UserSession) {
        prefs.edit()
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_NAME, session.name)
            .putString(KEY_ROLE, session.role)
            .apply()
        _current.value = session
    }

    fun continueAsGuest() {
        signIn(UserSession(email = "guest", name = "Guest", role = "GUEST"))
    }

    fun signOut() {
        prefs.edit().clear().apply()
        _current.value = null
    }

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_ROLE = "role"
    }
}
