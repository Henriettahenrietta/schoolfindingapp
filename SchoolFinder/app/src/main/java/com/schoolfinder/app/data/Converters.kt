package com.schoolfinder.app.data

import androidx.room.TypeConverter

/** Converts List<String> (programs, facilities) to/from a single stored String. */
class Converters {

    @TypeConverter
    fun fromList(value: List<String>): String =
        value.joinToString(separator = "||")

    @TypeConverter
    fun toList(value: String): List<String> =
        if (value.isBlank()) emptyList()
        else value.split("||").map { it.trim() }.filter { it.isNotEmpty() }
}
