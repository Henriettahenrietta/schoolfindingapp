package com.schoolfinder.service

import com.schoolfinder.api.CreateReviewRequest
import com.schoolfinder.api.NotFoundException
import com.schoolfinder.api.PageResponse
import com.schoolfinder.api.ReviewDto
import com.schoolfinder.domain.Review
import com.schoolfinder.domain.ReviewStatus
import com.schoolfinder.repository.AppUserRepository
import com.schoolfinder.repository.ReviewRepository
import com.schoolfinder.repository.SchoolRepository
import com.schoolfinder.security.CurrentUser
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviews: ReviewRepository,
    private val schools: SchoolRepository,
    private val users: AppUserRepository,
) {

    @Transactional(readOnly = true)
    fun listForSchool(schoolId: Long, pageable: Pageable): PageResponse<ReviewDto> {
        if (!schools.existsById(schoolId)) throw NotFoundException("School $schoolId not found")
        val page = reviews.findBySchoolIdAndStatusOrderByCreatedAtDesc(schoolId, ReviewStatus.APPROVED, pageable)
        return PageResponse.of(page.map(::toDto))
    }

    /** Creates the caller's review, or updates it if they have already reviewed this school. */
    @Transactional
    fun submit(schoolId: Long, current: CurrentUser, req: CreateReviewRequest): ReviewDto {
        val school = schools.findById(schoolId).orElseThrow { NotFoundException("School $schoolId not found") }
        val existing = reviews.findByUserIdAndSchoolId(current.id, schoolId)
        val review = if (existing != null) {
            existing.rating = req.rating.toShort()
            existing.comment = req.comment
            existing.status = ReviewStatus.APPROVED
            existing
        } else {
            Review(
                school = school,
                user = users.getReferenceById(current.id),
                rating = req.rating.toShort(),
                comment = req.comment,
                status = ReviewStatus.APPROVED,
            )
        }
        return toDto(reviews.save(review))
    }

    @Transactional(readOnly = true)
    fun myReviews(current: CurrentUser): List<ReviewDto> =
        reviews.findByUserIdOrderByCreatedAtDesc(current.id).map(::toDto)

    @Transactional
    fun deleteOwn(reviewId: Long, current: CurrentUser) {
        val review = reviews.findById(reviewId).orElseThrow { NotFoundException("Review $reviewId not found") }
        if (review.user.id != current.id) throw NotFoundException("Review $reviewId not found")
        reviews.delete(review)
    }

    private fun toDto(r: Review) = ReviewDto(
        id = r.id!!,
        schoolId = r.school.id!!,
        schoolName = r.school.name,
        userDisplayName = r.user.displayName ?: r.user.email,
        rating = r.rating.toInt(),
        comment = r.comment,
        status = r.status,
        createdAt = r.createdAt,
    )
}
