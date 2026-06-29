package com.schoolfinder.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolfinder.app.data.ReviewEntity
import com.schoolfinder.app.data.SchoolEntity
import com.schoolfinder.app.ui.LocalRepository
import com.schoolfinder.app.ui.LocalSession
import com.schoolfinder.app.ui.components.PlaceholderImage
import com.schoolfinder.app.ui.components.RatingStars
import com.schoolfinder.app.ui.components.SectionHeader
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SchoolDetailScreen(schoolId: Int, onBack: () -> Unit) {
    val repo = LocalRepository.current
    val session = LocalSession.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentSession by session.current.collectAsState()
    val isGuest = currentSession?.isGuest ?: true
    val userEmail = currentSession?.email ?: "guest"
    val userName = currentSession?.name ?: "Guest"

    var school by remember { mutableStateOf<SchoolEntity?>(null) }
    androidx.compose.runtime.LaunchedEffect(schoolId) {
        school = repo.getSchool(schoolId)
    }

    val reviews by repo.reviewsForSchool(schoolId).collectAsState(initial = emptyList())
    val favoriteIds by repo.favoriteIds(userEmail).collectAsState(initial = emptyList())
    val isFavorite = favoriteIds.contains(schoolId)

    // combined rating = baseline blended with user reviews
    val combinedRating = remember(school, reviews) {
        val s = school
        if (s == null) 0.0
        else if (reviews.isEmpty()) s.baseRating
        else {
            val reviewAvg = reviews.map { it.rating }.average()
            (s.baseRating + reviewAvg) / 2.0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isGuest) {
                        IconButton(onClick = {
                            scope.launch { repo.toggleFavorite(userEmail, schoolId) }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Toggle favorite",
                                tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val s = school
        if (s == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading…", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PlaceholderImage(
                name = s.name,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                fontSize = 56
            )
            Spacer(Modifier.height(14.dp))
            Text(s.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(s.category, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            RatingStars(rating = combinedRating, starSize = 20)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text(s.address, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tuition: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("\$${s.tuition} / year", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { openInMaps(context, s) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Map, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Open in Maps")
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { dialPhone(context, s.phone) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Call")
                }
                OutlinedButton(
                    onClick = { openWebsite(context, s.website) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Website")
                }
            }

            Spacer(Modifier.height(18.dp))
            SectionHeader("About")
            Text(s.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))
            SectionHeader("Programs")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                s.programs.forEach { p ->
                    AssistChip(onClick = {}, label = { Text(p) })
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionHeader("Facilities")
            s.facilities.forEach { f ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Box(
                        modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(f, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            SectionHeader("Reviews (${reviews.size})")

            if (isGuest) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        "Log in as a student to write a review.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                AddReviewSection(
                    onSubmit = { rating, comment ->
                        scope.launch {
                            repo.addReview(
                                ReviewEntity(
                                    schoolId = schoolId,
                                    userName = userName,
                                    rating = rating,
                                    comment = comment
                                )
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
            if (reviews.isEmpty()) {
                Text(
                    "No reviews yet. Be the first to review!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                reviews.forEach { review ->
                    ReviewCard(review)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddReviewSection(onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Write a review", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rate $i",
                            tint = if (i <= rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Your comment") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(Modifier.height(8.dp))
            FilledTonalButton(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSubmit(rating, comment.trim())
                        comment = ""
                        rating = 5
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit review")
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewEntity) {
    val dateFmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(review.userName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(dateFmt.format(Date(review.timestamp)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            RatingStars(rating = review.rating.toDouble(), starSize = 14, showValue = false)
            Spacer(Modifier.height(6.dp))
            Text(review.comment, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun openInMaps(context: android.content.Context, s: SchoolEntity) {
    val label = Uri.encode(s.name)
    val geoUri = Uri.parse("geo:${s.latitude},${s.longitude}?q=${s.latitude},${s.longitude}($label)")
    val intent = Intent(Intent.ACTION_VIEW, geoUri)
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val web = Uri.parse("https://www.google.com/maps/search/?api=1&query=${s.latitude},${s.longitude}")
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, web))
        } catch (_: ActivityNotFoundException) {
            // no handler available; ignore silently
        }
    }
}

private fun dialPhone(context: android.content.Context, phone: String) {
    if (phone.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
    }
}

private fun openWebsite(context: android.content.Context, url: String) {
    if (url.isBlank()) return
    val normalized = if (url.startsWith("http")) url else "https://$url"
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
    } catch (_: ActivityNotFoundException) {
    }
}
