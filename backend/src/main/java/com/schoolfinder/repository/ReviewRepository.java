package com.schoolfinder.repository;

import com.schoolfinder.domain.Review;
import com.schoolfinder.domain.ReviewStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findBySchoolIdAndStatusOrderByCreatedAtDesc(Long schoolId, ReviewStatus status, Pageable pageable);

    Page<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status, Pageable pageable);

    Review findByUserIdAndSchoolId(Long userId, Long schoolId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
        select r.school.id as schoolId, avg(r.rating) as avg, count(r) as cnt
        from Review r
        where r.status = com.schoolfinder.domain.ReviewStatus.APPROVED and r.school.id in :ids
        group by r.school.id
        """)
    List<RatingAgg> ratingAggregates(@Param("ids") Collection<Long> ids);

    /** Projection for aggregated rating numbers. */
    interface RatingAgg {
        Long getSchoolId();

        Double getAvg();

        Long getCnt();
    }
}
