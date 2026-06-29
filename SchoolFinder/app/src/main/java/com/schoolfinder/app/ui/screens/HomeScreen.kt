package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.ui.LocalRepository
import com.schoolfinder.app.ui.LocalSession
import com.schoolfinder.app.ui.components.SchoolCard
import kotlinx.coroutines.launch

private val sortOptions = listOf("Top rated", "Lowest tuition", "Highest tuition", "Name (A-Z)")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenSchool: (Int) -> Unit) {
    val repo = LocalRepository.current
    val session = LocalSession.current
    val scope = rememberCoroutineScope()

    val schools by repo.schools.collectAsState(initial = emptyList())
    val currentSession by session.current.collectAsState()
    val canFavorite = currentSession?.isGuest == false

    val favoriteIds by (
        if (canFavorite) repo.favoriteIds(currentSession!!.email)
        else repo.favoriteIds("__none__")
        ).collectAsState(initial = emptyList())

    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var sort by rememberSaveable { mutableStateOf(sortOptions.first()) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    val categories = remember(schools) {
        listOf("All") + schools.map { it.category }.distinct().sorted()
    }

    val filtered = remember(schools, query, category, sort) {
        schools
            .filter { s ->
                (category == "All" || s.category == category) &&
                    (query.isBlank() ||
                        s.name.contains(query, ignoreCase = true) ||
                        s.location.contains(query, ignoreCase = true) ||
                        s.programs.any { it.contains(query, ignoreCase = true) })
            }
            .let { list ->
                when (sort) {
                    "Lowest tuition" -> list.sortedBy { it.tuition }
                    "Highest tuition" -> list.sortedByDescending { it.tuition }
                    "Name (A-Z)" -> list.sortedBy { it.name }
                    else -> list.sortedByDescending { it.baseRating }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Finder") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search by name, city or program") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filtered.size} result${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box {
                    TextButton(onClick = { sortMenuOpen = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(sort)
                    }
                    DropdownMenu(
                        expanded = sortMenuOpen,
                        onDismissRequest = { sortMenuOpen = false }
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sort = option
                                    sortMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No schools match your search.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 6.dp)) {
                    items(filtered, key = { it.id }) { school ->
                        SchoolCard(
                            school = school,
                            rating = school.baseRating,
                            isFavorite = favoriteIds.contains(school.id),
                            showFavorite = canFavorite,
                            onClick = { onOpenSchool(school.id) },
                            onToggleFavorite = {
                                val email = currentSession?.email ?: return@SchoolCard
                                scope.launch { repo.toggleFavorite(email, school.id) }
                            }
                        )
                    }
                }
            }
        }
    }
}
