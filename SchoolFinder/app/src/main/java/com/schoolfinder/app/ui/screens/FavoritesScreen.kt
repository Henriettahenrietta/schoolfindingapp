package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.ui.LocalRepository
import com.schoolfinder.app.ui.LocalSession
import com.schoolfinder.app.ui.components.SchoolCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onOpenSchool: (Int) -> Unit) {
    val repo = LocalRepository.current
    val session = LocalSession.current
    val scope = rememberCoroutineScope()

    val currentSession by session.current.collectAsState()
    val isGuest = currentSession?.isGuest ?: true
    val userEmail = currentSession?.email ?: "guest"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favorites") }) }
    ) { padding ->
        if (isGuest) {
            EmptyState(
                padding = padding,
                message = "Log in as a student to save and view your favorite schools."
            )
            return@Scaffold
        }

        val schools by repo.schools.collectAsState(initial = emptyList())
        val favoriteIds by repo.favoriteIds(userEmail).collectAsState(initial = emptyList())
        val favoriteSchools = schools.filter { favoriteIds.contains(it.id) }

        if (favoriteSchools.isEmpty()) {
            EmptyState(
                padding = padding,
                message = "No favorites yet. Tap the heart on any school to save it here."
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(favoriteSchools, key = { it.id }) { school ->
                    SchoolCard(
                        school = school,
                        rating = school.baseRating,
                        isFavorite = true,
                        showFavorite = true,
                        onClick = { onOpenSchool(school.id) },
                        onToggleFavorite = {
                            scope.launch { repo.toggleFavorite(userEmail, school.id) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(padding: androidx.compose.foundation.layout.PaddingValues, message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.height(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
