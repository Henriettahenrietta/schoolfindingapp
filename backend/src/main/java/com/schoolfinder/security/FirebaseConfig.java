package com.schoolfinder.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.schoolfinder.config.AppProperties;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialises the Firebase Admin SDK only when {@code app.firebase.enabled=true}. When disabled the
 * bean is simply absent, so {@link FirebaseTokenFilter} receives a null {@link FirebaseApp} and
 * falls back to dev auth — the whole stack runs with placeholder configuration, no Firebase project
 * required.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private final AppProperties props;

    public FirebaseConfig(AppProperties props) {
        this.props = props;
    }

    @Bean
    @ConditionalOnProperty(name = "app.firebase.enabled", havingValue = "true")
    public FirebaseApp firebaseApp() throws Exception {
        String path = props.getFirebase().getCredentialsPath();
        if (path == null || path.isBlank()) {
            throw new IllegalStateException("app.firebase.enabled=true but no credentials-path was provided");
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(new FileInputStream(path)))
            .build();

        FirebaseApp app = FirebaseApp.getApps().isEmpty()
            ? FirebaseApp.initializeApp(options)
            : FirebaseApp.getInstance();
        log.info("Firebase Admin SDK initialised");
        return app;
    }
}
