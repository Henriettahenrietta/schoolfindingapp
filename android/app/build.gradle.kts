plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        // Render deployment (render.yaml service "schoolfinder-api"). If Render assigns a
        // different hostname, update this and push to rebuild. For the emulator use
        // "http://10.0.2.2:8080/"; for same-WiFi testing use "http://<your-pc-ip>:8080/".
        buildConfigField("String", "API_BASE_URL", "\"https://schoolfinder-api.onrender.com/\"")
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Local session storage
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Firebase Authentication (optional) ---
    // To switch from dev-auth to real Firebase Auth: add app/google-services.json,
    // apply the google-services plugin, and uncomment the lines below. See android/README.md.
    // implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    // implementation("com.google.firebase:firebase-auth-ktx")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
