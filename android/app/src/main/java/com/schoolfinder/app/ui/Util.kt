package com.schoolfinder.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.text.NumberFormat
import java.util.Locale

/** Generic factory so screens can build their ViewModels with constructor dependencies. */
@Suppress("UNCHECKED_CAST")
class VMFactory<T : ViewModel>(private val create: () -> T) : ViewModelProvider.Factory {
    override fun <X : ViewModel> create(modelClass: Class<X>): X = create() as X
}

/** "50 000 XAF" style formatting. */
fun formatMoney(amount: Double?, currency: String): String {
    if (amount == null) return "—"
    val nf = NumberFormat.getNumberInstance(Locale.FRANCE) // space thousands separator, fits XAF
    return "${nf.format(amount)} $currency"
}

fun categoryLabel(raw: String): String = when (raw) {
    "PRIMARY" -> "Primary"
    "SECONDARY" -> "Secondary"
    "HIGH_SCHOOL" -> "High School"
    "VOCATIONAL" -> "Vocational"
    "UNIVERSITY" -> "University"
    else -> raw.lowercase().replaceFirstChar { it.uppercase() }
}
