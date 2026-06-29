package com.schoolfinder.app.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.schoolfinder.app.data.Repository
import com.schoolfinder.app.session.SessionManager

val LocalRepository = staticCompositionLocalOf<Repository> {
    error("Repository not provided")
}

val LocalSession = staticCompositionLocalOf<SessionManager> {
    error("SessionManager not provided")
}
