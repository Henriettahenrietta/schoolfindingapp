package com.schoolfinder.app.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/v1/meta")
    suspend fun meta(): Meta

    @GET("api/v1/schools")
    suspend fun searchSchools(
        @Query("q") q: String? = null,
        @Query("category") category: String? = null,
        @Query("city") city: String? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("maxTuition") maxTuition: Double? = null,
        @Query("sort") sort: String = "name",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PageResponse<SchoolSummary>

    @GET("api/v1/schools/{id}")
    suspend fun school(@Path("id") id: Long): SchoolDetail

    @GET("api/v1/schools/compare")
    suspend fun compare(@Query("ids") ids: String): CompareResponse

    @GET("api/v1/schools/{id}/reviews")
    suspend fun reviews(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
    ): PageResponse<Review>

    @POST("api/v1/schools/{id}/reviews")
    suspend fun submitReview(@Path("id") id: Long, @Body body: CreateReviewRequest): Review

    // Favorites
    @GET("api/v1/favorites")
    suspend fun favorites(): List<SchoolSummary>

    @POST("api/v1/favorites/{schoolId}")
    suspend fun addFavorite(@Path("schoolId") schoolId: Long)

    @DELETE("api/v1/favorites/{schoolId}")
    suspend fun removeFavorite(@Path("schoolId") schoolId: Long)

    // Me
    @GET("api/v1/me")
    suspend fun me(): Me

    @GET("api/v1/me/reviews")
    suspend fun myReviews(): List<Review>
}
