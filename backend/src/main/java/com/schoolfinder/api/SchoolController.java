package com.schoolfinder.api;

import com.schoolfinder.api.Dtos.CompareResponse;
import com.schoolfinder.api.Dtos.CreateReviewRequest;
import com.schoolfinder.api.Dtos.PageResponse;
import com.schoolfinder.api.Dtos.ReviewDto;
import com.schoolfinder.api.Dtos.SchoolDetail;
import com.schoolfinder.api.Dtos.SchoolSummary;
import com.schoolfinder.domain.SchoolCategory;
import com.schoolfinder.security.CurrentUser;
import com.schoolfinder.service.ReviewService;
import com.schoolfinder.service.SchoolService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schools")
public class SchoolController {

    private final SchoolService schoolService;
    private final ReviewService reviewService;

    public SchoolController(SchoolService schoolService, ReviewService reviewService) {
        this.schoolService = schoolService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public PageResponse<SchoolSummary> search(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) SchoolCategory category,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) Double minRating,
        @RequestParam(required = false) BigDecimal maxTuition,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "name") String sort
    ) {
        String sortField = switch (sort) {
            case "tuition" -> "tuitionFee";
            case "newest" -> "createdAt";
            default -> "name";
        };
        int boundedSize = Math.min(100, Math.max(1, size));
        PageRequest pageable = PageRequest.of(page, boundedSize, Sort.by(sortField));
        return schoolService.search(q, category, city, minRating, maxTuition, pageable);
    }

    // NOTE: declared before "/{id}" so the literal path wins over the path variable.
    @GetMapping("/compare")
    public CompareResponse compare(@RequestParam List<Long> ids) {
        return schoolService.compare(ids);
    }

    @GetMapping("/{id}")
    public SchoolDetail detail(@PathVariable Long id) {
        return schoolService.detail(id);
    }

    @GetMapping("/{id}/reviews")
    public PageResponse<ReviewDto> reviews(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        int boundedSize = Math.min(100, Math.max(1, size));
        return reviewService.listForSchool(id, PageRequest.of(page, boundedSize));
    }

    @PostMapping("/{id}/reviews")
    public ReviewDto submitReview(
        @PathVariable Long id,
        @Valid @RequestBody CreateReviewRequest body
    ) {
        return reviewService.submit(id, CurrentUser.require(), body);
    }
}
