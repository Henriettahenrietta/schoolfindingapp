package com.schoolfinder.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.schoolfinder.app.data.remote.Program
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolfinder.app.data.SchoolRepository
import com.schoolfinder.app.data.remote.Review
import com.schoolfinder.app.data.remote.SchoolDetail
import com.schoolfinder.app.di.ServiceLocator
import com.schoolfinder.app.ui.LoadingBox
import com.schoolfinder.app.ui.MessageBox
import com.schoolfinder.app.ui.RatingStars
import com.schoolfinder.app.ui.VMFactory
import com.schoolfinder.app.ui.categoryLabel
import com.schoolfinder.app.ui.formatMoney
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: SchoolRepository, private val schoolId: Long) : ViewModel() {
    var loading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set
    var school by mutableStateOf<SchoolDetail?>(null); private set
    var reviews by mutableStateOf<List<Review>>(emptyList()); private set
    var favorite by mutableStateOf(false); private set
    var message by mutableStateOf<String?>(null)

    init { load() }

    fun load() {
        loading = true
        viewModelScope.launch {
            repo.school(schoolId)
                .onSuccess { school = it; favorite = it.favorite; error = null }
                .onFailure { error = it.message ?: "Failed to load school" }
            repo.reviews(schoolId).onSuccess { reviews = it }
            loading = false
        }
    }

    fun toggleFavorite() {
        val wasFav = favorite
        favorite = !wasFav
        viewModelScope.launch {
            val result = if (wasFav) repo.removeFavorite(schoolId) else repo.addFavorite(schoolId)
            result.onFailure { favorite = wasFav; message = it.message }
        }
    }

    fun submitReview(rating: Int, comment: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.submitReview(schoolId, rating, comment)
                .onSuccess { message = "Review submitted"; load(); onDone() }
                .onFailure { message = it.message ?: "Could not submit review" }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(schoolId: Long, onBack: () -> Unit) {
    val vm: DetailViewModel =
        viewModel(factory = VMFactory { DetailViewModel(ServiceLocator.repository, schoolId) })
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vm.school?.name ?: "School") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { vm.toggleFavorite() }) {
                        Icon(
                            if (vm.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favourite",
                        )
                    }
                },
            )
        },
    ) { padding ->
        when {
            vm.loading -> LoadingBox(Modifier.padding(padding))
            vm.error != null -> MessageBox("${vm.error}", Modifier.padding(padding))
            vm.school != null -> {
                val s = vm.school!!
                Column(
                    modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        AssistChip(onClick = {}, label = { Text(categoryLabel(s.category)) })
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    RatingStars(s.averageRating, s.ratingCount, starSize = 20)
                    Spacer(Modifier.height(8.dp))
                    Text(formatMoney(s.tuitionFee, s.currency), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    listOfNotNull(s.address, s.city, s.region).joinToString(", ").takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (!s.description.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(s.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    if (!s.history.isNullOrBlank()) {
                        SectionTitle("History")
                        Text(s.history, style = MaterialTheme.typography.bodyMedium)
                    }

                    if (s.latitude != null && s.longitude != null) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = {
                            val uri = Uri.parse("geo:${s.latitude},${s.longitude}?q=${s.latitude},${s.longitude}(${Uri.encode(s.name)})")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }) {
                            Icon(Icons.Filled.Place, null)
                            Text("  Navigate")
                        }
                    }

                    if (s.programs.isNotEmpty()) {
                        SectionTitle("Programmes")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp),
                        ) {
                            items(s.programs) { p -> ProgramSlide(p, s.currency) }
                        }
                    }

                    SectionTitle("Reviews")
                    ReviewComposer(onSubmit = { rating, comment -> vm.submitReview(rating, comment) {} })
                    vm.message?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.height(8.dp))
                    if (vm.reviews.isEmpty()) {
                        Text("No reviews yet. Be the first!", style = MaterialTheme.typography.bodySmall)
                    } else {
                        vm.reviews.forEach { r ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(Modifier.padding(12.dp)) {
                                    RatingStars(r.rating.toDouble())
                                    Text(r.userDisplayName ?: "Anonymous", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                    if (!r.comment.isNullOrBlank()) Text(r.comment, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
}

private data class Field(val icon: String, val colors: List<Color>)

/** Icon and Cameroon-themed gradient for a programme, drawn locally (no network images). */
private fun fieldOf(text: String): Field {
    val t = text.lowercase()
    return when {
        Regex("software|comput|network|cloud|database|cyber|web|e-?commerce|digital|systems|graphics|information tech").containsMatchIn(t) -> Field("💻", listOf(Color(0xFF0D6E6E), Color(0xFF09504F)))
        Regex("nurs|midwif|medical|pharmac|health|physio|medicine|biomed|laborator").containsMatchIn(t) -> Field("🩺", listOf(Color(0xFFC0392B), Color(0xFF7B241C)))
        Regex("law|magistr|legal").containsMatchIn(t) -> Field("⚖️", listOf(Color(0xFF34495E), Color(0xFF2C3E50)))
        Regex("account|bank|financ|econom|market|business|management|administration|bba|mba|commerce|logistic|transport|shipping|project|human resource").containsMatchIn(t) -> Field("📊", listOf(Color(0xFF1F6F8B), Color(0xFF16505F)))
        Regex("engineer|civil|electric|telecom|mechanic").containsMatchIn(t) -> Field("🛠️", listOf(Color(0xFFE67E22), Color(0xFFA85B11)))
        Regex("journal|communicat|advertis|public relation|media").containsMatchIn(t) -> Field("🎙️", listOf(Color(0xFF8E44AD), Color(0xFF5E2D73)))
        Regex("tourism|hotel|travel|catering|hospitality").containsMatchIn(t) -> Field("🏨", listOf(Color(0xFF16A085), Color(0xFF0E6655)))
        Regex("bakery|food").containsMatchIn(t) -> Field("🍞", listOf(Color(0xFFD35400), Color(0xFFA04000)))
        Regex("beauty|cosmetic|esthetic|hairdress").containsMatchIn(t) -> Field("💄", listOf(Color(0xFFD81B60), Color(0xFF880E4F)))
        Regex("fashion|clothing|design").containsMatchIn(t) -> Field("👗", listOf(Color(0xFF6D4C41), Color(0xFF4E342E)))
        Regex("theolog|religio").containsMatchIn(t) -> Field("⛪", listOf(Color(0xFF5D4037), Color(0xFF3E2723)))
        Regex("statistic|demograph|mathematic|physic|data|science").containsMatchIn(t) -> Field("🔬", listOf(Color(0xFF2980B9), Color(0xFF1C5980)))
        Regex("english|letters|arts|language").containsMatchIn(t) -> Field("📚", listOf(Color(0xFF7F8C8D), Color(0xFF5D6D6E)))
        Regex("political|international relation|public administr|customs|treasury|governance").containsMatchIn(t) -> Field("🏛️", listOf(Color(0xFF596275), Color(0xFF3D4453)))
        else -> Field("🎓", listOf(Color(0xFF0D6E6E), Color(0xFF09504F)))
    }
}

@Composable
private fun ProgramSlide(p: Program, currency: String) {
    val f = fieldOf(p.name + " " + (p.faculty ?: ""))
    Card(modifier = Modifier.width(240.dp)) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Brush.linearGradient(f.colors)),
                contentAlignment = Alignment.Center,
            ) {
                Text(f.icon, fontSize = 56.sp)
            }
            Column(Modifier.padding(12.dp)) {
                p.faculty?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                Text(p.name, fontWeight = FontWeight.SemiBold)
                Text(
                    listOfNotNull(
                        p.level,
                        p.durationMonths?.let { "$it months" },
                        p.tuitionFee?.let { formatMoney(it, currency) },
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ReviewComposer(onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Rate this school", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star }) {
                        Icon(
                            if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "$star",
                        )
                    }
                }
            }
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Your review (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onSubmit(rating, comment) }, modifier = Modifier.fillMaxWidth()) {
                Text("Submit review")
            }
        }
    }
}
