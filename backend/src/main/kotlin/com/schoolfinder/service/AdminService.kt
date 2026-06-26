package com.schoolfinder.service

import com.schoolfinder.api.ImageDto
import com.schoolfinder.api.NotFoundException
import com.schoolfinder.api.PageResponse
import com.schoolfinder.api.ReviewDto
import com.schoolfinder.api.SchoolDetail
import com.schoolfinder.api.SchoolUpsertRequest
import com.schoolfinder.api.UserDto
import com.schoolfinder.domain.Review
import com.schoolfinder.domain.ReviewStatus
import com.schoolfinder.domain.Role
import com.schoolfinder.domain.School
import com.schoolfinder.domain.SchoolImage
import com.schoolfinder.repository.AppUserRepository
import com.schoolfinder.repository.ReviewRepository
import com.schoolfinder.repository.SchoolImageRepository
import com.schoolfinder.repository.SchoolRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val schools: SchoolRepository,
    private val users: AppUserRepository,
    private val reviews: ReviewRepository,
    private val images: SchoolImageRepository,
    private val cloudinary: CloudinaryService,
    private val schoolService: SchoolService,
) {

    // ----- School images (Cloudinary) -----

    @Transactional
    fun uploadSchoolImage(id: Long, bytes: ByteArray, caption: String?, setCover: Boolean): ImageDto {
        val school = schools.findById(id).orElseThrow { NotFoundException("School $id not found") }
        val url = cloudinary.upload(bytes)
        val img = images.save(SchoolImage(school = school, url = url, caption = caption))
        if (setCover) {
            school.coverImageUrl = url
            schools.save(school)
        }
        return ImageDto(img.id!!, img.url, img.caption)
    }

    @Transactional
    fun deleteSchoolImage(imageId: Long) {
        if (!images.existsById(imageId)) throw NotFoundException("Image $imageId not found")
        images.deleteById(imageId)
    }

    // ----- Schools -----

    @Transactional
    fun createSchool(req: SchoolUpsertRequest): SchoolDetail {
        val school = School(name = req.name, category = req.category).apply { applyFrom(req) }
        return schoolService.detail(schools.save(school).id!!)
    }

    @Transactional
    fun updateSchool(id: Long, req: SchoolUpsertRequest): SchoolDetail {
        val school = schools.findById(id).orElseThrow { NotFoundException("School $id not found") }
        school.name = req.name
        school.category = req.category
        school.applyFrom(req)
        schools.save(school)
        return schoolService.detail(id)
    }

    @Transactional
    fun deleteSchool(id: Long) {
        if (!schools.existsById(id)) throw NotFoundException("School $id not found")
        schools.deleteById(id)
    }

    private fun School.applyFrom(req: SchoolUpsertRequest) {
        description = req.description
        city = req.city
        region = req.region
        address = req.address
        latitude = req.latitude
        longitude = req.longitude
        tuitionFee = req.tuitionFee
        currency = req.currency
        website = req.website
        phone = req.phone
        email = req.email
        coverImageUrl = req.coverImageUrl
        approved = req.approved
    }

    // ----- Users -----

    @Transactional(readOnly = true)
    fun listUsers(): List<UserDto> = users.findAll().map {
        UserDto(it.id!!, it.email, it.displayName, it.role, it.active, it.createdAt)
    }

    @Transactional
    fun setUserActive(id: Long, active: Boolean): UserDto {
        val u = users.findById(id).orElseThrow { NotFoundException("User $id not found") }
        u.active = active
        return UserDto(u.id!!, u.email, u.displayName, u.role, u.active, u.createdAt)
    }

    @Transactional
    fun setUserRole(id: Long, role: Role): UserDto {
        val u = users.findById(id).orElseThrow { NotFoundException("User $id not found") }
        u.role = role
        return UserDto(u.id!!, u.email, u.displayName, u.role, u.active, u.createdAt)
    }

    // ----- Review moderation -----

    @Transactional(readOnly = true)
    fun pendingReviews(pageable: Pageable): PageResponse<ReviewDto> =
        PageResponse.of(reviews.findByStatusOrderByCreatedAtDesc(ReviewStatus.PENDING, pageable).map(::toReviewDto))

    @Transactional
    fun moderateReview(id: Long, status: ReviewStatus): ReviewDto {
        val review = reviews.findById(id).orElseThrow { NotFoundException("Review $id not found") }
        review.status = status
        return toReviewDto(review)
    }

    // ----- Analytics -----

    @Transactional(readOnly = true)
    fun analytics(): Map<String, Any> = mapOf(
        "totalSchools" to schools.count(),
        "totalUsers" to users.count(),
        "totalReviews" to reviews.count(),
        "schoolsByCategory" to schools.findAll().groupingBy { it.category.name }.eachCount(),
    )

    private fun toReviewDto(r: Review) = ReviewDto(
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
