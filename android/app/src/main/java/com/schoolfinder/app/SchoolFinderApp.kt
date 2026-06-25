package com.schoolfinder.app

import android.app.Application
import com.schoolfinder.app.di.ServiceLocator

class SchoolFinderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
