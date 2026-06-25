package com.schoolfinder.api

import com.schoolfinder.repository.AppUserRepository
import com.schoolfinder.security.CurrentUser
import com.schoolfinder.service.ReviewService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/me")
class MeController(
    private val users: AppUserRepository,
    private val reviewService: ReviewService,
) {

    @GetMapping
    fun me(): MeDto {
        val current = CurrentUser.require()
        val user = users.findById(current.id).orElseThrow { NotFoundException("User not found") }
        return MeDto(user.id!!, user.firebaseUid, user.email, user.displayName, user.role)
    }

    @GetMapping("/reviews")
    fun myReviews(): List<ReviewDto> = reviewService.myReviews(CurrentUser.require())

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReview(@PathVariable id: Long) = reviewService.deleteOwn(id, CurrentUser.require())
}
