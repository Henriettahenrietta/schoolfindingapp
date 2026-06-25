package com.schoolfinder.app.data

import com.schoolfinder.app.data.remote.ApiService
import com.schoolfinder.app.data.remote.CompareResponse
import com.schoolfinder.app.data.remote.CreateReviewRequest
import com.schoolfinder.app.data.remote.Meta
import com.schoolfinder.app.data.remote.Review
import com.schoolfinder.app.data.remote.SchoolDetail
import com.schoolfinder.app.data.remote.SchoolSummary

/** Thin wrapper over [ApiService] returning [Result] so the UI can handle errors uniformly. */
class SchoolRepository(private val api: ApiService) {

    suspend fun meta(): Result<Meta> = runCatching { api.meta() }

    suspend fun search(
        q: String?,
        category: String?,
        city: String?,
        minRating: Double?,
        maxTuition: Double?,
        sort: String,
    ): Result<List<SchoolSummary>> = runCatching {
        api.searchSchools(
            q = q?.ifBlank { null },
            category = category,
            city = city?.ifBlank { null },
            minRating = minRating,
            maxTuition = maxTuition,
            sort = sort,
            size = 50,
        ).content
    }

    suspend fun school(id: Long): Result<SchoolDetail> = runCatching { api.school(id) }

    suspend fun compare(ids: List<Long>): Result<CompareResponse> =
        runCatching { api.compare(ids.joinToString(",")) }

    suspend fun reviews(schoolId: Long): Result<List<Review>> =
        runCatching { api.reviews(schoolId).content }

    suspend fun submitReview(schoolId: Long, rating: Int, comment: String?): Result<Review> =
        runCatching { api.submitReview(schoolId, CreateReviewRequest(rating, comment?.ifBlank { null })) }

    suspend fun favorites(): Result<List<SchoolSummary>> = runCatching { api.favorites() }

    suspend fun addFavorite(schoolId: Long): Result<Unit> = runCatching { api.addFavorite(schoolId) }

    suspend fun removeFavorite(schoolId: Long): Result<Unit> = runCatching { api.removeFavorite(schoolId) }

    suspend fun myReviews(): Result<List<Review>> = runCatching { api.myReviews() }
}
