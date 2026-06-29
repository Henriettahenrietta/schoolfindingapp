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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.data.SchoolEntity
import com.schoolfinder.app.ui.LocalRepository
import com.schoolfinder.app.ui.LocalSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSchoolsScreen(onBack: () -> Unit) {
    val repo = LocalRepository.current
    val session = LocalSession.current
    val scope = rememberCoroutineScope()

    val currentSession by session.current.collectAsState()
    val isAdmin = currentSession?.isAdmin ?: false

    val schools by repo.schools.collectAsState(initial = emptyList())

    // null = list view; SchoolEntity (id==0 => new) = editing
    var editing by remember { mutableStateOf<SchoolEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showForm) (if (editing?.id == 0) "Add School" else "Edit School") else "Manage Schools") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showForm) { showForm = false; editing = null } else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin && !showForm) {
                FloatingActionButton(onClick = {
                    editing = blankSchool()
                    showForm = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add school")
                }
            }
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

        if (showForm && editing != null) {
            SchoolForm(
                initial = editing!!,
                modifier = Modifier.padding(padding),
                onCancel = { showForm = false; editing = null },
                onSave = { school ->
                    scope.launch {
                        if (school.id == 0) repo.addSchool(school) else repo.updateSchool(school)
                    }
                    showForm = false
                    editing = null
                }
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(schools, key = { it.id }) { school ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(school.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${school.category} • \$${school.tuition}/yr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { editing = school; showForm = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { scope.launch { repo.deleteSchool(school) } }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun blankSchool() = SchoolEntity(
    id = 0, name = "", category = "", location = "", address = "",
    latitude = 0.0, longitude = 0.0, tuition = 0,
    programs = emptyList(), facilities = emptyList(),
    description = "", baseRating = 4.0, website = "", phone = ""
)

@Composable
private fun SchoolForm(
    initial: SchoolEntity,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSave: (SchoolEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var category by remember { mutableStateOf(initial.category) }
    var location by remember { mutableStateOf(initial.location) }
    var address by remember { mutableStateOf(initial.address) }
    var latitude by remember { mutableStateOf(if (initial.id == 0) "" else initial.latitude.toString()) }
    var longitude by remember { mutableStateOf(if (initial.id == 0) "" else initial.longitude.toString()) }
    var tuition by remember { mutableStateOf(if (initial.id == 0) "" else initial.tuition.toString()) }
    var programs by remember { mutableStateOf(initial.programs.joinToString(", ")) }
    var facilities by remember { mutableStateOf(initial.facilities.joinToString(", ")) }
    var description by remember { mutableStateOf(initial.description) }
    var baseRating by remember { mutableStateOf(initial.baseRating.toString()) }
    var website by remember { mutableStateOf(initial.website) }
    var phone by remember { mutableStateOf(initial.phone) }

    val nameValid = name.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        FormField("School name *", name) { name = it }
        FormField("Category (e.g. University, High School)", category) { category = it }
        FormField("Location (city / region)", location) { location = it }
        FormField("Full address", address) { address = it }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormField("Latitude", latitude, Modifier.weight(1f)) { latitude = it }
            FormField("Longitude", longitude, Modifier.weight(1f)) { longitude = it }
        }
        FormField("Tuition (USD / year)", tuition) { tuition = it }
        FormField("Programs (comma separated)", programs) { programs = it }
        FormField("Facilities (comma separated)", facilities) { facilities = it }
        FormField("Base rating (0-5)", baseRating) { baseRating = it }
        FormField("Website", website) { website = it }
        FormField("Phone", phone) { phone = it }
        FormField("Description", description, minLines = 3) { description = it }

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    onSave(
                        initial.copy(
                            name = name.trim(),
                            category = category.trim().ifBlank { "School" },
                            location = location.trim(),
                            address = address.trim(),
                            latitude = latitude.toDoubleOrNull() ?: 0.0,
                            longitude = longitude.toDoubleOrNull() ?: 0.0,
                            tuition = tuition.toIntOrNull() ?: 0,
                            programs = splitList(programs),
                            facilities = splitList(facilities),
                            description = description.trim(),
                            baseRating = (baseRating.toDoubleOrNull() ?: 4.0).coerceIn(0.0, 5.0),
                            website = website.trim(),
                            phone = phone.trim()
                        )
                    )
                },
                enabled = nameValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun splitList(raw: String): List<String> =
    raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }

@Composable
private fun FormField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        minLines = minLines,
        singleLine = minLines == 1
    )
}
