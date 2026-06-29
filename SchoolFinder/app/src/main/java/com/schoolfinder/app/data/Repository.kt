package com.schoolfinder.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Single access point to the local data layer. In a production build this would
 * call the Spring Boot REST APIs described in the SRS; here it talks to Room.
 */
class Repository(
    private val schoolDao: SchoolDao,
    private val userDao: UserDao,
    private val reviewDao: ReviewDao,
    private val favoriteDao: FavoriteDao
) {
    // ---- Schools ----
    val schools: Flow<List<SchoolEntity>> = schoolDao.observeAll()
    suspend fun getSchool(id: Int): SchoolEntity? = schoolDao.getById(id)
    suspend fun addSchool(school: SchoolEntity) = schoolDao.insert(school)
    suspend fun updateSchool(school: SchoolEntity) = schoolDao.update(school)
    suspend fun deleteSchool(school: SchoolEntity) = schoolDao.delete(school)
    suspend fun schoolCount(): Int = schoolDao.count()

    // ---- Users / Auth ----
    val users: Flow<List<UserEntity>> = userDao.observeAll()
    suspend fun login(email: String, password: String): UserEntity? =
        userDao.login(email.trim().lowercase(), password)

    suspend fun emailExists(email: String): Boolean =
        userDao.getByEmail(email.trim().lowercase()) != null

    suspend fun register(name: String, email: String, password: String): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        if (emailExists(cleanEmail)) {
            return Result.failure(IllegalStateException("An account with this email already exists."))
        }
        val user = UserEntity(
            name = name.trim(),
            email = cleanEmail,
            password = password,
            role = "STUDENT"
        )
        val id = userDao.insert(user)
        return Result.success(user.copy(id = id.toInt()))
    }

    // ---- Reviews ----
    fun reviewsForSchool(schoolId: Int): Flow<List<ReviewEntity>> =
        reviewDao.observeApprovedForSchool(schoolId)

    val allReviews: Flow<List<ReviewEntity>> = reviewDao.observeAll()
    suspend fun addReview(review: ReviewEntity) = reviewDao.insert(review)
    suspend fun updateReview(review: ReviewEntity) = reviewDao.update(review)
    suspend fun deleteReview(review: ReviewEntity) = reviewDao.delete(review)

    // ---- Favorites ----
    fun favoriteIds(userEmail: String): Flow<List<Int>> =
        favoriteDao.observeIdsForUser(userEmail)

    suspend fun toggleFavorite(userEmail: String, schoolId: Int) {
        val existing = favoriteDao.find(userEmail, schoolId)
        if (existing == null) {
            favoriteDao.insert(FavoriteEntity(userEmail = userEmail, schoolId = schoolId))
        } else {
            favoriteDao.remove(userEmail, schoolId)
        }
    }
}
