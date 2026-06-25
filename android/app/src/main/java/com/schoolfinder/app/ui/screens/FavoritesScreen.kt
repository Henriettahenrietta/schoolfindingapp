package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolfinder.app.data.SchoolRepository
import com.schoolfinder.app.data.remote.SchoolSummary
import com.schoolfinder.app.di.ServiceLocator
import com.schoolfinder.app.ui.LoadingBox
import com.schoolfinder.app.ui.MessageBox
import com.schoolfinder.app.ui.SchoolCard
import com.schoolfinder.app.ui.VMFactory
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repo: SchoolRepository) : ViewModel() {
    var loading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set
    var schools by mutableStateOf<List<SchoolSummary>>(emptyList()); private set

    init { load() }

    fun load() {
        loading = true
        viewModelScope.launch {
            repo.favorites()
                .onSuccess { schools = it; error = null }
                .onFailure { error = it.message ?: "Failed to load favourites" }
            loading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onOpenSchool: (Long) -> Unit) {
    val vm: FavoritesViewModel = viewModel(factory = VMFactory { FavoritesViewModel(ServiceLocator.repository) })

    Scaffold(topBar = { TopAppBar(title = { Text("Favourites") }) }) { padding ->
        when {
            vm.loading -> LoadingBox(Modifier.padding(padding))
            vm.error != null -> MessageBox("${vm.error}", Modifier.padding(padding))
            vm.schools.isEmpty() -> MessageBox("No favourites yet. Tap the heart on a school to save it.", Modifier.padding(padding))
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(vm.schools, key = { it.id }) { school ->
                    SchoolCard(school = school, onClick = { onOpenSchool(school.id) })
                }
            }
        }
    }
}
