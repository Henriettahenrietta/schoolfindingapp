package com.schoolfinder.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.schoolfinder.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

/**
 * Initialises the Firebase Admin SDK only when `app.firebase.enabled=true`. When disabled the bean
 * is simply absent, so [FirebaseTokenFilter] receives a null [FirebaseApp] and falls back to dev
 * auth — the whole stack runs with placeholder configuration, no Firebase project required.
 */
@Configuration
class FirebaseConfig(private val props: AppProperties) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    @ConditionalOnProperty(name = ["app.firebase.enabled"], havingValue = "true")
    fun firebaseApp(): FirebaseApp {
        val path = props.firebase.credentialsPath
        require(path.isNotBlank()) { "app.firebase.enabled=true but no credentials-path was provided" }

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(path)))
            .build()

        return (if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getInstance()
        }).also { log.info("Firebase Admin SDK initialised") }
    }
}
