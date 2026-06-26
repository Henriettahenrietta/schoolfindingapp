package com.schoolfinder.repository

import com.schoolfinder.domain.AppUser
import com.schoolfinder.domain.Favorite
import com.schoolfinder.domain.Review
import com.schoolfinder.domain.ReviewStatus
import com.schoolfinder.domain.School
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AppUserRepository : JpaRepository<AppUser, Long> {
    fun findByFirebaseUid(firebaseUid: String): AppUser?
}

interface SchoolRepository : JpaRepository<School, Long>, JpaSpecificationExecutor<School>

interface ProgramRepository : JpaRepository<com.schoolfinder.domain.Program, Long>

interface SchoolImageRepository : JpaRepository<com.schoolfinder.domain.SchoolImage, Long>

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findBySchoolIdAndStatusOrderByCreatedAtDesc(
        schoolId: Long,
        status: ReviewStatus,
        pageable: Pageable,
    ): Page<Review>

    fun findByStatusOrderByCreatedAtDesc(status: ReviewStatus, pageable: Pageable): Page<Review>

    fun findByUserIdAndSchoolId(userId: Long, schoolId: Long): Review?

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Review>

    @Query(
        """
        select r.school.id as schoolId, avg(r.rating) as avg, count(r) as cnt
        from Review r
        where r.status = com.schoolfinder.domain.ReviewStatus.APPROVED and r.school.id in :ids
        group by r.school.id
        """
    )
    fun ratingAggregates(@Param("ids") ids: Collection<Long>): List<RatingAgg>
}

interface RatingAgg {
    val schoolId: Long
    val avg: Double
    val cnt: Long
}

interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Favorite>
    fun findByUserIdAndSchoolId(userId: Long, schoolId: Long): Favorite?
    fun deleteByUserIdAndSchoolId(userId: Long, schoolId: Long)
    fun existsByUserIdAndSchoolId(userId: Long, schoolId: Long): Boolean
}
