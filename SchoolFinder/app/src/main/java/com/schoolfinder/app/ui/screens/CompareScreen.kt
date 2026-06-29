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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.data.SchoolEntity
import com.schoolfinder.app.ui.LocalRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(onOpenSchool: (Int) -> Unit) {
    val repo = LocalRepository.current
    val schools by repo.schools.collectAsState(initial = emptyList())

    var schoolAId by remember { mutableStateOf<Int?>(null) }
    var schoolBId by remember { mutableStateOf<Int?>(null) }

    val schoolA = schools.firstOrNull { it.id == schoolAId }
    val schoolB = schools.firstOrNull { it.id == schoolBId }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compare Schools") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Pick two schools to compare them side by side.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SchoolPicker(
                    label = "School A",
                    schools = schools,
                    selected = schoolA,
                    onSelect = { schoolAId = it },
                    modifier = Modifier.weight(1f)
                )
                SchoolPicker(
                    label = "School B",
                    schools = schools,
                    selected = schoolB,
                    onSelect = { schoolBId = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (schoolA == null || schoolB == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Select both schools to see the comparison.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                ComparisonTable(schoolA, schoolB)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { onOpenSchool(schoolA.id) }, modifier = Modifier.weight(1f)) {
                        Text("Open A", maxLines = 1)
                    }
                    OutlinedButton(onClick = { onOpenSchool(schoolB.id) }, modifier = Modifier.weight(1f)) {
                        Text("Open B", maxLines = 1)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolPicker(
    label: String,
    schools: List<SchoolEntity>,
    selected: SchoolEntity?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = selected?.name ?: "Choose…",
                maxLines = 1,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            schools.forEach { s ->
                DropdownMenuItem(
                    text = { Text(s.name) },
                    onClick = {
                        onSelect(s.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ComparisonTable(a: SchoolEntity, b: SchoolEntity) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            CompareRow("", a.name, b.name, header = true)
            HorizontalDivider(Modifier.padding(vertical = 6.dp))
            CompareRow("Category", a.category, b.category)
            CompareRow("Location", a.location, b.location)
            CompareRow("Tuition / yr", "\$${a.tuition}", "\$${b.tuition}", highlightCheaper = a.tuition to b.tuition)
            CompareRow("Rating", String.format("%.1f", a.baseRating), String.format("%.1f", b.baseRating), highlightHigher = a.baseRating to b.baseRating)
            CompareRow("Programs", a.programs.size.toString(), b.programs.size.toString())
            CompareRow("Facilities", a.facilities.size.toString(), b.facilities.size.toString())
        }
    }
}

@Composable
private fun CompareRow(
    label: String,
    valueA: String,
    valueB: String,
    header: Boolean = false,
    highlightCheaper: Pair<Int, Int>? = null,
    highlightHigher: Pair<Double, Double>? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    val normal = MaterialTheme.colorScheme.onSurface

    val colorA = when {
        highlightCheaper != null && highlightCheaper.first <= highlightCheaper.second -> primary
        highlightHigher != null && highlightHigher.first >= highlightHigher.second -> primary
        else -> normal
    }
    val colorB = when {
        highlightCheaper != null && highlightCheaper.second < highlightCheaper.first -> primary
        highlightHigher != null && highlightHigher.second > highlightHigher.first -> primary
        else -> normal
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.9f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valueA,
            modifier = Modifier.weight(1f),
            style = if (header) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
            color = if (header) normal else colorA,
            textAlign = TextAlign.Center
        )
        Text(
            text = valueB,
            modifier = Modifier.weight(1f),
            style = if (header) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
            color = if (header) normal else colorB,
            textAlign = TextAlign.Center
        )
    }
}
