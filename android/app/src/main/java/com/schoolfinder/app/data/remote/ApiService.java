package com.schoolfinder.app.data.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/v1/meta")
    Call<Meta> meta();

    @GET("api/v1/schools")
    Call<PageResponse<SchoolSummary>> searchSchools(
            @Query("q") String q,
            @Query("category") String category,
            @Query("city") String city,
            @Query("minRating") Double minRating,
            @Query("maxTuition") Double maxTuition,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("size") int size);

    @GET("api/v1/schools/{id}")
    Call<SchoolDetail> school(@Path("id") long id);

    @GET("api/v1/schools/compare")
    Call<CompareResponse> compare(@Query("ids") String ids);

    @GET("api/v1/schools/{id}/reviews")
    Call<PageResponse<Review>> reviews(
            @Path("id") long id,
            @Query("page") int page,
            @Query("size") int size);

    @POST("api/v1/schools/{id}/reviews")
    Call<Review> submitReview(@Path("id") long id, @Body CreateReviewRequest body);

    // Favorites
    @GET("api/v1/favorites")
    Call<List<SchoolSummary>> favorites();

    @POST("api/v1/favorites/{schoolId}")
    Call<Void> addFavorite(@Path("schoolId") long schoolId);

    @DELETE("api/v1/favorites/{schoolId}")
    Call<Void> removeFavorite(@Path("schoolId") long schoolId);

    // Me
    @GET("api/v1/me")
    Call<Me> me();

    @GET("api/v1/me/reviews")
    Call<List<Review>> myReviews();
}
