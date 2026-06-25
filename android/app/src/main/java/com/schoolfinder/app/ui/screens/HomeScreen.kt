package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolfinder.app.data.SchoolRepository
import com.schoolfinder.app.data.remote.Meta
import com.schoolfinder.app.data.remote.SchoolSummary
import com.schoolfinder.app.di.ServiceLocator
import com.schoolfinder.app.ui.LoadingBox
import com.schoolfinder.app.ui.MessageBox
import com.schoolfinder.app.ui.SchoolCard
import com.schoolfinder.app.ui.VMFactory
import com.schoolfinder.app.ui.categoryLabel
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: SchoolRepository) : ViewModel() {
    var loading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set
    var schools by mutableStateOf<List<SchoolSummary>>(emptyList()); private set
    var meta by mutableStateOf<Meta?>(null); private set

    var query by mutableStateOf("")
    var category by mutableStateOf<String?>(null); private set

    val selected: SnapshotStateList<Long> = mutableListOf<Long>().toMutableStateList()

    init { refresh() }

    fun setCategory(c: String?) { category = c; refresh() }

    fun refresh() {
        loading = true
        error = null
        viewModelScope.launch {
            if (meta == null) repo.meta().onSuccess { meta = it }
            repo.search(
                q = query,
                category = category,
                city = null,
                minRating = null,
                maxTuition = null,
                sort = "name",
            ).onSuccess { schools = it; error = null }
                .onFailure { error = it.message ?: "Failed to load schools" }
            loading = false
        }
    }

    fun toggleCompare(id: Long) {
        if (selected.contains(id)) selected.remove(id)
        else if (selected.size < 4) selected.add(id)
    }
}

@Composable
fun HomeScreen(onOpenSchool: (Long) -> Unit, onCompare: (List<Long>) -> Unit) {
    val vm: HomeViewModel = viewModel(factory = VMFactory { HomeViewModel(ServiceLocator.repository) })

    Scaffold(
        floatingActionButton = {
            if (vm.selected.size >= 2) {
                ExtendedFloatingActionButton(
                    onClick = { onCompare(vm.selected.toList()) },
                    icon = { Icon(Icons.Filled.Check, null) },
                    text = { Text("Compare ${vm.selected.size}") },
                )
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = vm.query,
                onValueChange = { vm.query = it },
                label = { Text("Search schools") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            )

            val cats = vm.meta?.categories ?: emptyList()
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = vm.category == null,
                    onClick = { vm.setCategory(null) },
                    label = { Text("All") },
                )
                cats.forEach { c ->
                    FilterChip(
                        selected = vm.category == c,
                        onClick = { vm.setCategory(if (vm.category == c) null else c) },
                        label = { Text(categoryLabel(c)) },
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    vm.loading -> LoadingBox()
                    vm.error != null -> MessageBox("${vm.error}\n\nIs the backend running at the configured URL?")
                    vm.schools.isEmpty() -> MessageBox("No schools match your search.")
                    else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 4.dp)) {
                        items(vm.schools, key = { it.id }) { school ->
                            SchoolCard(
                                school = school,
                                onClick = { onOpenSchool(school.id) },
                                trailing = {
                                    Checkbox(
                                        checked = vm.selected.contains(school.id),
                                        onCheckedChange = { vm.toggleCompare(school.id) },
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
