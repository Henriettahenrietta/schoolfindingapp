package com.schoolfinder.api

import com.schoolfinder.domain.ReviewStatus
import com.schoolfinder.domain.Role
import com.schoolfinder.domain.SchoolCategory
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.OffsetDateTime

// ---------- Schools ----------

data class SchoolSummary(
    val id: Long,
    val name: String,
    val category: SchoolCategory,
    val city: String?,
    val region: String?,
    val tuitionFee: BigDecimal?,
    val currency: String,
    val coverImageUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val averageRating: Double,
    val ratingCount: Long,
)

data class ProgramDto(
    val id: Long,
    val name: String,
    val faculty: String?,
    val level: String?,
    val durationMonths: Int?,
    val tuitionFee: BigDecimal?,
)

data class ImageDto(val id: Long, val url: String, val caption: String?)

data class SchoolDetail(
    val id: Long,
    val name: String,
    val category: SchoolCategory,
    val description: String?,
    val history: String?,
    val city: String?,
    val region: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val tuitionFee: BigDecimal?,
    val currency: String,
    val website: String?,
    val phone: String?,
    val email: String?,
    val coverImageUrl: String?,
    val averageRating: Double,
    val ratingCount: Long,
    val favorite: Boolean,
    val programs: List<ProgramDto>,
    val images: List<ImageDto>,
)

// ---------- Reviews ----------

data class CreateReviewRequest(
    @field:Min(1) @field:Max(5) val rating: Int,
    @field:Size(max = 4000) val comment: String? = null,
)

data class ReviewDto(
    val id: Long,
    val schoolId: Long,
    val schoolName: String?,
    val userDisplayName: String?,
    val rating: Int,
    val comment: String?,
    val status: ReviewStatus,
    val createdAt: OffsetDateTime,
)

// ---------- Admin: schools ----------

data class SchoolUpsertRequest(
    @field:NotBlank val name: String,
    val category: SchoolCategory,
    val description: String? = null,
    val history: String? = null,
    val city: String? = null,
    val region: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tuitionFee: BigDecimal? = null,
    val currency: String = "XAF",
    val website: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val coverImageUrl: String? = null,
    val approved: Boolean = true,
)

// ---------- Users / me ----------

data class MeDto(
    val id: Long,
    val firebaseUid: String,
    val email: String?,
    val displayName: String?,
    val role: Role,
)

data class UserDto(
    val id: Long,
    val email: String?,
    val displayName: String?,
    val role: Role,
    val active: Boolean,
    val createdAt: OffsetDateTime,
)

// ---------- Generic ----------

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
) {
    companion object {
        fun <T> of(p: org.springframework.data.domain.Page<T>) = PageResponse(
            content = p.content,
            page = p.number,
            size = p.size,
            totalElements = p.totalElements,
            totalPages = p.totalPages,
        )
    }
}

data class CompareResponse(
    val schools: List<SchoolDetail>,
    val cheapestSchoolId: Long?,
    val highestRatedSchoolId: Long?,
)

data class MetaResponse(
    val appName: String,
    val tagline: String,
    val country: String,
    val currency: String,
    val mapCenterLat: Double,
    val mapCenterLng: Double,
    val categories: List<String>,
    val firebaseEnabled: Boolean,
)
