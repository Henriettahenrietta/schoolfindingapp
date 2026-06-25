package com.schoolfinder.service

import com.schoolfinder.api.ImageDto
import com.schoolfinder.api.ProgramDto
import com.schoolfinder.api.SchoolDetail
import com.schoolfinder.api.SchoolSummary
import com.schoolfinder.domain.School

/** Rating numbers for a school, rounded to one decimal place. */
data class Rating(val average: Double, val count: Long)

object SchoolMapper {

    fun toSummary(s: School, rating: Rating) = SchoolSummary(
        id = s.id!!,
        name = s.name,
        category = s.category,
        city = s.city,
        region = s.region,
        tuitionFee = s.tuitionFee,
        currency = s.currency,
        coverImageUrl = s.coverImageUrl,
        latitude = s.latitude,
        longitude = s.longitude,
        averageRating = rating.average,
        ratingCount = rating.count,
    )

    fun toDetail(s: School, rating: Rating, isFavorite: Boolean) = SchoolDetail(
        id = s.id!!,
        name = s.name,
        category = s.category,
        description = s.description,
        history = s.history,
        city = s.city,
        region = s.region,
        address = s.address,
        latitude = s.latitude,
        longitude = s.longitude,
        tuitionFee = s.tuitionFee,
        currency = s.currency,
        website = s.website,
        phone = s.phone,
        email = s.email,
        coverImageUrl = s.coverImageUrl,
        averageRating = rating.average,
        ratingCount = rating.count,
        favorite = isFavorite,
        programs = s.programs.map { ProgramDto(it.id!!, it.name, it.faculty, it.level, it.durationMonths, it.tuitionFee) },
        images = s.images.map { ImageDto(it.id!!, it.url, it.caption) },
    )
}
