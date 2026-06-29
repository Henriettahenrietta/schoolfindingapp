package com.schoolfinder.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun observeAll(): Flow<List<SchoolEntity>>

    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): SchoolEntity?

    @Query("SELECT COUNT(*) FROM schools")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(school: SchoolEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schools: List<SchoolEntity>)

    @Update
    suspend fun update(school: SchoolEntity)

    @Delete
    suspend fun delete(school: SchoolEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE schoolId = :schoolId AND approved = 1 ORDER BY timestamp DESC")
    fun observeApprovedForSchool(schoolId: Int): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<ReviewEntity>>

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity)

    @Update
    suspend fun update(review: ReviewEntity)

    @Delete
    suspend fun delete(review: ReviewEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT schoolId FROM favorites WHERE userEmail = :email")
    fun observeIdsForUser(email: String): Flow<List<Int>>

    @Query("SELECT * FROM favorites WHERE userEmail = :email AND schoolId = :schoolId LIMIT 1")
    suspend fun find(email: String, schoolId: Int): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userEmail = :email AND schoolId = :schoolId")
    suspend fun remove(email: String, schoolId: Int)
}
