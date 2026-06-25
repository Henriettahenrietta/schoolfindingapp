package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.data.Session
import com.schoolfinder.app.di.ServiceLocator
import kotlinx.coroutines.launch

/**
 * Dev sign-in. Identifies the user to the backend via X-Debug-* headers (no real Firebase needed).
 * Replace with a Firebase Auth UI when wiring real authentication — see android/README.md.
 */
@Composable
fun LoginScreen(onLoggedIn: (Session) -> Unit) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    fun signIn(uid: String, displayName: String, admin: Boolean) {
        val session = Session(uid = uid, displayName = displayName, role = if (admin) "ADMIN" else "STUDENT")
        scope.launch {
            ServiceLocator.sessionStore.save(session)
            onLoggedIn(session)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("School Finder", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Discover, compare and review schools in Cameroon",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !isAdmin, onClick = { isAdmin = false }, label = { Text("Student") })
            FilterChip(selected = isAdmin, onClick = { isAdmin = true }, label = { Text("Administrator") })
        }
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val trimmed = name.trim().ifBlank { "Guest" }
                val uid = "dev-" + trimmed.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
                signIn(uid, trimmed, isAdmin)
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Continue") }

        Spacer(Modifier.height(28.dp))
        Text("Quick demo accounts", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { signIn("student-1", "Ada N.", false) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Sign in as demo student") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { signIn("admin-dev", "Platform Admin", true) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Sign in as demo admin") }
    }
}
