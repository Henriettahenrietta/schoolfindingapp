plugins {
    id("com.android.application")
}

android {
    namespace = "com.schoolfinder.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.schoolfinder.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Backend base URL baked into the APK.
        // Points at the LIVE deployment (the Node server on Render that serves /api/v1 and
        // the web UI). This is the same backend the web app uses, so the Android app loads
        // the same live data. For a backend running on your own PC use the emulator alias
        // "http://10.0.2.2:8080/", or "http://<your-pc-ip>:8080/" for a physical device on the same WiFi.
        buildConfigField("String", "API_BASE_URL", "\"https://unimatch-cameroon.onrender.com/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core:1.13.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.fragment:fragment:1.8.3")

    // Lifecycle (ViewModel + LiveData) — Java
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.6")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- Firebase Authentication (optional) ---
    // To switch from dev-auth to real Firebase Auth: add app/google-services.json,
    // apply the google-services plugin, and uncomment the lines below. See android/README.md.
    // implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    // implementation("com.google.firebase:firebase-auth")
}
