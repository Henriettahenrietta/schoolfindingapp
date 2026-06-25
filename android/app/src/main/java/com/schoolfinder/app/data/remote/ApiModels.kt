package com.schoolfinder.app.data.remote

// Parsed reflectively by Moshi (KotlinJsonAdapterFactory) — no codegen needed.

data class Meta(
    val country: String,
    val currency: String,
    val mapCenterLat: Double,
    val mapCenterLng: Double,
    val categories: List<String>,
    val firebaseEnabled: Boolean,
)

data class SchoolSummary(
    val id: Long,
    val name: String,
    val category: String,
    val city: String?,
    val region: String?,
    val tuitionFee: Double?,
    val currency: String,
    val coverImageUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val averageRating: Double,
    val ratingCount: Long,
)

data class Program(
    val id: Long,
    val name: String,
    val level: String?,
    val durationMonths: Int?,
    val tuitionFee: Double?,
)

data class SchoolImage(val id: Long, val url: String, val caption: String?)

data class SchoolDetail(
    val id: Long,
    val name: String,
    val category: String,
    val description: String?,
    val city: String?,
    val region: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val tuitionFee: Double?,
    val currency: String,
    val website: String?,
    val phone: String?,
    val email: String?,
    val coverImageUrl: String?,
    val averageRating: Double,
    val ratingCount: Long,
    val favorite: Boolean,
    val programs: List<Program>,
    val images: List<SchoolImage>,
)

data class Review(
    val id: Long,
    val schoolId: Long,
    val schoolName: String?,
    val userDisplayName: String?,
    val rating: Int,
    val comment: String?,
    val status: String,
    val createdAt: String,
)

data class CreateReviewRequest(val rating: Int, val comment: String?)

data class CompareResponse(
    val schools: List<SchoolDetail>,
    val cheapestSchoolId: Long?,
    val highestRatedSchoolId: Long?,
)

data class Me(
    val id: Long,
    val firebaseUid: String,
    val email: String?,
    val displayName: String?,
    val role: String,
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
