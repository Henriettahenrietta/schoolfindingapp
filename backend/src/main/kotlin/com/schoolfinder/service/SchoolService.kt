package com.schoolfinder.service

import com.schoolfinder.api.CompareResponse
import com.schoolfinder.api.NotFoundException
import com.schoolfinder.api.PageResponse
import com.schoolfinder.api.SchoolDetail
import com.schoolfinder.api.SchoolSummary
import com.schoolfinder.domain.School
import com.schoolfinder.domain.SchoolCategory
import com.schoolfinder.repository.FavoriteRepository
import com.schoolfinder.repository.ReviewRepository
import com.schoolfinder.repository.SchoolRepository
import com.schoolfinder.security.CurrentUser
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.math.round

@Service
class SchoolService(
    private val schools: SchoolRepository,
    private val reviews: ReviewRepository,
    private val favorites: FavoriteRepository,
) {

    @Transactional(readOnly = true)
    fun search(
        q: String?,
        category: SchoolCategory?,
        city: String?,
        minRating: Double?,
        maxTuition: BigDecimal?,
        pageable: Pageable,
    ): PageResponse<SchoolSummary> {
        val spec = Specification<School> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            predicates += cb.isTrue(root.get("approved"))
            if (!q.isNullOrBlank()) {
                val like = "%${q.trim().lowercase()}%"
                predicates += cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like),
                )
            }
            if (category != null) predicates += cb.equal(root.get<SchoolCategory>("category"), category)
            if (!city.isNullOrBlank()) predicates += cb.equal(cb.lower(root.get("city")), city.trim().lowercase())
            if (maxTuition != null) predicates += cb.lessThanOrEqualTo(root.get("tuitionFee"), maxTuition)
            cb.and(*predicates.toTypedArray())
        }

        val page = schools.findAll(spec, pageable)
        val ratings = ratingsFor(page.content.mapNotNull { it.id })
        var summaries = page.content.map { SchoolMapper.toSummary(it, ratings.ratingOf(it.id!!)) }

        // minRating is a post-aggregation filter (applied to the current page).
        if (minRating != null) summaries = summaries.filter { it.averageRating >= minRating }

        return PageResponse(
            content = summaries,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
        )
    }

    @Transactional(readOnly = true)
    fun detail(id: Long): SchoolDetail {
        val school = schools.findById(id).orElseThrow { NotFoundException("School $id not found") }
        val rating = ratingsFor(listOf(id)).ratingOf(id)
        return SchoolMapper.toDetail(school, rating, isFavorite(id))
    }

    @Transactional(readOnly = true)
    fun compare(ids: List<Long>): CompareResponse {
        if (ids.size !in 2..4) throw com.schoolfinder.api.BadRequestException("Compare between 2 and 4 schools")
        val found = schools.findAllById(ids)
        if (found.size != ids.size) throw NotFoundException("One or more schools not found")
        // Preserve the requested order.
        val byId = found.associateBy { it.id }
        val ordered = ids.mapNotNull { byId[it] }
        val ratings = ratingsFor(ids)

        val details = ordered.map { SchoolMapper.toDetail(it, ratings.ratingOf(it.id!!), isFavorite(it.id!!)) }
        val cheapest = details.filter { it.tuitionFee != null }.minByOrNull { it.tuitionFee!! }?.id
        val highestRated = details.maxByOrNull { it.averageRating }?.takeIf { it.ratingCount > 0 }?.id

        return CompareResponse(details, cheapest, highestRated)
    }

    private fun isFavorite(schoolId: Long): Boolean {
        val user = CurrentUser.current() ?: return false
        return favorites.existsByUserIdAndSchoolId(user.id, schoolId)
    }

    private fun ratingsFor(ids: List<Long>): RatingLookup {
        if (ids.isEmpty()) return RatingLookup(emptyMap())
        val map = reviews.ratingAggregates(ids).associate {
            it.schoolId to Rating(round(it.avg * 10) / 10.0, it.cnt)
        }
        return RatingLookup(map)
    }

    private class RatingLookup(private val map: Map<Long, Rating>) {
        fun ratingOf(id: Long): Rating = map[id] ?: Rating(0.0, 0)
    }
}
