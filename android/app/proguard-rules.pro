# Keep model classes used reflectively by Moshi
-keep class com.schoolfinder.app.data.remote.** { *; }

# Retrofit / OkHttp / Moshi
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Signature, *Annotation*
-keep class kotlin.Metadata { *; }
