package com.schoolfinder.api

import com.schoolfinder.domain.ReviewStatus
import com.schoolfinder.domain.Role
import com.schoolfinder.service.AdminService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(private val admin: AdminService) {

    // ----- Schools -----

    @PostMapping("/schools")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSchool(@Valid @RequestBody body: SchoolUpsertRequest): SchoolDetail = admin.createSchool(body)

    @PutMapping("/schools/{id}")
    fun updateSchool(@PathVariable id: Long, @Valid @RequestBody body: SchoolUpsertRequest): SchoolDetail =
        admin.updateSchool(id, body)

    @DeleteMapping("/schools/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSchool(@PathVariable id: Long) = admin.deleteSchool(id)

    // ----- Users -----

    @GetMapping("/users")
    fun users(): List<UserDto> = admin.listUsers()

    @PutMapping("/users/{id}/active")
    fun setActive(@PathVariable id: Long, @RequestParam active: Boolean): UserDto =
        admin.setUserActive(id, active)

    @PutMapping("/users/{id}/role")
    fun setRole(@PathVariable id: Long, @RequestParam role: Role): UserDto =
        admin.setUserRole(id, role)

    // ----- Review moderation -----

    @GetMapping("/reviews/pending")
    fun pending(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): PageResponse<ReviewDto> = admin.pendingReviews(PageRequest.of(page, size.coerceIn(1, 100)))

    @PutMapping("/reviews/{id}/status")
    fun moderate(@PathVariable id: Long, @RequestParam status: ReviewStatus): ReviewDto =
        admin.moderateReview(id, status)

    // ----- Analytics -----

    @GetMapping("/analytics")
    fun analytics(): Map<String, Any> = admin.analytics()
}
