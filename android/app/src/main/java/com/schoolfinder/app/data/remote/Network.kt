package com.schoolfinder.app.data.remote

import com.schoolfinder.app.data.SessionHolder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Network {

    /**
     * Dev-auth interceptor: attaches the current session as X-Debug-* headers, which the backend
     * trusts when `app.firebase.enabled=false`. To use real Firebase Auth, replace this with an
     * interceptor that adds `Authorization: Bearer <firebase-id-token>`.
     */
    private val authInterceptor = Interceptor { chain ->
        val session = SessionHolder.current
        val request = if (session != null) {
            chain.request().newBuilder()
                .header("X-Debug-Uid", session.uid)
                .header("X-Debug-Name", session.displayName)
                .header("X-Debug-Role", session.role)
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    fun create(baseUrl: String): ApiService {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}
