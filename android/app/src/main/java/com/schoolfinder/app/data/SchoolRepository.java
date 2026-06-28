package com.schoolfinder.app.data;

import androidx.annotation.NonNull;

import com.schoolfinder.app.data.remote.ApiService;
import com.schoolfinder.app.data.remote.CompareResponse;
import com.schoolfinder.app.data.remote.CreateReviewRequest;
import com.schoolfinder.app.data.remote.Meta;
import com.schoolfinder.app.data.remote.PageResponse;
import com.schoolfinder.app.data.remote.Review;
import com.schoolfinder.app.data.remote.SchoolDetail;
import com.schoolfinder.app.data.remote.SchoolSummary;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Thin wrapper over {@link ApiService}. Retrofit delivers callbacks on the main thread,
 * so the UI can update directly in onSuccess/onError.
 */
public class SchoolRepository {

    private final ApiService api;

    public SchoolRepository(ApiService api) {
        this.api = api;
    }

    public void meta(ResultCallback<Meta> cb) {
        enqueue(api.meta(), cb);
    }

    public void search(String q, String category, String city, Double minRating,
                       Double maxTuition, String sort, ResultCallback<List<SchoolSummary>> cb) {
        String query = isBlank(q) ? null : q;
        String cityArg = isBlank(city) ? null : city;
        enqueuePage(api.searchSchools(query, category, cityArg, minRating, maxTuition, sort, 0, 50), cb);
    }

    public void school(long id, ResultCallback<SchoolDetail> cb) {
        enqueue(api.school(id), cb);
    }

    public void compare(List<Long> ids, ResultCallback<CompareResponse> cb) {
        enqueue(api.compare(join(ids)), cb);
    }

    public void reviews(long schoolId, ResultCallback<List<Review>> cb) {
        enqueuePage(api.reviews(schoolId, 0, 50), cb);
    }

    public void submitReview(long schoolId, int rating, String comment, ResultCallback<Review> cb) {
        enqueue(api.submitReview(schoolId, new CreateReviewRequest(rating, isBlank(comment) ? null : comment)), cb);
    }

    public void favorites(ResultCallback<List<SchoolSummary>> cb) {
        enqueue(api.favorites(), cb);
    }

    public void addFavorite(long schoolId, ResultCallback<Void> cb) {
        enqueue(api.addFavorite(schoolId), cb);
    }

    public void removeFavorite(long schoolId, ResultCallback<Void> cb) {
        enqueue(api.removeFavorite(schoolId), cb);
    }

    public void myReviews(ResultCallback<List<Review>> cb) {
        enqueue(api.myReviews(), cb);
    }

    // --- helpers ---

    private static <T> void enqueue(Call<T> call, ResultCallback<T> cb) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> c, @NonNull Response<T> r) {
                if (r.isSuccessful()) {
                    cb.onSuccess(r.body());
                } else {
                    cb.onError("Request failed (HTTP " + r.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> c, @NonNull Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    /** Unwraps a paged response down to its content list. */
    private static <T> void enqueuePage(Call<PageResponse<T>> call, ResultCallback<List<T>> cb) {
        call.enqueue(new Callback<PageResponse<T>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<T>> c, @NonNull Response<PageResponse<T>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    cb.onSuccess(r.body().content);
                } else {
                    cb.onError("Request failed (HTTP " + r.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<T>> c, @NonNull Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String join(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(ids.get(i));
        }
        return sb.toString();
    }
}
