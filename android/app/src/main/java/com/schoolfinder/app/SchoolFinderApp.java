package com.schoolfinder.app;

import android.app.Application;

import com.schoolfinder.app.di.ServiceLocator;

public class SchoolFinderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLocator.init(this);
    }
}
