package com.schoolfinder.app.di

import android.content.Context
import com.schoolfinder.app.BuildConfig
import com.schoolfinder.app.data.SchoolRepository
import com.schoolfinder.app.data.SessionStore
import com.schoolfinder.app.data.remote.Network

/** Tiny manual DI container — avoids pulling in Hilt for a small app. */
object ServiceLocator {

    lateinit var repository: SchoolRepository
        private set

    lateinit var sessionStore: SessionStore
        private set

    fun init(context: Context) {
        if (::repository.isInitialized) return
        val api = Network.create(BuildConfig.API_BASE_URL)
        repository = SchoolRepository(api)
        sessionStore = SessionStore(context.applicationContext)
    }
}
