package com.schoolfinder.service

import com.schoolfinder.api.ConflictException
import com.schoolfinder.api.NotFoundException
import com.schoolfinder.api.SchoolSummary
import com.schoolfinder.domain.Favorite
import com.schoolfinder.repository.AppUserRepository
import com.schoolfinder.repository.FavoriteRepository
import com.schoolfinder.repository.ReviewRepository
import com.schoolfinder.repository.SchoolRepository
import com.schoolfinder.security.CurrentUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.round

@Service
class FavoriteService(
    private val favorites: FavoriteRepository,
    private val schools: SchoolRepository,
    private val users: AppUserRepository,
    private val reviews: ReviewRepository,
) {

    @Transactional(readOnly = true)
    fun list(current: CurrentUser): List<SchoolSummary> {
        val favs = favorites.findByUserIdOrderByCreatedAtDesc(current.id)
        val schoolsList = favs.map { it.school }
        val ids = schoolsList.mapNotNull { it.id }
        val ratings = if (ids.isEmpty()) emptyMap() else reviews.ratingAggregates(ids)
            .associate { it.schoolId to Rating(round(it.avg * 10) / 10.0, it.cnt) }
        return schoolsList.map { SchoolMapper.toSummary(it, ratings[it.id] ?: Rating(0.0, 0)) }
    }

    @Transactional
    fun add(current: CurrentUser, schoolId: Long) {
        if (!schools.existsById(schoolId)) throw NotFoundException("School $schoolId not found")
        if (favorites.existsByUserIdAndSchoolId(current.id, schoolId)) {
            throw ConflictException("School already in favourites")
        }
        favorites.save(
            Favorite(
                user = users.getReferenceById(current.id),
                school = schools.getReferenceById(schoolId),
            )
        )
    }

    @Transactional
    fun remove(current: CurrentUser, schoolId: Long) {
        val fav = favorites.findByUserIdAndSchoolId(current.id, schoolId)
            ?: throw NotFoundException("Favourite not found")
        favorites.delete(fav)
    }
}
