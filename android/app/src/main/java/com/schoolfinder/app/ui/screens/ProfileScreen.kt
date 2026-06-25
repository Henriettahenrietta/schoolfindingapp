package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolfinder.app.data.Session
import com.schoolfinder.app.data.SchoolRepository
import com.schoolfinder.app.data.remote.Review
import com.schoolfinder.app.di.ServiceLocator
import com.schoolfinder.app.ui.RatingStars
import com.schoolfinder.app.ui.VMFactory
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: SchoolRepository) : ViewModel() {
    var reviews by mutableStateOf<List<Review>>(emptyList()); private set

    init {
        viewModelScope.launch { repo.myReviews().onSuccess { reviews = it } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(session: Session, onLogout: () -> Unit) {
    val vm: ProfileViewModel = viewModel(factory = VMFactory { ProfileViewModel(ServiceLocator.repository) })

    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(session.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(if (session.isAdmin) "Administrator" else "Student", style = MaterialTheme.typography.bodyMedium)
                    Text("ID: ${session.uid}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (session.isAdmin) {
                Spacer(Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Admin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Admin management (schools, users, review moderation, analytics) is exposed by the " +
                                "backend under /api/v1/admin. A full admin console is on the roadmap.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("My reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            if (vm.reviews.isEmpty()) {
                Text("You haven't reviewed any schools yet.", style = MaterialTheme.typography.bodySmall)
            } else {
                vm.reviews.forEach { r ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(r.schoolName ?: "School #${r.schoolId}", fontWeight = FontWeight.SemiBold)
                            RatingStars(r.rating.toDouble())
                            if (!r.comment.isNullOrBlank()) Text(r.comment, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Sign out") }
            Spacer(Modifier.height(24.dp))
        }
    }
}
