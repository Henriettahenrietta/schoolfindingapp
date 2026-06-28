package com.schoolfinder.app.data.remote;

import com.schoolfinder.app.data.Session;
import com.schoolfinder.app.data.SessionHolder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Builds the Retrofit ApiService with dev-auth headers. */
public final class Network {

    private Network() {}

    /**
     * Dev-auth interceptor: attaches the current session as X-Debug-* headers, which the backend
     * trusts when app.firebase.enabled=false. To use real Firebase Auth, replace this with an
     * interceptor that adds "Authorization: Bearer <firebase-id-token>".
     */
    private static final Interceptor AUTH = chain -> {
        Session session = SessionHolder.current;
        Request request = chain.request();
        if (session != null) {
            request = request.newBuilder()
                    .header("X-Debug-Uid", session.uid)
                    .header("X-Debug-Name", session.displayName)
                    .header("X-Debug-Role", session.role)
                    .build();
        }
        return chain.proceed(request);
    };

    public static ApiService create(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(AUTH)
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}
