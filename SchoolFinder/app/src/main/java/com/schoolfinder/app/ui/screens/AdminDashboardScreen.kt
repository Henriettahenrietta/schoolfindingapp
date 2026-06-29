package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.ui.LocalRepository
import com.schoolfinder.app.ui.LocalSession
import com.schoolfinder.app.ui.components.RatingStars
import com.schoolfinder.app.ui.components.SectionHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onBack: () -> Unit, onManageSchools: () -> Unit) {
    val repo = LocalRepository.current
    val session = LocalSession.current
    val scope = rememberCoroutineScope()

    val currentSession by session.current.collectAsState()
    val isAdmin = currentSession?.isAdmin ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (!isAdmin) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Access denied. Administrator privileges are required.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            return@Scaffold
        }

        val schools by repo.schools.collectAsState(initial = emptyList())
        val users by repo.users.collectAsState(initial = emptyList())
        val allReviews by repo.allReviews.collectAsState(initial = emptyList())

        val avgRating = if (allReviews.isEmpty()) 0.0 else allReviews.map { it.rating }.average()
        val schoolNames = remember(schools) { schools.associate { it.id to it.name } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader("Overview")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Schools", schools.size.toString(), Modifier.weight(1f))
                StatCard("Students", users.size.toString(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Reviews", allReviews.size.toString(), Modifier.weight(1f))
                StatCard("Avg rating", String.format("%.1f", avgRating), Modifier.weight(1f))
            }

            Spacer(Modifier.height(18.dp))
            Button(onClick = onManageSchools, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.School, contentDescription = null)
                Text("  Manage Schools")
            }

            Spacer(Modifier.height(20.dp))
            SectionHeader("Moderate Reviews (${allReviews.size})")
            if (allReviews.isEmpty()) {
                Text(
                    "No reviews to moderate yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                allReviews.forEach { review ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                schoolNames[review.schoolId] ?: "School #${review.schoolId}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "by ${review.userName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            RatingStars(rating = review.rating.toDouble(), starSize = 14, showValue = false)
                            Spacer(Modifier.height(6.dp))
                            Text(review.comment, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = review.approved,
                                    onClick = {
                                        scope.launch { repo.updateReview(review.copy(approved = !review.approved)) }
                                    },
                                    label = { Text(if (review.approved) "Approved" else "Hidden") }
                                )
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = {
                                    scope.launch { repo.deleteReview(review) }
                                }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete review",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
