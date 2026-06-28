package com.schoolfinder.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.schoolfinder.app.data.Session;
import com.schoolfinder.app.di.ServiceLocator;

import java.util.Locale;

/**
 * Dev sign-in. Identifies the user to the backend via X-Debug-* headers (no real Firebase needed).
 * Replace with a Firebase Auth UI when wiring real authentication — see android/README.md.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Already signed in? Go straight to the app.
        if (ServiceLocator.sessionStore().load() != null) {
            goMain();
            return;
        }

        setContentView(R.layout.activity_login);

        TextInputEditText nameInput = findViewById(R.id.input_name);
        Chip adminChip = findViewById(R.id.chip_admin);

        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            String typed = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
            String name = typed.isEmpty() ? "Guest" : typed;
            String uid = "dev-" + name.toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("^-+", "")
                    .replaceAll("-+$", "");
            signIn(uid, name, adminChip.isChecked());
        });

        findViewById(R.id.btn_demo_student).setOnClickListener(v -> signIn("student-1", "Ada N.", false));
        findViewById(R.id.btn_demo_admin).setOnClickListener(v -> signIn("admin-dev", "Platform Admin", true));
    }

    private void signIn(String uid, String displayName, boolean admin) {
        Session session = new Session(uid, displayName, admin ? "ADMIN" : "STUDENT");
        ServiceLocator.sessionStore().save(session);
        goMain();
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
