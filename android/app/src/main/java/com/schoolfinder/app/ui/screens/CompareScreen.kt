package com.schoolfinder.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.schoolfinder.app.data.SchoolRepository
import com.schoolfinder.app.data.remote.CompareResponse
import com.schoolfinder.app.data.remote.SchoolDetail
import com.schoolfinder.app.di.ServiceLocator
import com.schoolfinder.app.ui.LoadingBox
import com.schoolfinder.app.ui.MessageBox
import com.schoolfinder.app.ui.categoryLabel
import com.schoolfinder.app.ui.formatMoney
import kotlinx.coroutines.launch

class CompareViewModel(private val repo: SchoolRepository, private val ids: List<Long>) : ViewModel() {
    var loading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set
    var data by mutableStateOf<CompareResponse?>(null); private set

    init {
        viewModelScope.launch {
            repo.compare(ids)
                .onSuccess { data = it; error = null }
                .onFailure { error = it.message ?: "Failed to compare" }
            loading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(ids: List<Long>, onBack: () -> Unit) {
    val vm: CompareViewModel =
        viewModel(factory = com.schoolfinder.app.ui.VMFactory { CompareViewModel(ServiceLocator.repository, ids) })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        },
    ) { padding ->
        when {
            vm.loading -> LoadingBox(Modifier.padding(padding))
            vm.error != null -> MessageBox("${vm.error}", Modifier.padding(padding))
            vm.data != null -> {
                val d = vm.data!!
                Column(
                    modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState()).padding(12.dp),
                ) {
                    CompareRow("", d.schools.map { it.name }, header = true)
                    Divider()
                    CompareRow("Category", d.schools.map { categoryLabel(it.category) })
                    CompareRow("City", d.schools.map { it.city ?: "—" })
                    CompareRow(
                        "Tuition",
                        d.schools.map { formatMoney(it.tuitionFee, it.currency) },
                        highlightId = d.cheapestSchoolId,
                        ids = d.schools.map { it.id },
                    )
                    CompareRow(
                        "Rating",
                        d.schools.map { "%.1f (%d)".format(it.averageRating, it.ratingCount) },
                        highlightId = d.highestRatedSchoolId,
                        ids = d.schools.map { it.id },
                    )
                    CompareRow("Programs", d.schools.map { it.programs.size.toString() })
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Highlighted cells show the cheapest tuition and the highest rating.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareRow(
    label: String,
    values: List<String>,
    header: Boolean = false,
    highlightId: Long? = null,
    ids: List<Long> = emptyList(),
) {
    Row(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            label,
            modifier = Modifier.width(96.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        values.forEachIndexed { index, value ->
            val isBest = highlightId != null && ids.getOrNull(index) == highlightId
            Text(
                value,
                modifier = Modifier.width(140.dp).padding(horizontal = 4.dp),
                style = if (header) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                fontWeight = if (header || isBest) FontWeight.Bold else FontWeight.Normal,
                color = if (isBest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
