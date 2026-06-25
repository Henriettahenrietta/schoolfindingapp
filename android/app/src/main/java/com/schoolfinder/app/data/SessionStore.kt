package com.schoolfinder.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "session")

/** Persists the dev-auth session and mirrors it into [SessionHolder] for the network layer. */
class SessionStore(private val context: Context) {

    private val uidKey = stringPreferencesKey("uid")
    private val nameKey = stringPreferencesKey("name")
    private val roleKey = stringPreferencesKey("role")

    /** Loads any saved session into [SessionHolder]; returns it (or null). */
    suspend fun load(): Session? {
        val prefs = context.dataStore.data.first()
        val uid = prefs[uidKey] ?: return null
        val session = Session(
            uid = uid,
            displayName = prefs[nameKey] ?: uid,
            role = prefs[roleKey] ?: "STUDENT",
        )
        SessionHolder.current = session
        return session
    }

    suspend fun save(session: Session) {
        context.dataStore.edit {
            it[uidKey] = session.uid
            it[nameKey] = session.displayName
            it[roleKey] = session.role
        }
        SessionHolder.current = session
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        SessionHolder.current = null
    }
}
