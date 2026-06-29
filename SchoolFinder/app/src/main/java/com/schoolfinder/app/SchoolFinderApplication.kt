package com.schoolfinder.app

import android.app.Application
import com.schoolfinder.app.data.AppDatabase
import com.schoolfinder.app.data.Repository
import com.schoolfinder.app.data.SeedData
import com.schoolfinder.app.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SchoolFinderApplication : Application() {

    lateinit var repository: Repository
        private set

    lateinit var session: SessionManager
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        val db = AppDatabase.getInstance(this)
        repository = Repository(
            schoolDao = db.schoolDao(),
            userDao = db.userDao(),
            reviewDao = db.reviewDao(),
            favoriteDao = db.favoriteDao()
        )
        session = SessionManager(this)

        seedIfEmpty(db)
    }

    private fun seedIfEmpty(db: AppDatabase) {
        appScope.launch {
            if (db.schoolDao().count() == 0) {
                db.schoolDao().insertAll(SeedData.schools)
                SeedData.sampleReviews.forEach { db.reviewDao().insert(it) }
            }
            if (db.userDao().count() == 0) {
                db.userDao().insert(SeedData.defaultAdmin)
            }
        }
    }
}
