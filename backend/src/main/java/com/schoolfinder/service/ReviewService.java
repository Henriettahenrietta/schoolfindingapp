package com.schoolfinder.service;

import com.schoolfinder.api.Dtos.CreateReviewRequest;
import com.schoolfinder.api.Dtos.PageResponse;
import com.schoolfinder.api.Dtos.ReviewDto;
import com.schoolfinder.api.NotFoundException;
import com.schoolfinder.domain.Review;
import com.schoolfinder.domain.ReviewStatus;
import com.schoolfinder.domain.School;
import com.schoolfinder.repository.AppUserRepository;
import com.schoolfinder.repository.ReviewRepository;
import com.schoolfinder.repository.SchoolRepository;
import com.schoolfinder.security.CurrentUser;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final SchoolRepository schools;
    private final AppUserRepository users;

    public ReviewService(ReviewRepository reviews, SchoolRepository schools, AppUserRepository users) {
        this.reviews = reviews;
        this.schools = schools;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewDto> listForSchool(Long schoolId, Pageable pageable) {
        if (!schools.existsById(schoolId)) throw new NotFoundException("School " + schoolId + " not found");
        return PageResponse.of(
            reviews.findBySchoolIdAndStatusOrderByCreatedAtDesc(schoolId, ReviewStatus.APPROVED, pageable)
                .map(this::toDto));
    }

    /** Creates the caller's review, or updates it if they have already reviewed this school. */
    @Transactional
    public ReviewDto submit(Long schoolId, CurrentUser current, CreateReviewRequest req) {
        School school = schools.findById(schoolId)
            .orElseThrow(() -> new NotFoundException("School " + schoolId + " not found"));
        Review existing = reviews.findByUserIdAndSchoolId(current.id(), schoolId);
        Review review;
        if (existing != null) {
            existing.setRating((short) req.rating());
            existing.setComment(req.comment());
            existing.setStatus(ReviewStatus.APPROVED);
            review = existing;
        } else {
            review = new Review(
                school,
                users.getReferenceById(current.id()),
                (short) req.rating(),
                req.comment(),
                ReviewStatus.APPROVED
            );
        }
        return toDto(reviews.save(review));
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> myReviews(CurrentUser current) {
        return reviews.findByUserIdOrderByCreatedAtDesc(current.id()).stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteOwn(Long reviewId, CurrentUser current) {
        Review review = reviews.findById(reviewId)
            .orElseThrow(() -> new NotFoundException("Review " + reviewId + " not found"));
        if (!review.getUser().getId().equals(current.id())) {
            throw new NotFoundException("Review " + reviewId + " not found");
        }
        reviews.delete(review);
    }

    private ReviewDto toDto(Review r) {
        String displayName = r.getUser().getDisplayName() != null ? r.getUser().getDisplayName() : r.getUser().getEmail();
        return new ReviewDto(
            r.getId(),
            r.getSchool().getId(),
            r.getSchool().getName(),
            displayName,
            r.getRating(),
            r.getComment(),
            r.getStatus(),
            r.getCreatedAt()
        );
    }
}
