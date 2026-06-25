package com.schoolfinder.api

import com.schoolfinder.domain.SchoolCategory
import com.schoolfinder.security.CurrentUser
import com.schoolfinder.service.ReviewService
import com.schoolfinder.service.SchoolService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/schools")
class SchoolController(
    private val schoolService: SchoolService,
    private val reviewService: ReviewService,
) {

    @GetMapping
    fun search(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) category: SchoolCategory?,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) minRating: Double?,
        @RequestParam(required = false) maxTuition: BigDecimal?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sort: String,
    ): PageResponse<SchoolSummary> {
        val sortField = when (sort) {
            "tuition" -> "tuitionFee"
            "name" -> "name"
            "newest" -> "createdAt"
            else -> "name"
        }
        val pageable = PageRequest.of(page, size.coerceIn(1, 100), Sort.by(sortField))
        return schoolService.search(q, category, city, minRating, maxTuition, pageable)
    }

    // NOTE: declared before "/{id}" so the literal path wins over the path variable.
    @GetMapping("/compare")
    fun compare(@RequestParam ids: List<Long>): CompareResponse = schoolService.compare(ids)

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): SchoolDetail = schoolService.detail(id)

    @GetMapping("/{id}/reviews")
    fun reviews(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): PageResponse<ReviewDto> =
        reviewService.listForSchool(id, PageRequest.of(page, size.coerceIn(1, 100)))

    @PostMapping("/{id}/reviews")
    fun submitReview(
        @PathVariable id: Long,
        @Valid @RequestBody body: CreateReviewRequest,
    ): ReviewDto = reviewService.submit(id, CurrentUser.require(), body)
}
