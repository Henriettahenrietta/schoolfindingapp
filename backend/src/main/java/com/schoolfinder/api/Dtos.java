package com.schoolfinder.api;

import com.schoolfinder.domain.ReviewStatus;
import com.schoolfinder.domain.Role;
import com.schoolfinder.domain.SchoolCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

/** All API request/response DTOs as Java records. Import the nested ones directly, e.g.
 *  {@code import com.schoolfinder.api.Dtos.SchoolSummary;} */
public final class Dtos {

    private Dtos() {
    }

    // ---------- Schools ----------

    public record SchoolSummary(
        Long id,
        String name,
        SchoolCategory category,
        String city,
        String region,
        BigDecimal tuitionFee,
        String currency,
        String coverImageUrl,
        Double latitude,
        Double longitude,
        double averageRating,
        long ratingCount
    ) {
    }

    public record ProgramDto(
        Long id,
        String name,
        String faculty,
        String level,
        Integer durationMonths,
        BigDecimal tuitionFee
    ) {
    }

    public record ImageDto(Long id, String url, String caption) {
    }

    public record SchoolDetail(
        Long id,
        String name,
        SchoolCategory category,
        String description,
        String history,
        String city,
        String region,
        String address,
        Double latitude,
        Double longitude,
        BigDecimal tuitionFee,
        String currency,
        String website,
        String phone,
        String email,
        String coverImageUrl,
        double averageRating,
        long ratingCount,
        boolean favorite,
        List<ProgramDto> programs,
        List<ImageDto> images
    ) {
    }

    // ---------- Reviews ----------

    public record CreateReviewRequest(
        @Min(1) @Max(5) int rating,
        @Size(max = 4000) String comment
    ) {
    }

    public record ReviewDto(
        Long id,
        Long schoolId,
        String schoolName,
        String userDisplayName,
        int rating,
        String comment,
        ReviewStatus status,
        OffsetDateTime createdAt
    ) {
    }

    // ---------- Admin: schools ----------

    public record SchoolUpsertRequest(
        @NotBlank String name,
        SchoolCategory category,
        String description,
        String history,
        String city,
        String region,
        String address,
        Double latitude,
        Double longitude,
        BigDecimal tuitionFee,
        String currency,
        String website,
        String phone,
        String email,
        String coverImageUrl,
        Boolean approved
    ) {
        public SchoolUpsertRequest {
            if (currency == null) currency = "XAF";
            if (approved == null) approved = Boolean.TRUE;
        }
    }

    // ---------- Users / me ----------

    public record MeDto(Long id, String firebaseUid, String email, String displayName, Role role) {
    }

    public record UserDto(Long id, String email, String displayName, Role role, boolean active, OffsetDateTime createdAt) {
    }

    // ---------- Generic ----------

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
        public static <T> PageResponse<T> of(Page<T> p) {
            return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        }
    }

    public record CompareResponse(List<SchoolDetail> schools, Long cheapestSchoolId, Long highestRatedSchoolId) {
    }

    public record MetaResponse(
        String appName,
        String tagline,
        String country,
        String currency,
        double mapCenterLat,
        double mapCenterLng,
        List<String> categories,
        boolean firebaseEnabled
    ) {
    }

    public record ApiError(int status, String error, String message, OffsetDateTime timestamp) {
        public ApiError(int status, String error, String message) {
            this(status, error, message, OffsetDateTime.now());
        }
    }
}
