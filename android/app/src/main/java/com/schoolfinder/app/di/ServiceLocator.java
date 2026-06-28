package com.schoolfinder.app.di;

import android.content.Context;

import com.schoolfinder.app.BuildConfig;
import com.schoolfinder.app.data.SchoolRepository;
import com.schoolfinder.app.data.SessionStore;
import com.schoolfinder.app.data.remote.Network;

/** Tiny manual DI container — avoids pulling in Hilt for a small app. */
public final class ServiceLocator {

    private ServiceLocator() {}

    private static SchoolRepository repository;
    private static SessionStore sessionStore;

    public static void init(Context context) {
        if (repository != null) return;
        repository = new SchoolRepository(Network.create(BuildConfig.API_BASE_URL));
        sessionStore = new SessionStore(context.getApplicationContext());
    }

    public static SchoolRepository repository() {
        return repository;
    }

    public static SessionStore sessionStore() {
        return sessionStore;
    }
}
