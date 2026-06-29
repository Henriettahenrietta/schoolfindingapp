package com.schoolfinder.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * In the SRS the backend uses PostgreSQL. For a self-contained app that runs
 * immediately in Android Studio (no server/keys required), the same data model
 * is represented locally with Room/SQLite. The schema mirrors the SRS tables:
 * users, schools, reviews and favorites.
 */

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val location: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val tuition: Int,                 // annual tuition in USD
    val programs: List<String>,
    val facilities: List<String>,
    val description: String,
    val baseRating: Double,           // editorial/baseline rating before user reviews
    val website: String,
    val phone: String
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,             // demo only: stored in plain text locally
    val role: String                  // "STUDENT" or "ADMIN"
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val schoolId: Int,
    val userName: String,
    val rating: Int,                  // 1..5
    val comment: String,
    val approved: Boolean = true,     // admins can moderate (approve/hide/delete)
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "favorites",
    indices = [Index(value = ["userEmail", "schoolId"], unique = true)]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val schoolId: Int
)
